package com.ctl.security.ips.service;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.common.domain.mongo.Account;
import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.service.config.IpsServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by sean.robb on 3/9/2015.
 */
@Component
public class EventNotifyService {

    @Autowired
    private ConfigurationItemClient configurationItemClient;

    @Autowired
    @Qualifier(IpsServiceConfig.IPS_SERVICE_REST_TEMPLATE)
    private RestTemplate restTemplate;

    public void notify(EventBean eventBean)
    {
        List<NotificationDestination> notificationDestinations = configurationItemClient
                .getConfigurationItem(eventBean.getHostName(), eventBean.getAccountId())
                .getContent()
                .getAccount()
                .getNotificationDestinations();

        for (NotificationDestination notification : notificationDestinations)
        {
            restTemplate.exchange(notification.getUrl(),HttpMethod.POST,
                    new HttpEntity<>(eventBean.getEvent().getMessage()), String.class);
        }
    }
}
