package com.ctl.security.ips.maestro.service;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.common.domain.mongo.Account;
import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import com.ctl.security.ips.common.jms.bean.EventBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by sean.robb on 3/9/2015.
 */
public class EventNotifyService {


    @Autowired
    private ConfigurationItemClient configurationItemClient;

    public void notify(EventBean eventBean)
    {
        List<NotificationDestination> notificationDestinations = configurationItemClient
                .getConfigurationItem(eventBean.getHostName(), eventBean.getAccountId())
                .getContent()
                .getAccount()
                .getNotificationDestinations();



    }
}
