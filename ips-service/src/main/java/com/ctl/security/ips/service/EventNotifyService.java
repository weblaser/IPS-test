package com.ctl.security.ips.service;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.service.config.IpsServiceConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Value("${${spring.profiles.active:local}.ips.maxRetryAttempts}")
    private Integer maxRetryAttempts;

    @Value("${${spring.profiles.active:local}.ips.retryWaitTime}")
    private Integer retryWaitTime;


    private static final Logger logger = Logger.getLogger(EventNotifyService.class);

    public void notify(EventBean eventBean){

            List<NotificationDestination> notificationDestinations = configurationItemClient
                    .getConfigurationItem(eventBean.getHostName(), eventBean.getAccountId())
                    .getContent()
                    .getAccount()
                    .getNotificationDestinations();

            for (NotificationDestination notification : notificationDestinations) {

                ResponseEntity<String> responseEntity=new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
                Integer retryAttempts=0;

                while((!responseEntity.getStatusCode().is2xxSuccessful()) && (retryAttempts < maxRetryAttempts))
                {
                    try {
                        responseEntity = restTemplate.exchange(
                                notification.getUrl(),
                                HttpMethod.POST,
                                new HttpEntity<>(eventBean.getEvent().getMessage()),
                                String.class
                        );
                        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                            logger.info("Failed to Send Notification to " + notification.getUrl());
                        }
                    }catch(Exception e)
                    {
                        logger.info("Exception in Send Notification to " + notification.getUrl(), e);
                    }
                    retryAttempts++;
                    try {Thread.sleep(retryWaitTime);}catch(Exception e){}
                }
                if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                    logger.error("Failed to Send Notification to "+ notification.getUrl());
                }
            }
    }
}
