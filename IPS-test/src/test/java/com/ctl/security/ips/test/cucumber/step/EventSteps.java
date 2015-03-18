package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.common.domain.mongo.*;
import com.ctl.security.ips.client.EventClient;
import com.ctl.security.ips.client.NotificationClient;
import com.ctl.security.ips.common.domain.Event;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.HttpStatus;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by sean.robb on 3/10/2015.
 */
public class EventSteps {

    public static final String SOME_VALID_ADDRESS = "/someValidAddress";

    public static final String SOME_INVALID_ADDRESS = "/someInvalidAddress";

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
    public static final int MAX_ATTEMPTS = 30;
    WireMockServer wireMockServer;
    private Exception exception;

    @Value("${${spring.profiles.active:local}.ips.test.port}")
    int destinationPort;

    @Value("${${spring.profiles.active:local}.ips.test.host}")
    String destinationHostName;

    String accountId = ClcAuthenticationComponent.VALID_AA;
    String hostName = "server.host.name." + System.currentTimeMillis();

    @Given("^an event occurs$")
    public void an_event_occurs() throws Throwable{
        createEventBean(accountId, hostName);
        createAndConfigureConfigurationItem(accountId,hostName);
    }

    @Given("^the notification destination is invalid$")
    public void the_notification_destination_is_invalid() throws Throwable{
        createAndSetNotificationDestination(destinationHostName,destinationPort,SOME_INVALID_ADDRESS,accountId,hostName);
        createAndSetupWireMockServer(SOME_INVALID_ADDRESS, destinationPort, destinationHostName, HttpStatus.SC_BAD_REQUEST);
        exception=null;
    }

    @Given("^the notification destination is valid$")
    public void the_notification_destination_is_valid() throws Throwable{
        createAndSetNotificationDestination(destinationHostName, destinationPort, SOME_VALID_ADDRESS, accountId, hostName);
        createAndSetupWireMockServer(SOME_VALID_ADDRESS, destinationPort, destinationHostName, HttpStatus.SC_OK);
        exception=null;
    }

    @When("^the event notification is posted to the events endpoint$")
    public void the_event_notification_is_posted_to_the_events_endpoint(){
        try {
            eventClient.notify(eventBean, bearerToken);
        }
        catch (Exception e){
            exception = e;
        }
//        try {Thread.sleep(10000);} catch (Exception e) {}
    }

    @Then("^the event information is sent to the correct URL$")
    public void the_event_information_is_sent_to_the_correct_URL(){

        try {
            List<LoggedRequest> loggedRequests;
            do{
                loggedRequests = findAll(postRequestedFor(urlEqualTo(SOME_VALID_ADDRESS)));
            }while(loggedRequests.isEmpty());

            verify(postRequestedFor(urlEqualTo(SOME_VALID_ADDRESS)));
        }catch (Exception e)
        {

        }


        assertNull(exception);

        stopWireMockServer();

        //cleanup
        ConfigurationItem configurationItem=configurationItemClient.getConfigurationItem(hostName,accountId).getContent();
        configurationItemClient.deleteConfigurationItem(configurationItem.getId());
    }

    @Then("^the event information is attempted to be sent to the URL multiple times$")
    public void the_event_information_is_attempted_to_be_sent_to_the_URL_multiple_times(){
        verify(maxRetryAttempts,postRequestedFor(urlEqualTo(SOME_INVALID_ADDRESS)));



        assertNull(exception);

        stopWireMockServer();

        //cleanup
        ConfigurationItem configurationItem=configurationItemClient.getConfigurationItem(hostName,accountId).getContent();
        configurationItemClient.deleteConfigurationItem(configurationItem.getId());
    }

    private void stopWireMockServer() {
        //stops the wire mock server
        wireMockServer.stop();
    }

    private void createAndSetupWireMockServer(String notificationUrlPath, int destinationPort, String destinationHostName, int httpStatus) {
        wireMockServer= new WireMockServer(destinationPort);
        configureFor(destinationHostName, destinationPort);
        wireMockServer.start();
        stubFor(post(urlPathEqualTo(notificationUrlPath))
                .willReturn(aResponse()
                        .withStatus(httpStatus)));
    }

    private void createAndConfigureConfigurationItem(String accountId, String hostName)throws Throwable {
        Account account = new Account()
                .setCustomerAccountId(accountId);

        ConfigurationItem configurationItem = new ConfigurationItem()
                .setAccount(account)
                .setHostName(hostName);

        configurationItemClient.createConfigurationItem(configurationItem);
    }

    private void createAndSetNotificationDestination(String destinationHostName,Integer destinationPort,String urlPath,String accountId, String hostName) throws Throwable{
        NotificationDestination notificationDestination = new NotificationDestination();
        notificationDestination.setEmailAddress("My.Test.Email@Testing.Test");
        notificationDestination.setIntervalCode(NotificationDestinationInterval.DAILY);
        notificationDestination.setTypeCode(NotificationDestinationType.WEBHOOK);
        notificationDestination.setUrl("http://" + destinationHostName + ":" + destinationPort + urlPath);

        List<NotificationDestination> notificationDestinations = null;
        NotificationDestinationBean notificationDestinationBean;
        notificationDestinationBean = new NotificationDestinationBean(hostName,
                accountId,
                Arrays.asList(notificationDestination));

        bearerToken = clcAuthenticationComponent.authenticate();

        //Sets the Bean Notification Destination Information
        notificationClient.updateNotificationDestination(notificationDestinationBean, bearerToken);

        ConfigurationItem configurationItem= configurationItemClient.getConfigurationItem(hostName,accountId).getContent();
        //Waits for Notification Destinations to be set (Since Active MQ)
        int currentAttempts = 0;
        int maxAttempts = MAX_ATTEMPTS;
        while(currentAttempts < maxAttempts && notificationDestinations == null){
            notificationDestinations = configurationItem.getAccount().getNotificationDestinations();
            Thread.sleep(1000);
            currentAttempts++;
        }
    }

    private void createEventBean(String accountId, String hostName) {
        //Creates an event
        Event event=new Event();
        event.setMessage("An Event Has Happened");

        //Sets event bean with correct information
        eventBean=new EventBean(hostName,accountId,event);
    }

}
