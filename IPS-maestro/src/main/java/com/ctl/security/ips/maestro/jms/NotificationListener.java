package com.ctl.security.ips.maestro.jms;

import com.ctl.security.ips.common.jms.NotificationOperation;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import com.ctl.security.ips.maestro.service.NotificationServiceWrite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Created by sean.robb on 3/6/2015.
 */

@Component
public class NotificationListener {

    @Autowired
    private NotificationServiceWrite notificationServiceWrite;

    @JmsListener(destination = NotificationOperation.UPDATE_NOTIFICATION_DESTINATION_FOR_SERVER)
    public void updateNotificationDestination(final NotificationDestinationBean notificationDestinationBean) {
        notificationServiceWrite.updateNotificationDestination(notificationDestinationBean);
    }
}

