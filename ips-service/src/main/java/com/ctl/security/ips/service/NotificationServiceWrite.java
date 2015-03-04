package com.ctl.security.ips.service;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.common.domain.mongo.Account;
import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kevin.wilde on 3/4/2015.
 */

@Component
public class NotificationServiceWrite {

    @Autowired
    private ConfigurationItemClient configurationItemClient;

    public void updateNotificationDestination(String hostName, String accountId) {

        ConfigurationItem configurationItem = new ConfigurationItem();

        Account account =new Account().setCustomerAccountId(accountId);
        configurationItem.setHostName(hostName).setAccount(account);

        configurationItemClient.updateConfigurationItem(configurationItem);

    }
}
