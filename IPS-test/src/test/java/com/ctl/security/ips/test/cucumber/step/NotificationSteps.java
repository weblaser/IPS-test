package com.ctl.security.ips.test.cucumber.step;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by sean.robb on 3/5/2015.
 */

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {CucumberConfiguration.class})
public class NotificationSteps {

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private ConfigurationItemClient configurationItemClient;

    @Autowired
    private ClcAuthenticationComponent clcAuthenticationComponent;

    private NotificationDestinationBean notificationDestinationBean;
    private String bearerToken;
    public static final int MAX_ATTEMPTS = 30;

    @Given("^the customer wants to update a notification for a server$")
    public void the_customer_wants_to_update_a_notification_for_a_server(){

        bearerToken = clcAuthenticationComponent.authenticate();

        String hostName = "server.host.name." + System.currentTimeMillis();
        String accountId = ClcAuthenticationComponent.VALID_AA;
        NotificationDestination notificationDestination = new NotificationDestination();

        notificationDestination.setEmailAddress("My.Test.Email@Testing.Test");
        notificationDestination.setIntervalCode(NotificationDestinationInterval.DAILY);
        notificationDestination.setTypeCode(NotificationDestinationType.WEBHOOK);
        notificationDestination.setUrl("my.test.url/testing");

        Account account = new Account()
                .setCustomerAccountId(accountId);

        ConfigurationItem configurationItem = new ConfigurationItem()
                .setAccount(account)
                .setHostName(hostName);

        configurationItemClient.createConfigurationItem(configurationItem);

        List<NotificationDestination> notificationDestinations = Arrays.asList(notificationDestination);
        notificationDestinationBean = new NotificationDestinationBean(hostName, accountId, notificationDestinations);
    }

    @When("^the notification destination is updated via the notification resource$")
    public void the_notification_destination_is_updated_via_the_notification_resource(){
        notificationClient.updateNotificationDestination(notificationDestinationBean, bearerToken);
    }

    @Then("^the server notification destination is updated with new destination$")
    public void the_server_notification_destination_is_updated_with_new_destination() throws Throwable{
        ConfigurationItemResource configurationItemResource=null;
        List<NotificationDestination> notificationDestinations=null;

        int currentAttempts = 0;
        int maxAttempts = MAX_ATTEMPTS;
        while(currentAttempts < maxAttempts && notificationDestinations == null){
            configurationItemResource = configurationItemClient.getConfigurationItem(notificationDestinationBean.getHostName(), notificationDestinationBean.getAccountId());
            notificationDestinations = configurationItemResource.getContent().getAccount().getNotificationDestinations();
            Thread.sleep(1000);
            currentAttempts++;
        }

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
}
