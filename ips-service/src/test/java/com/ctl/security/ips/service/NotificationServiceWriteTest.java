package com.ctl.security.ips.service;

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

    @Test
    public void updateNotificationDestination_updatesNotificationDestination(){

        String hostName = null;
        String accountId = null;
        NotificationDestination notificationDestination = null;
        List<NotificationDestination> notificationDestinations = Arrays.asList(notificationDestination);
        NotificationDestinationBean notificationDestinationBean = new NotificationDestinationBean(hostName, accountId, notificationDestinations);

        when(configurationItem.getAccount()).thenReturn(account);
        when(configurationItemClient.getConfigurationItem(hostName, accountId)).thenReturn(configurationItemResource);
        when(configurationItemResource.getContent()).thenReturn(configurationItem);


        classUnderTest.updateNotificationDestination(notificationDestinationBean);


        verify(configurationItemClient).getConfigurationItem(hostName, accountId);
        verify(account).setNotificationDestinations(notificationDestinations);
        verify(configurationItemClient).updateConfigurationItem(configurationItem);

    }

}