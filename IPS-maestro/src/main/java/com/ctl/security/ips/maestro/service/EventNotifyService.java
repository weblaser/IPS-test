package com.ctl.security.ips.maestro.service;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.domain.configurationitem.ConfigurationItemResource;
import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import com.ctl.security.ips.common.domain.Event.DpiEvent;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.library.common.httpclient.CtlSecurityResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by sean.robb on 3/9/2015.
 */
@Component
public class EventNotifyService {

    public static final String SUCCESSFULLY_SENT_NOTIFICATION_TO = "Successfully Sent Notification to ";
    public static final String FAILED_TO_SEND_NOTIFICATION_TO = "Failed to Send Notification to ";

    @Autowired
    private ConfigurationItemClient configurationItemClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductUserActivityService productUserActivityService;

    @Value("${${spring.profiles.active:local}.ips.maxRetryAttempts}")
    private Integer maxRetryAttempts;

    @Value("${${spring.profiles.active:local}.ips.retryWaitTime}")
    private Integer retryWaitTime;

    //TODO: After story SECURITY-755 is completed switch to org.apache.logging.log4j.LogManager
    private static final Logger logger = Logger.getLogger(EventNotifyService.class);

    public void notify(EventBean eventBean) {

        ConfigurationItemResource configurationItemResource = configurationItemClient
                .getConfigurationItem(eventBean.getHostName(), eventBean.getAccountId());
        ConfigurationItem configurationItem = configurationItemResource.getContent();

        List<NotificationDestination> notificationDestinations = configurationItem
                .getAccount()
                .getNotificationDestinations();

        for (NotificationDestination notificationDestination : notificationDestinations) {

            Integer retryAttempts = 0;
            CtlSecurityResponse ctlSecurityResponse = null;
            ResponseEntity<String> responseEntity = null;

            do {
                try {
                    responseEntity = restTemplate.exchange(notificationDestination.getUrl(),
                        HttpMethod.POST, new HttpEntity<DpiEvent>(eventBean.getEvent()), String.class);

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }

                retryAttempts++;
                try {
                    Thread.sleep(retryWaitTime);
                } catch (Exception e) {
                }
            }
            while (!postToNotificationDestinationSuccessful(responseEntity) && (retryAttempts < maxRetryAttempts));

            productUserActivityService.persistNotification(postToNotificationDestinationSuccessful(responseEntity), configurationItem, notificationDestination);
        }
    }

    private boolean postToNotificationDestinationSuccessful(CtlSecurityResponse ctlSecurityResponse) {
        return (ctlSecurityResponse != null && ctlSecurityResponse.isSuccessful());
    }
    private boolean postToNotificationDestinationSuccessful(ResponseEntity<String> responseEntity) {
        return (responseEntity != null && responseEntity.getStatusCode() != null && responseEntity.getStatusCode().is2xxSuccessful());
    }
}
