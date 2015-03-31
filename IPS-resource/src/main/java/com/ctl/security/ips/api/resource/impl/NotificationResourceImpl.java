package com.ctl.security.ips.api.resource.impl;

import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import com.ctl.security.ips.api.jms.NotificationMessageSender;
import com.ctl.security.ips.api.resource.NotificationResource;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Created by sean.robb on 3/5/2015.
 */

@Component
public class NotificationResourceImpl implements NotificationResource {

    @Autowired
    private NotificationMessageSender notificationMessageSender;

    @Override
    public void updateNotificationDestination(String account, String hostName, List<NotificationDestination> notificationDestinations) {
        NotificationDestinationBean notificationDestinationBean = new NotificationDestinationBean(hostName, account, notificationDestinations);
        notificationMessageSender.updateNotificationDestination(notificationDestinationBean);
    }
}
