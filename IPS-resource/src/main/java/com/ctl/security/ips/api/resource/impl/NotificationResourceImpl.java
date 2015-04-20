package com.ctl.security.ips.api.resource.impl;

import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import com.ctl.security.ips.api.jms.NotificationMessageSender;
import com.ctl.security.ips.api.resource.NotificationResource;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Created by sean.robb on 3/5/2015.
 */

@Component
public class NotificationResourceImpl implements NotificationResource {

    private Logger logger = LogManager.getLogger(NotificationResourceImpl.class);

    @Autowired
    private NotificationMessageSender notificationMessageSender;

    @Override
    public void updateNotificationDestination(String account, String hostName, List<NotificationDestination> notificationDestinations) {

        logger.error("updateNotificationDestination begin");
        logger.error("account: " + account );
        logger.error("hostName: " + hostName);
        logger.error("notificationDestinations: " + notificationDestinations);

        NotificationDestinationBean notificationDestinationBean = new NotificationDestinationBean(hostName, account, notificationDestinations);
        notificationMessageSender.updateNotificationDestination(notificationDestinationBean);

        logger.error("updateNotificationDestination end");
    }
}
