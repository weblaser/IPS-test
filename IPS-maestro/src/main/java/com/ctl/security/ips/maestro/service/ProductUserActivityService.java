package com.ctl.security.ips.maestro.service;

import com.ctl.security.data.client.cmdb.ProductUserActivityClient;
import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import com.ctl.security.data.common.domain.mongo.ProductUserActivity;
import com.ctl.security.library.common.httpclient.CtlSecurityResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kevin on 4/23/15.
 */

@Component
public class ProductUserActivityService {

    private static final Logger logger = LogManager.getLogger(ProductUserActivityService.class);

    @Autowired
    private ProductUserActivityClient productUserActivityClient;

    void persistNotification(Boolean postedToNotificationDestination,
                             ConfigurationItem configurationItem,
                             NotificationDestination notificationDestination) {

        String productUserActivityDescription;

        if (postedToNotificationDestination) {
            productUserActivityDescription = EventNotifyService.SUCCESSFULLY_SENT_NOTIFICATION_TO + notificationDestination.getUrl();
            logger.info(productUserActivityDescription);
        }
        else{
            productUserActivityDescription = EventNotifyService.FAILED_TO_SEND_NOTIFICATION_TO + notificationDestination.getUrl();
            logger.error(productUserActivityDescription);
        }

        ProductUserActivity productUserActivity = new ProductUserActivity();

        productUserActivity.setConfigurationItem(configurationItem);
        productUserActivity.setDescription(productUserActivityDescription);

        productUserActivityClient.createProductUserActivity(productUserActivity);
    }
}
