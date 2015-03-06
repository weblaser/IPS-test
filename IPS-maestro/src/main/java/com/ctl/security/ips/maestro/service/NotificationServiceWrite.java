package com.ctl.security.ips.maestro.service;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.domain.configurationitem.ConfigurationItemResource;
import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kevin.wilde on 3/4/2015.
 */

@Component
public class NotificationServiceWrite {

    @Autowired
    private ConfigurationItemClient configurationItemClient;

    public void updateNotificationDestination(NotificationDestinationBean notificationDestinationBean) {

        ConfigurationItemResource configurationItemResource = configurationItemClient.getConfigurationItem(notificationDestinationBean.getHostName(), notificationDestinationBean.getAccountId());

        if(configurationItemResource != null){
            ConfigurationItem configurationItem = configurationItemResource.getContent();
            configurationItem.getAccount().setNotificationDestinations(notificationDestinationBean.getNotificationDestinations());
            configurationItemClient.updateConfigurationItem(configurationItem);
        }


    }
}
