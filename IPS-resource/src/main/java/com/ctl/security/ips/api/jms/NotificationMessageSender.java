package com.ctl.security.ips.api.jms;

import com.ctl.security.ips.common.jms.NotificationOperation;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by sean.robb on 3/6/2015.
 */
@Component
public class NotificationMessageSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void updateNotificationDestination(final NotificationDestinationBean notificationDestinationBean) {
        jmsTemplate.convertAndSend(NotificationOperation.UPDATE_NOTIFICATION_DESTINATION_FOR_SERVER, notificationDestinationBean);
    }

    public void deleteNotificationDestination(final NotificationDestinationBean notificationDestinationBean) {
        jmsTemplate.convertAndSend(NotificationOperation.DELETE_NOTIFICATION_DESTINATION_FOR_SERVER, notificationDestinationBean);
    }
}
