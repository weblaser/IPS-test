package com.ctl.security.ips.service;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.common.domain.mongo.Account;
import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceWriteTest {

    @InjectMocks
    private NotificationServiceWrite classUnderTest;

    @Mock
    private ConfigurationItemClient configurationItemClient;

    @Test
    public void updateNotificationDestination_updatesNotificationDestination(){

        ConfigurationItem configurationItem = new ConfigurationItem();
        String hostName = null;
        String accountId = null;
        Account account = new Account().setCustomerAccountId(accountId);
        configurationItem.setHostName(hostName).setAccount(account);

        classUnderTest.updateNotificationDestination(hostName, accountId);

        verify(configurationItemClient).updateConfigurationItem(configurationItem);

    }

}