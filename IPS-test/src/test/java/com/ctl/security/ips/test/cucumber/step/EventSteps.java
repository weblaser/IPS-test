package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.common.domain.mongo.*;
import com.ctl.security.ips.client.EventClient;
import com.ctl.security.ips.client.NotificationClient;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
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

    @Autowired
    private WaitComponent waitComponent;

    @Autowired
    private WireMockComponent wireMockComponent;

    @Value("${${spring.profiles.active:local}.ips.test.retryWaitTime}")
    private Integer retryWaitTime;

    @Value("${${spring.profiles.active:local}.ips.maxRetryAttempts}")
    private Integer maxRetryAttempts;

    private EventBean eventBean;
    private String bearerToken;

    private WireMockServer wireMockServer;

    @Value("${${spring.profiles.active:local}.ips.test.port}")
    private int destinationPort;

    @Value("${${spring.profiles.active:local}.ips.test.host}")
    private String destinationHostName;

    private String accountId = ClcAuthenticationComponent.VALID_AA;
    private String hostName = "server.host.name." + System.currentTimeMillis();

    @Given("^an event occurs for a valid configuration item$")
    public void an_event_occurs() {
        createEventBean(accountId, hostName);
        createAndConfigureConfigurationItem(accountId, hostName);
    }

    @Given("^the notification destination is (valid|invalid)$")
    public void the_notification_destination_is_validity(String validity) {
        if (VALID.equalsIgnoreCase(validity)) {
            createAndSetNotificationDestination(destinationHostName, destinationPort, SOME_VALID_ADDRESS);
        } else {
            createAndSetNotificationDestination(destinationHostName, destinationPort, SOME_INVALID_ADDRESS);
        }

        wireMockServer = wireMockComponent.createWireMockServer(destinationHostName, destinationPort);

        wireMockComponent.createWireMockServerStub(SOME_VALID_ADDRESS, HttpStatus.SC_OK);

        wireMockComponent.createWireMockServerStub(SOME_INVALID_ADDRESS, HttpStatus.SC_BAD_REQUEST);
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
        waitForPostRequests(maxRetryAttempts, SOME_INVALID_ADDRESS);

        verify(maxRetryAttempts, postRequestedFor(urlEqualTo(SOME_INVALID_ADDRESS)));

        testCleanUpAndStopWireMock();
    }


    @Given("^an event has occurred$")
    public void an_event_has_occurred() throws Throwable {

    }

    @When("^the notification executes$")
    public void the_notification_executes() throws Throwable {

    }

    @Then("^the notification is persisted in the product user activity document$")
    public void the_notification_is_persisted_in_the_product_user_activity_document() throws Throwable {

    }

    private void createAndConfigureConfigurationItem(String accountId, String hostName) {
        Account account = new Account()
                .setCustomerAccountId(accountId);

        ConfigurationItem configurationItem = new ConfigurationItem()
                .setAccount(account)
                .setHostName(hostName);

        configurationItemClient.createConfigurationItem(configurationItem);
    }

    private void createAndSetNotificationDestination(String destinationHostName, Integer destinationPort, String path) {

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

    private void waitForNotificationDestinationUpdate() {

        List<NotificationDestination> notificationDestinations = null;

        int currentAttempts = 0;

        while (currentAttempts < MAX_ATTEMPTS && notificationDestinations == null) {

            ConfigurationItem configurationItem = configurationItemClient
                    .getConfigurationItem(hostName, accountId)
                    .getContent();

            notificationDestinations = configurationItem
                    .getAccount()
                    .getNotificationDestinations();

            waitComponent.sleep(retryWaitTime, currentAttempts);
            currentAttempts++;
        }
    }

    private void createEventBean(String accountId, String hostName) {
        //Creates an event
        FirewallEvent event = new FirewallEvent();
        event.setReason("An FirewallEvent Reason");
        event.setHostName("An FirewallEvent Host");

        //Sets event bean with correct information
        eventBean = new EventBean(hostName, accountId, event);
    }

    private void waitForPostRequests(int requests, String address) {
        int currentAttempts = 0;
        List<LoggedRequest> loggedRequests;
        do {
            loggedRequests = findAll(postRequestedFor(urlEqualTo(address)));

            waitComponent.sleep(retryWaitTime, currentAttempts);
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
