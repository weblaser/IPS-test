package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.cmdb.UserClient;
import com.ctl.security.data.common.domain.mongo.*;
import com.ctl.security.ips.client.NotificationClient;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import com.ctl.security.ips.test.cucumber.adapter.EventAdapter;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
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

    @Autowired
    private UserClient userClient;


    @Value("${${spring.profiles.active:local}.ips.test.port}")
    private int destinationPort;

    @Value("${${spring.profiles.active:local}.ips.test.host}")
    private String destinationHostName;

    @Value("${${spring.profiles.active:local}.ips.test.retryWaitTime}")
    private Integer retryWaitTime;

    private String bearerToken;

    private String validAccountAlias1 = ClcAuthenticationComponent.VALID_ACCOUNT_ALIAS1;

    private Map<ConfigurationItem, User> safeConfigurationItemUsers = new HashMap<>();

    private Map<ConfigurationItem, User> attackedConfigurationItemsUsers = new HashMap<>();

    @Value("${${spring.profiles.active:local}.ips.maxRetryAttempts}")
    private Integer maxRetryAttempts;
    private WireMockServer wireMockServer;

    @Given("^there is (\\d+) configuration item running$")
    public void there_is_configuration_item_running(int amountOfConfigurationItems) throws Throwable {
        for (int index = 0; index < amountOfConfigurationItems; index++) {
            User user = createNewUser("User", validAccountAlias1);
            ConfigurationItem configurationItem = createAndConfigureConfigurationItem(user.getAccountId(),
                    getUniqueHostName());
            safeConfigurationItemUsers.put(configurationItem, user);
        }
    }

    @And("^the notification destination is set for all configuration items$")
    public void the_notification_destination_is_set_for_all_configuration_items() throws Throwable {
        wireMockServer = wireMockComponent.createWireMockServer(destinationHostName, destinationPort);
//        wireMockClient = wireMockComponent.createWireMockClient(destinationHostName, destinationPort);
        for (Map.Entry<ConfigurationItem, User> entry : safeConfigurationItemUsers.entrySet()) {
            String notificationDestinationUrl = entry.getKey().getHostName() + "destinationUrl";
            String destinationURL = "http://" + destinationHostName + ":" + destinationPort
                    + "/" + notificationDestinationUrl;
            createAndSetNotificationDestination(destinationURL, entry.getKey());
            wireMockComponent.createWireMockServerPostStub(wireMockServer, notificationDestinationUrl, HttpStatus.SC_OK);
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

    private ConfigurationItem randomSafeConfigItem() {
        Integer selectedConfigItemIndex = new Random().nextInt(safeConfigurationItemUsers.size());

        ConfigurationItem randomSafeConfigItem = (ConfigurationItem) safeConfigurationItemUsers.keySet().toArray()[selectedConfigItemIndex];

        return randomSafeConfigItem;
    }

    @Then("^the events are posted to the attacked notification destinations$")
    public void the_events_are_posted_to_the_attacked_notification_destinations() throws Throwable {
        verifyAllConfigurationItemPosts(attackedConfigurationItemsUsers, 1);
    }

    @And("^no events are posted to the safe notification destinations$")
    public void no_events_are_posted_to_the_safe_notification_destinations() throws Throwable {
        verifyAllConfigurationItemPosts(safeConfigurationItemUsers, 0);

        wireMockServer.stop();
    }

    private void verifyAllConfigurationItemPosts(Map<ConfigurationItem, User> configurationItemUserMap, Integer postTimes) {
        for (Map.Entry<ConfigurationItem, User> entry : configurationItemUserMap.entrySet()) {
            ConfigurationItem currentConfigurationItem = entry.getKey();
            User currentUser = entry.getValue();
            verifyPosts(postTimes, currentConfigurationItem, currentUser);
        }
    }

    private void verifyPosts(int postTimes, ConfigurationItem currentConfigurationItem, User currentUser) {
        ConfigurationItem retrievedConfigurationItem = configurationItemClient
                .getConfigurationItem(
                        currentConfigurationItem.getHostName(),
                        currentConfigurationItem.getAccount().getCustomerAccountId()
                )
                .getContent();

        //TODO Create Test for TS,QA,PROD
        String activeProfiles = environment.getActiveProfiles()[0];

        for (NotificationDestination notificationDestination : retrievedConfigurationItem.getAccount().getNotificationDestinations()) {
            waitForPostRequests(postTimes, notificationDestination.getUrl());
            if (activeProfiles.equalsIgnoreCase("local") || activeProfiles.equalsIgnoreCase("dev")) {
                wireMockServer.verify(postTimes, postRequestedFor(urlEqualTo(getWireMockUrl(notificationDestination.getUrl()))));
            }
        }

        cleanUpUser(currentUser);
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
            loggedRequests = wireMockServer.findAll(postRequestedFor(urlEqualTo(getWireMockUrl(fullAddress))));
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
        user.setAccountId(accountId + System.currentTimeMillis());
        user.setUsername(userName + System.currentTimeMillis());
        userClient.createUser(user);
        return user;
    }

}
