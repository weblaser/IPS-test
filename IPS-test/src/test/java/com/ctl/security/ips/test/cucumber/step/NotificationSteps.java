package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.clc.client.common.domain.ClcAuthenticationResponse;
import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.domain.configurationitem.ConfigurationItemResource;
import com.ctl.security.data.common.domain.mongo.*;
import com.ctl.security.ips.client.NotificationClient;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import com.ctl.security.ips.test.cucumber.config.CucumberConfiguration;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by sean.robb on 3/5/2015.
 *
 */

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {CucumberConfiguration.class})
public class NotificationSteps {

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private ConfigurationItemClient configurationItemClient;

    @Autowired
    private ClcAuthenticationComponent clcAuthenticationComponent;

    @Autowired
    private WaitComponent waitComponent;

    private NotificationDestinationBean notificationDestinationBean;
    private String bearerToken;

    public static final int MAX_ATTEMPTS = 30;

    @Given("^the customer has a notification destination for a server$")
    public void the_customer_has_a_notification_destination_for_a_server(){

        ClcAuthenticationResponse clcAuthenticationResponse = clcAuthenticationComponent.authenticate();
        String accountAlias = clcAuthenticationResponse.getAccountAlias();
        bearerToken = clcAuthenticationResponse.getBearerToken();

        String hostName = "server.host.name." + System.currentTimeMillis();
        NotificationDestination notificationDestination = new NotificationDestination();

        notificationDestination.setEmailAddress("My.Test.Email@Testing.Test");
        notificationDestination.setIntervalCode(NotificationDestinationInterval.DAILY);
        notificationDestination.setTypeCode(NotificationDestinationType.WEBHOOK);
        notificationDestination.setUrl("my.test.url/testing");

        Account account = new Account()
                .setCustomerAccountId(accountAlias);

        ConfigurationItem configurationItem = new ConfigurationItem()
                .setAccount(account)
                .setHostName(hostName);

        configurationItemClient.createConfigurationItem(configurationItem);

        List<NotificationDestination> notificationDestinations = Arrays.asList(notificationDestination);
        notificationDestinationBean = new NotificationDestinationBean(hostName, accountAlias, notificationDestinations);
    }

    @When("^the notification destination is updated via the notification resource$")
    public void the_notification_destination_is_updated_via_the_notification_resource(){
        notificationClient.updateNotificationDestination(notificationDestinationBean, bearerToken);
    }

    @Then("^the server notification destination is updated with new destination$")
    public void the_server_notification_destination_is_updated_with_new_destination() throws Throwable{
        waitForNotificationDestinationUpdate();

        ConfigurationItemResource configurationItemResource = configurationItemClient.getConfigurationItem(notificationDestinationBean.getHostName(), notificationDestinationBean.getAccountId());

        assertNotNull(configurationItemResource);
        assertNotNull(configurationItemResource.getContent());
        assertNotNull(configurationItemResource.getContent().getAccount());
        assertNotNull(configurationItemResource.getContent().getAccount().getNotificationDestinations());
        assertNotNull(configurationItemResource.getContent().getAccount().getNotificationDestinations().get(0));

        assertEquals(notificationDestinationBean.getNotificationDestinations().get(0), configurationItemResource.getContent().getAccount().getNotificationDestinations().get(0));

        assertNotNull(configurationItemResource.getContent().getAccount().getNotificationDestinations().get(0).getUrl());
        assertNotNull(configurationItemResource.getContent().getAccount().getNotificationDestinations().get(0).getIntervalCode());
        assertNotNull(configurationItemResource.getContent().getAccount().getNotificationDestinations().get(0).getTypeCode());
        assertNotNull(configurationItemResource.getContent().getAccount().getNotificationDestinations().get(0).getEmailAddress());

        //cleanup
        configurationItemClient.deleteConfigurationItem(configurationItemResource.getContent().getId());
    }

    @When("^the notification destination is deleted via the notification resource$")
    public void the_notification_destination_is_deleted_via_the_notification_resource() throws Throwable {
        notificationClient.deleteNotificationDestination(notificationDestinationBean, bearerToken);
    }

    @Then("^there is no notification destination in the configuration item$")
    public void there_is_no_notification_destination_in_the_configuration_item() throws Throwable {
        List<NotificationDestination> notificationDestinations;
        ConfigurationItemResource configurationItemResource;
        int currentAttempts = 0;
        do {
            configurationItemResource = configurationItemClient.getConfigurationItem(notificationDestinationBean.getHostName(), notificationDestinationBean.getAccountId());
            notificationDestinations = configurationItemResource.getContent().getAccount().getNotificationDestinations();
            waitComponent.sleep(1000, currentAttempts);
            currentAttempts++;
        } while (currentAttempts < MAX_ATTEMPTS && notificationDestinations != null);

        assertNotNull(configurationItemResource);
        assertNotNull(configurationItemResource.getContent());
        assertNotNull(configurationItemResource.getContent().getAccount());
        assertNull(configurationItemResource.getContent().getAccount().getNotificationDestinations());

        //cleanup
        configurationItemClient.deleteConfigurationItem(configurationItemResource.getContent().getId());
    }

    private ConfigurationItemResource waitForNotificationDestinationUpdate() throws InterruptedException {
        ConfigurationItemResource configurationItemResource = null;
        List<NotificationDestination> notificationDestinations = null;
        int currentAttempts = 0;
        while(currentAttempts < MAX_ATTEMPTS && notificationDestinations == null){
            configurationItemResource = configurationItemClient.getConfigurationItem(notificationDestinationBean.getHostName(), notificationDestinationBean.getAccountId());
            notificationDestinations = configurationItemResource.getContent().getAccount().getNotificationDestinations();
            waitComponent.sleep(1000, currentAttempts);
            currentAttempts++;
        }
        return configurationItemResource;
    }
}
