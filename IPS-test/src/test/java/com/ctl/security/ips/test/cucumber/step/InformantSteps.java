package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.clc.client.common.domain.ClcAuthenticationResponse;
import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.cmdb.UserClient;
import com.ctl.security.data.common.domain.mongo.*;
import com.ctl.security.ips.client.NotificationClient;
import com.ctl.security.ips.client.PolicyClient;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import com.ctl.security.ips.test.cucumber.adapter.EventAdapter;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import cucumber.api.PendingException;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

/**
 * Created by Sean Robb on 3/30/2015.
 */
public class InformantSteps {

    public static final int MAX_ATTEMPTS = 100;

    @Autowired
    EventAdapter eventAdapter;

    @Autowired
    private PolicyClient policyClient;

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

    @Value("#{SecurityLibraryPropertySplitter.map('${${spring.profiles.active:local}.ips.test.validHostAccountInfo}')}")
    private HashMap<String, String> accountOptions;

    @Value("${${spring.profiles.active:local}.ips.maxRetryAttempts}")
    private Integer maxRetryAttempts;

    private String bearerToken;

    private Map<ConfigurationItem, User> safeConfigurationItemUsers = new HashMap<>();

    private Map<ConfigurationItem, User> attackedConfigurationItemsUsers = new HashMap<>();

    @Given("^there is (\\d+) configuration item running$")
    public void there_is_configuration_item_running(int amountOfConfigurationItems) throws Throwable {

        assertThat("The Amount of Configuration Items to run is larger the the pool available",
                amountOfConfigurationItems,
                lessThan(accountOptions.size() + 1));

        Iterator<Map.Entry<String, String>> iterator = accountOptions.entrySet().iterator();

        for (int index = 0; index < amountOfConfigurationItems; index++) {
            Map.Entry<String, String> next = iterator.next();

            String knownValidAccountAlias = next.getKey();
            String knownValidHostName = next.getValue();

            User user = createNewUser("User", knownValidAccountAlias);

            ConfigurationItem configurationItem = createAndConfigureConfigurationItem(knownValidAccountAlias,
                    knownValidHostName);
            safeConfigurationItemUsers.put(configurationItem, user);
        }
    }

    @And("^DSM agent is installed on all of the configuration items$")
    public void DSM_agent_is_installed_on_all_of_the_configuration_items() {

        bearerToken = clcAuthenticationComponent.authenticate().getBearerToken();

        for (Map.Entry<ConfigurationItem, User> entry : safeConfigurationItemUsers.entrySet()) {

            ConfigurationItem configurationItem = entry.getKey();
            User user = entry.getValue();

            Policy policy = new Policy()
                    .setName("name" + System.currentTimeMillis())
                    .setHostName(configurationItem.getHostName())
                    .setUsername(user.getAccountId() + System.currentTimeMillis()); //This potentially needs to be a unique value

            policyClient.createPolicyForAccount(user.getAccountId(), policy, bearerToken);
        }
    }

    @And("^the notification destination is set for all configuration items$")
    public void the_notification_destination_is_set_for_all_configuration_items() throws Throwable {
        for (Map.Entry<ConfigurationItem, User> entry : safeConfigurationItemUsers.entrySet()) {
            String notificationDestinationUrl = entry.getKey().getHostName() +
                    "destinationUrl" + System.currentTimeMillis();

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
        assertThat("The Amount of Configuration Items To attack is too large",
                amountOfAttackedConfigurationItems,
                lessThan(safeConfigurationItemUsers.size() + 1));

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
                if (activeProfiles.equalsIgnoreCase("local") || activeProfiles.equalsIgnoreCase("dev") || activeProfiles.equalsIgnoreCase("ts")) {
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

    private String getWireMockUrl(String notificationDestination) {
        String[] split = notificationDestination.split("/");
        return "/" + split[split.length - 1];
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

    private ConfigurationItem createAndConfigureConfigurationItem(String accountId, String hostName) {
        //Clear out old matches to Database
        ConfigurationItem oldConfigurationItem = configurationItemClient
                .getConfigurationItem(hostName, accountId)
                .getContent();

        if (oldConfigurationItem.getHostName() != null && oldConfigurationItem.getAccount() != null) {
            configurationItemClient.deleteConfigurationItem(oldConfigurationItem.getId());
        }

        Account account = new Account()
                .setCustomerAccountId(accountId);

        ConfigurationItem configurationItem = new ConfigurationItem()
                .setAccount(account)
                .setHostName(hostName);

        configurationItemClient.createConfigurationItem(configurationItem);
        return configurationItem;
    }

    private User createNewUser(String userName, String accountId) {
        User user = new User();
        user.setAccountId(accountId);
        user.setUsername(userName + System.currentTimeMillis());
        userClient.createUser(user);
        return user;
    }

    private void cleanUpConfigurationItems(ConfigurationItem currentConfigurationItem) {
        ConfigurationItem configurationItem = configurationItemClient.getConfigurationItem(
                currentConfigurationItem.getHostName(),
                currentConfigurationItem.getAccount().getCustomerAccountId()
        ).getContent();

        configurationItemClient.deleteConfigurationItem(configurationItem.getId());
    }

    private void cleanUpUser(User userToDelete) {
        User user = userClient.getUser(userToDelete.getUsername(), userToDelete.getAccountId()).getContent();
        userClient.deleteUser(user.getId());
    }

}
