package com.ctl.security.ips.maestro.service;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.domain.configurationitem.ConfigurationItemResource;
import com.ctl.security.data.common.domain.mongo.Account;
import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceWriteTest {

    @InjectMocks
    private NotificationServiceWrite classUnderTest;

    @Mock
    private ConfigurationItemClient configurationItemClient;

    @Mock
    private ConfigurationItemResource configurationItemResource;

    @Mock
    private ConfigurationItem configurationItem;

    @Mock
    private Account account;

    private NotificationDestinationBean notificationDestinationBean;
    private String hostName;
    private String accountId;

    @Test
    public void testUpdateNotificationDestination_updatesNotificationDestination(){

        NotificationDestination notificationDestination = null;
        List<NotificationDestination> notificationDestinations = Arrays.asList(notificationDestination);
        notificationDestinationBean = new NotificationDestinationBean(hostName, accountId, notificationDestinations);

        when(configurationItem.getAccount()).thenReturn(account);
        when(configurationItemClient.getConfigurationItem(hostName, accountId)).thenReturn(configurationItemResource);
        when(configurationItemResource.getContent()).thenReturn(configurationItem);


        classUnderTest.updateNotificationDestination(notificationDestinationBean);


        verify(configurationItemClient).getConfigurationItem(hostName, accountId);
        verify(account).setNotificationDestinations(notificationDestinations);
        verify(configurationItemClient).updateConfigurationItem(configurationItem);

    }

    @Test
    public void testDeleteNotificationDestination() throws Exception {
        //arrange
        notificationDestinationBean = new NotificationDestinationBean(hostName, accountId, null);

        when(configurationItemClient.getConfigurationItem(hostName, accountId)).thenReturn(configurationItemResource);
        when(configurationItemResource.getContent()).thenReturn(configurationItem);
        when(configurationItem.getAccount()).thenReturn(account);
        //act
        classUnderTest.deleteNotificationDestination(notificationDestinationBean);
        //assert
        verify(configurationItemClient).updateConfigurationItem(configurationItem);
        verify(configurationItemClient).getConfigurationItem(notificationDestinationBean.getHostName(), notificationDestinationBean.getAccountId());
        verify(account).setNotificationDestinations(null);
    }

    @Test
    public void testDelete_NotificationDestination_NullConfigurationItem() throws Exception {
        //arrange
        notificationDestinationBean = new NotificationDestinationBean(hostName, accountId, null);

        when(configurationItemClient.getConfigurationItem(hostName, accountId)).thenReturn(null);
        //act
        classUnderTest.deleteNotificationDestination(notificationDestinationBean);
        //assert
        verify(configurationItemClient).getConfigurationItem(notificationDestinationBean.getHostName(), notificationDestinationBean.getAccountId());

    }
}