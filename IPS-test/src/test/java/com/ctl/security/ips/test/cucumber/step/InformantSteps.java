package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.common.domain.mongo.*;
import com.ctl.security.ips.client.NotificationClient;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import com.ctl.security.ips.test.cucumber.adapter.EventAdapter;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Created by Sean Robb on 3/30/2015.
 */
public class InformantSteps {

    public static final int MAX_ATTEMPTS = 30;

    @Autowired
    EventAdapter eventAdapter;

    @Autowired
    private ConfigurationItemClient configurationItemClient;

    @Autowired
    private ClcAuthenticationComponent clcAuthenticationComponent;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private Environment environment;

    @Autowired
    private WaitComponent waitComponent;

    @Autowired
    private WireMockComponent wireMockComponent;


    @Value("${${spring.profiles.active:local}.ips.test.port}")
    int destinationPort;

    @Value("${${spring.profiles.active:local}.ips.test.host}")
    private String destinationHostName;

    @Value("${${spring.profiles.active:local}.ips.test.retryWaitTime}")
    private Integer retryWaitTime;

    private String urlPath ="/someAddress";

    private String bearerToken;

    String accountId = ClcAuthenticationComponent.VALID_AA;
    String hostName = "server.host.name." + System.currentTimeMillis();

    @Value("${${spring.profiles.active:local}.ips.maxRetryAttempts}")
    private Integer maxRetryAttempts;
    private WireMockServer wireMockServer;

    @Given("^a DSM agent is running on configuration item$")
    public void a_DSM_agent_is_running_on_configuration_item() throws Throwable {
        ConfigurationItem configurationItem = createAndConfigureConfigurationItem(accountId, hostName);
        String destinationURL="http://" + destinationHostName + ":" + destinationPort + urlPath;
        createAndSetNotificationDestination(destinationURL, configurationItem);

        wireMockServer = wireMockComponent.createWireMockServer(destinationHostName, destinationPort);
        wireMockComponent.createWireMockServerStub(urlPath, HttpStatus.SC_OK);
    }

    @When("^events are posted to DSM$")
    public void events_are_posted_to_DSM() throws Throwable {
        //Creates a FirewallEvent
        FirewallEvent firewallEvent = new FirewallEvent();
        firewallEvent.setReason("An FirewallEvent Reason");
        firewallEvent.setHostName(hostName);

        eventAdapter.triggerEvent(Arrays.asList(firewallEvent));
    }

    @Then("^the events are posted to the correct notification destination$")
    public void the_events_are_posted_to_the_correct_notification_destination() throws Throwable {

        waitForPostRequests(1, urlPath);

        //TODO Create Test for TS,QA,PROD
        String activeProfiles = environment.getActiveProfiles()[0];

        if(activeProfiles.equalsIgnoreCase("local")){//||activeProfiles.equalsIgnoreCase("dev")) {
            wireMockServer.verify(postRequestedFor(urlEqualTo(urlPath)));
        }

        wireMockServer.stop();

    }

    private void waitForPostRequests(int requests, String address) {
        int currentAttempts = 0;
        List<LoggedRequest> loggedRequests;
        do {
            loggedRequests = wireMockServer.findAll(postRequestedFor(urlEqualTo(address)));
            waitComponent.sleep(retryWaitTime, currentAttempts);
            currentAttempts++;
        } while (loggedRequests.size() < requests && currentAttempts < MAX_ATTEMPTS);
    }

    private ConfigurationItem createAndConfigureConfigurationItem(String accountId, String hostName) {
        Account account = new Account()
                .setCustomerAccountId(accountId);

        ConfigurationItem configurationItem = new ConfigurationItem()
                .setAccount(account)
                .setHostName(hostName);

        configurationItemClient.createConfigurationItem(configurationItem);
        return configurationItem;
    }

    private void createAndSetNotificationDestination(String destinationURL, ConfigurationItem configurationItem) {
        NotificationDestinationBean notificationDestinationBean;
        notificationDestinationBean = getNotificationDestinationBean(destinationURL, configurationItem);

        bearerToken = clcAuthenticationComponent.authenticate();

        notificationClient.updateNotificationDestination(notificationDestinationBean, bearerToken);

        waitForNotificationDestinationUpdate(configurationItem);
    }

    private NotificationDestinationBean getNotificationDestinationBean(String destinationURL, ConfigurationItem configurationItem) {
        NotificationDestination notificationDestination = new NotificationDestination();
        notificationDestination.setEmailAddress("My.Test.Email@Testing.Test");
        notificationDestination.setIntervalCode(NotificationDestinationInterval.DAILY);
        notificationDestination.setTypeCode(NotificationDestinationType.WEBHOOK);
        notificationDestination.setUrl(destinationURL);

        return new NotificationDestinationBean(configurationItem.getHostName(),
                configurationItem.getAccount().getCustomerAccountId(),
                Arrays.asList(notificationDestination));
    }

    private void waitForNotificationDestinationUpdate(ConfigurationItem configurationItem) {
        List<NotificationDestination> notificationDestinations = null;

        int currentAttempts = 0;

        while (currentAttempts < MAX_ATTEMPTS && notificationDestinations == null) {
            notificationDestinations = configurationItem
                    .getAccount()
                    .getNotificationDestinations();

            waitComponent.sleep(retryWaitTime, currentAttempts);
            currentAttempts++;
        }
    }


}
