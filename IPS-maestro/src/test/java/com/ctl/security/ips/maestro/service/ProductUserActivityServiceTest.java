package com.ctl.security.ips.maestro.service;

import com.ctl.security.data.client.cmdb.ProductUserActivityClient;
import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import com.ctl.security.data.common.domain.mongo.ProductUserActivity;
import com.ctl.security.ips.common.jms.bean.EventBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductUserActivityServiceTest {

    @InjectMocks
    private ProductUserActivityService classUnderTest;

    @Mock
    private ProductUserActivityClient productUserActivityClient;


    @Test
    public void persistNotification_persistsNotification() {
        Boolean postedToNotificationDestination = true;
        ConfigurationItem configurationItem = null;
        NotificationDestination notificationDestination = new NotificationDestination();

        classUnderTest.persistNotification(postedToNotificationDestination, configurationItem, notificationDestination);
    }


    @Test
    public void persistNotification_persistsSuccessfulNotification() {
        Boolean postedToNotificationDestination = true;
        ConfigurationItem configurationItem = null;
        NotificationDestination notificationDestination = new NotificationDestination();

        classUnderTest.persistNotification(postedToNotificationDestination, configurationItem, notificationDestination);

        ProductUserActivity productUserActivity = new ProductUserActivity();

        productUserActivity.setConfigurationItem(configurationItem);
        productUserActivity.setDescription(EventNotifyService.SUCCESSFULLY_SENT_NOTIFICATION_TO + notificationDestination.getUrl());

        verify(productUserActivityClient).createProductUserActivity(productUserActivity);
    }


    @Test
    public void persistNotification_persistsFailedNotification() {
        Boolean postedToNotificationDestination = false;
        ConfigurationItem configurationItem = null;
        NotificationDestination notificationDestination = new NotificationDestination();

        classUnderTest.persistNotification(postedToNotificationDestination, configurationItem, notificationDestination);

        ProductUserActivity productUserActivity = new ProductUserActivity();

        productUserActivity.setConfigurationItem(configurationItem);
        productUserActivity.setDescription(EventNotifyService.FAILED_TO_SEND_NOTIFICATION_TO + notificationDestination.getUrl());

        verify(productUserActivityClient).createProductUserActivity(productUserActivity);
    }
}