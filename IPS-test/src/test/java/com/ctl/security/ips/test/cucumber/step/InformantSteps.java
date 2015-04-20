package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.clc.client.common.domain.ClcAuthenticationResponse;
import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.cmdb.UserClient;
import com.ctl.security.data.client.domain.user.UserResource;
import com.ctl.security.data.common.domain.mongo.*;
import com.ctl.security.ips.client.NotificationClient;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import com.ctl.security.ips.test.cucumber.adapter.EventAdapter;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Created by Sean Robb on 3/30/2015.
 */
public class InformantSteps {

    public static final int MAX_ATTEMPTS = 1000;

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
    private UserClient userClient;

    @Autowired
    private WireMockServer notificationDestinationWireMockServer;

    @Value("${${spring.profiles.active:local}.ips.test.port}")
    private int destinationPort;

    @Value("${${spring.profiles.active:local}.ips.test.host}")
    private String destinationHostName;

    @Value("${${spring.profiles.active:local}.ips.test.retryWaitTime}")
    private Integer retryWaitTime;

    private String bearerToken;

    private String validAccountAlias = ClcAuthenticationComponent.VALID_AA;
    String accountId = "TCCD";
    String hostName = "server.host.name." + System.currentTimeMillis();

    private Map<ConfigurationItem, User> safeConfigurationItemUsers = new HashMap<>();

    private Map<ConfigurationItem, User> attackedConfigurationItemsUsers = new HashMap<>();

    @Value("${${spring.profiles.active:local}.ips.maxRetryAttempts}")
    private Integer maxRetryAttempts;

    @Given("^there is (\\d+) configuration item running$")
    public void there_is_configuration_item_running(int amountOfConfigurationItems) throws Throwable {
        for (int index = 0; index < amountOfConfigurationItems; index++) {
            User user = createNewUser("User", validAccountAlias);
            ConfigurationItem configurationItem = createAndConfigureConfigurationItem(user.getAccountId(),
                    getUniqueHostName());
            safeConfigurationItemUsers.put(configurationItem, user);
        }
        for (Map.Entry<ConfigurationItem, User> entry : safeConfigurationItemUsers.entrySet()) {
            ConfigurationItem currentConfigurationItem = entry.getKey();
            User currentUser = entry.getValue();

            waitForUserCreation(currentUser.getUsername(), currentUser.getAccountId());
            waitForConfigurationItemCreation(
                    currentConfigurationItem.getHostName(),
                    currentConfigurationItem.getAccount().getCustomerAccountId()
            );
        }
    }

    private void waitForConfigurationItemCreation(String hostName, String customerAccountId) {
        ConfigurationItem configurationItem=null;
        int currentAttempts = 0;
        while (currentAttempts < MAX_ATTEMPTS && configurationItem == null) {
            waitComponent.sleep(retryWaitTime, currentAttempts);
            configurationItem = configurationItemClient.getConfigurationItem(hostName,customerAccountId).getContent();
            currentAttempts++;
        }
    }

    private void waitForUserCreation(String userName,String AccountId) {
        User userRetrieved=null;
        int currentAttempts = 0;
        while (currentAttempts < MAX_ATTEMPTS && userRetrieved == null) {
            waitComponent.sleep(retryWaitTime, currentAttempts);
            userRetrieved = userClient.getUser(userName,AccountId).getContent();
            currentAttempts++;
        }
    }

