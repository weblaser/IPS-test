package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.common.domain.mongo.*;
import com.ctl.security.ips.client.EventClient;
import com.ctl.security.ips.client.NotificationClient;
import com.ctl.security.ips.common.domain.Event;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Created by sean.robb on 3/10/2015.
 */
public class EventSteps {
    public static final String SOME_VALID_ADDRESS = "/someValidAddress";
    public static final String SOME_INVALID_ADDRESS = "/someInvalidAddress";
    private static final String VALID = "valid";
    public static final int MAX_ATTEMPTS = 30;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private EventClient eventClient;

    @Autowired
    private ConfigurationItemClient configurationItemClient;

    @Autowired
    private ClcAuthenticationComponent clcAuthenticationComponent;

    @Value("${${spring.profiles.active:local}.ips.maxRetryAttempts}")
    private Integer maxRetryAttempts;

    private EventBean eventBean;
    private String bearerToken;

    WireMockServer wireMockServer;

    @Value("${${spring.profiles.active:local}.ips.test.port}")
    int destinationPort;

    @Value("${${spring.profiles.active:local}.ips.test.host}")
    String destinationHostName;

    String accountId = ClcAuthenticationComponent.VALID_AA;
    String hostName = "server.host.name." + System.currentTimeMillis();

    @Given("^an event occurs for a valid configuration item$")
    public void an_event_occurs(){
        createEventBean(accountId, hostName);
        createAndConfigureConfigurationItem(accountId, hostName);
    }

    @Given("^the notification destination is (valid|invalid)$")
    public void the_notification_destination_is_validity(String validity){
        if (VALID.equalsIgnoreCase(validity)) {
            createAndSetNotificationDestination(destinationHostName,destinationPort, SOME_VALID_ADDRESS);
        } else {
            createAndSetNotificationDestination(destinationHostName, destinationPort, SOME_INVALID_ADDRESS);
        }

        createWireMockServer(destinationPort, destinationHostName);

        createWireMockServerStub(SOME_VALID_ADDRESS, HttpStatus.SC_OK);
        createWireMockServerStub(SOME_INVALID_ADDRESS, HttpStatus.SC_BAD_REQUEST);
    }

    @When("^the event notification is posted to the events endpoint$")
    public void the_event_notification_is_posted_to_the_events_endpoint() {
        eventClient.notify(eventBean, bearerToken);
    }

    @Then("^the event information is sent to the correct URL$")
    public void the_event_information_is_sent_to_the_correct_URL() {
        waitForPostRequests(1, SOME_VALID_ADDRESS);

        verify(postRequestedFor(urlEqualTo(SOME_VALID_ADDRESS)));

        testCleanUpAndStopWireMock();
    }

    @Then("^the event information is attempted to be sent to the URL multiple times$")
    public void the_event_information_is_attempted_to_be_sent_to_the_URL_multiple_times() {
        waitForPostRequests(maxRetryAttempts, SOME_VALID_ADDRESS);

        verify(maxRetryAttempts, postRequestedFor(urlEqualTo(SOME_INVALID_ADDRESS)));

        testCleanUpAndStopWireMock();
    }

    private void createWireMockServer(int destinationPort, String destinationHostName) {
        wireMockServer = new WireMockServer(destinationPort);
        configureFor(destinationHostName, destinationPort);
        wireMockServer.start();
    }

    private void createWireMockServerStub(String notificationUrlPath, int httpStatus) {
        stubFor(post(urlPathEqualTo(notificationUrlPath))
                .willReturn(aResponse()
                        .withStatus(httpStatus)));
    }

    private void createAndConfigureConfigurationItem(String accountId, String hostName){
        Account account = new Account()
                .setCustomerAccountId(accountId);

        ConfigurationItem configurationItem = new ConfigurationItem()
                .setAccount(account)
                .setHostName(hostName);

        configurationItemClient.createConfigurationItem(configurationItem);
    }

    private void createAndSetNotificationDestination(String destinationHostName, Integer destinationPort, String path){
        NotificationDestination notificationDestination = new NotificationDestination();
        notificationDestination.setEmailAddress("My.Test.Email@Testing.Test");
        notificationDestination.setIntervalCode(NotificationDestinationInterval.DAILY);
        notificationDestination.setTypeCode(NotificationDestinationType.WEBHOOK);
        notificationDestination.setUrl("http://" + destinationHostName + ":" + destinationPort + path);

        NotificationDestinationBean notificationDestinationBean = new NotificationDestinationBean(hostName,
                accountId,
                Arrays.asList(notificationDestination));

        bearerToken = clcAuthenticationComponent.authenticate();

        notificationClient.updateNotificationDestination(notificationDestinationBean, bearerToken);

        waitForNotificationDestinationUpdate();
    }

    private void waitForNotificationDestinationUpdate(){
        List<NotificationDestination> notificationDestinations = null;
        ConfigurationItem configurationItem = configurationItemClient
                .getConfigurationItem(hostName, accountId)
                .getContent();

        int currentAttempts = 0;

        while (currentAttempts < MAX_ATTEMPTS && notificationDestinations == null) {
            notificationDestinations = configurationItem
                    .getAccount()
                    .getNotificationDestinations();

            sleep(1000);
            currentAttempts++;
        }
    }

    private void sleep(int amount) {
        try {Thread.sleep(amount);} catch (Exception e) {}
    }

    private void createEventBean(String accountId, String hostName) {
        //Creates an event
        Event event = new Event();
        event.setMessage("An Event Has Happened");

        //Sets event bean with correct information
        eventBean = new EventBean(hostName, accountId, event);
    }

    private void waitForPostRequests(int requests, String address) {
        int currentAttempts = 0;
        List<LoggedRequest> loggedRequests;
        do {
            loggedRequests = findAll(postRequestedFor(urlEqualTo(address)));
            sleep(1000);
            currentAttempts++;
        } while (loggedRequests.size() < requests && currentAttempts < MAX_ATTEMPTS);
    }

    private void testCleanUpAndStopWireMock() {
        wireMockServer.stop();
        ConfigurationItem configurationItem = configurationItemClient
                .getConfigurationItem(hostName, accountId)
                .getContent();
        configurationItemClient.deleteConfigurationItem(configurationItem.getId());
    }

}
