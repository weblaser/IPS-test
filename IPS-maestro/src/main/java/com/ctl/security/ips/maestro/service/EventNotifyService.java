package com.ctl.security.ips.maestro.service;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.cmdb.ProductUserActivityClient;
import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import com.ctl.security.data.common.domain.mongo.ProductUserActivity;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.maestro.config.MaestroConfig;
import com.ctl.security.library.common.httpclient.CtlSecurityClient;
import com.ctl.security.library.common.httpclient.CtlSecurityResponse;
import org.apache.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

/**
 * Created by sean.robb on 3/9/2015.
 */
@Component
public class EventNotifyService {

    @Autowired
    private ConfigurationItemClient configurationItemClient;

    @Autowired
    private ProductUserActivityClient productUserActivityClient;
    
    private CtlSecurityClient ctlSecurityClient;

    @Value("${${spring.profiles.active:local}.ips.maxRetryAttempts}")
    private Integer maxRetryAttempts;

    @Value("${${spring.profiles.active:local}.ips.retryWaitTime}")
    private Integer retryWaitTime;

    //TODO: After story SECURITY-755 is completed switch to org.apache.logging.log4j.LogManager
    private static final Logger logger = Logger.getLogger(EventNotifyService.class);

    public void notify(EventBean eventBean) {

        List<NotificationDestination> notificationDestinations = configurationItemClient
                .getConfigurationItem(eventBean.getHostName(), eventBean.getAccountId())
                .getContent()
                .getAccount()
                .getNotificationDestinations();

        for (NotificationDestination notificationDestination : notificationDestinations) {
            try {
                Integer retryAttempts = 0;
                CtlSecurityResponse ctlSecurityResponse;

                do {
                    ctlSecurityResponse = ctlSecurityClient.post(notificationDestination.getUrl())
                            .body(eventBean.getEvent())
                            .execute();

                    retryAttempts++;
                    try {
                        Thread.sleep(retryWaitTime);
                    } catch (Exception e) {
                    }
                } while (!ctlSecurityResponse.isSuccessful() && (retryAttempts < maxRetryAttempts));

                if (!ctlSecurityResponse.isSuccessful()) {
                    logger.error("Failed to Send Notification to " + notificationDestination.getUrl());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ProductUserActivity productUserActivity = new ProductUserActivity();

//                productUserActivity.setConfigurationItem(eventBean.)
            productUserActivityClient.createProductUserActivity(productUserActivity);
        }
    }
}
