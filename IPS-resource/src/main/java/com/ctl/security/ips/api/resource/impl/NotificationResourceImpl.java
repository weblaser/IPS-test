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
 * Added to by Chad on 5/12/2015.
 */

@Component
public class NotificationResourceImpl implements NotificationResource {

    private Logger logger = LogManager.getLogger(NotificationResourceImpl.class);

    @Autowired
    private NotificationMessageSender notificationMessageSender;

    @Override
    public void updateNotificationDestination(String account, String hostName, List<NotificationDestination> notificationDestinations) {

        logger.info("updateNotificationDestination begin");
        logger.info("account: " + account );
        logger.info("hostName: " + hostName);
        logger.info("notificationDestinations: " + notificationDestinations);

        NotificationDestinationBean notificationDestinationBean = new NotificationDestinationBean(hostName, account, notificationDestinations);
        notificationMessageSender.updateNotificationDestination(notificationDestinationBean);

        logger.info("updateNotificationDestination end");
    }

    @Override
    public void deleteNotificationDestination(String account, String hostName) {
        logger.info("deleteNotificationDestination begin");
        logger.info("account: " + account );
        logger.info("hostName: " + hostName);

        NotificationDestinationBean notificationDestinationBean = new NotificationDestinationBean(hostName, account, null);
        notificationMessageSender.deleteNotificationDestination(notificationDestinationBean);

        logger.info("deleteNotificationDestination end");
    }
}