    @And("^the notification destination is set for all configuration items$")
    public void the_notification_destination_is_set_for_all_configuration_items() throws Throwable {
        for (Map.Entry<ConfigurationItem, User> entry : safeConfigurationItemUsers.entrySet()) {
            String notificationDestinationUrl = entry.getKey().getHostName() + "destinationUrl";
            String destinationURL = "http://" + destinationHostName + ":" + destinationPort
                    + "/" + notificationDestinationUrl;


            createAndSetNotificationDestination(destinationURL, entry.getKey());

            notificationDestinationWireMockServer.stubFor(post(urlPathEqualTo(notificationDestinationUrl))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.SC_OK)));
        }
        for (Map.Entry<ConfigurationItem, User> entry : safeConfigurationItemUsers.entrySet()) {
            waitForNotificationDestinationUpdate(entry.getKey());
        }
    }

    @When("^an event are posted to DSM for (\\d+) of the configuration items$")
    public void an_event_are_posted_to_DSM_for_of_the_configuration_items(int amountOfAttackedConfigurationItems) throws Throwable {
        for (int index = 0; index < amountOfAttackedConfigurationItems; index++) {
            ConfigurationItem attackedConfigItem = randomSafeConfigItem();
            User attackedUser = safeConfigurationItemUsers.get(attackedConfigItem);

            //Creates a FirewallEvent
            FirewallEvent firewallEvent = new FirewallEvent();
            firewallEvent.setReason("An FirewallEvent Reason");
            firewallEvent.setHostName(attackedConfigItem.getHostName());

            eventAdapter.triggerEvent(attackedConfigItem, Arrays.asList(firewallEvent));

            attackedConfigurationItemsUsers.put(attackedConfigItem, attackedUser);
            safeConfigurationItemUsers.remove(attackedConfigItem);
        }

    }

    @Then("^the events are posted to the attacked notification destinations$")
    public void the_events_are_posted_to_the_attacked_notification_destinations() throws Throwable {
        for (Map.Entry<ConfigurationItem, User> entry : attackedConfigurationItemsUsers.entrySet()) {
            ConfigurationItem currentConfigurationItem = entry.getKey();
            User currentUser = entry.getValue();
            ConfigurationItem retrievedConfigurationItem = configurationItemClient
                    .getConfigurationItem(
                            currentConfigurationItem.getHostName(),
                            currentConfigurationItem.getAccount().getCustomerAccountId()
                    )
                    .getContent();

            //TODO Create Test for TS,QA,PROD
            String activeProfiles = environment.getActiveProfiles()[0];

            for (NotificationDestination notificationDestination : retrievedConfigurationItem.getAccount().getNotificationDestinations()) {
                waitForPostRequests(1, notificationDestination.getUrl());
                if (activeProfiles.equalsIgnoreCase("local") || activeProfiles.equalsIgnoreCase("dev")) {
                    notificationDestinationWireMockServer.verify(postRequestedFor(urlEqualTo(getWireMockUrl(notificationDestination.getUrl()))));
                }
            }

            cleanUpUser(currentUser);
            cleanUpConfigurationItems(currentConfigurationItem);
        }
    }

    private void cleanUpConfigurationItems(ConfigurationItem currentConfigurationItem) {
        ConfigurationItem configurationItem = configurationItemClient.getConfigurationItem(
                currentConfigurationItem.getHostName(),
                currentConfigurationItem.getAccount().getCustomerAccountId()
        ).getContent();

        configurationItemClient.deleteConfigurationItem(configurationItem.getId());
    }

    @And("^no events are posted to the safe notification destinations$")
    public void no_events_are_posted_to_the_safe_notification_destinations() throws Throwable {
        for (Map.Entry<ConfigurationItem, User> entry : safeConfigurationItemUsers.entrySet()) {
            ConfigurationItem currentConfigurationItem = entry.getKey();
            User currentUser = entry.getValue();
            ConfigurationItem retrievedConfigurationItem = configurationItemClient
                    .getConfigurationItem(
                            currentConfigurationItem.getHostName(),
                            currentConfigurationItem.getAccount().getCustomerAccountId()
                    )
                    .getContent();

            //TODO Create Test for TS,QA,PROD
            String activeProfiles = environment.getActiveProfiles()[0];

            for (NotificationDestination notificationDestination : retrievedConfigurationItem.getAccount().getNotificationDestinations()) {
                if (activeProfiles.equalsIgnoreCase("local") || activeProfiles.equalsIgnoreCase("dev")) {
                    notificationDestinationWireMockServer.verify(0, postRequestedFor(urlEqualTo(getWireMockUrl(notificationDestination.getUrl()))));
                }
            }

            cleanUpUser(currentUser);
            cleanUpConfigurationItems(currentConfigurationItem);
        }
    }

    private ConfigurationItem randomSafeConfigItem() {
        Integer selectedConfigItemIndex = new Random().nextInt(safeConfigurationItemUsers.size());

        ConfigurationItem randomSafeConfigItem = (ConfigurationItem) safeConfigurationItemUsers.keySet().toArray()[selectedConfigItemIndex];

        return randomSafeConfigItem;
    }

    private void cleanUpUser(User entry) {
        User user = userClient.getUser(entry.getUsername(), entry.getAccountId()).getContent();
        userClient.deleteUser(user.getId());
    }

    private String getWireMockUrl(String notificationDestination) {
        String[] split = notificationDestination.split("/");
        return "/" + split[split.length - 1];
    }

    private String getUniqueHostName() {
        return "server.host.name." + System.currentTimeMillis();
    }

    private void waitForPostRequests(int requests, String fullAddress) {
        int currentAttempts = 0;
        List<LoggedRequest> loggedRequests;
        do {
            waitComponent.sleep(retryWaitTime, currentAttempts);
            loggedRequests = notificationDestinationWireMockServer.findAll(postRequestedFor(urlEqualTo(getWireMockUrl(fullAddress))));
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

        ClcAuthenticationResponse clcAuthenticationResponse = clcAuthenticationComponent.authenticate();
        bearerToken = clcAuthenticationResponse.getBearerToken();

        notificationClient.updateNotificationDestination(notificationDestinationBean, bearerToken);
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
            waitComponent.sleep(retryWaitTime, currentAttempts);

            configurationItem = configurationItemClient
                    .getConfigurationItem(
                            configurationItem.getHostName(),
                            configurationItem.getAccount().getCustomerAccountId()
                    )
                    .getContent();

            notificationDestinations = configurationItem
                    .getAccount()
                    .getNotificationDestinations();
            currentAttempts++;
        }
    }

    private User createNewUser(String userName, String accountId) {
        User user = new User();
        user.setAccountId(accountId);// + System.currentTimeMillis());
        user.setUsername(userName + System.currentTimeMillis());
        userClient.createUser(user);
        return user;
    }

}
