package com.ctl.security.ips.client;

import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by sean.robb on 3/5/2015.
 */

@Component
public class NotificationClient {

    private static final Logger logger = LogManager.getLogger(NotificationClient.class);

    @Autowired
    private ClientComponent clientComponent;

    public static final String NOTIFICATIONS = "notifications";

    @Value("${${spring.profiles.active:local}.ips.host}")
    private String hostUrl;

    @Autowired
    private RestTemplate restTemplate;

    public void updateNotificationDestination(NotificationDestinationBean notificationDestinationBean, String bearerToken) {
            String address = hostUrl + NOTIFICATIONS +"/"+ notificationDestinationBean.getAccountId()+"/"+notificationDestinationBean.getHostName();
            logger.info("updatePolicyForAccount: " + address);

        HttpHeaders httpHeaders = clientComponent.createHeaders(bearerToken);
        restTemplate.exchange(address,
                    HttpMethod.PUT, new HttpEntity<>(notificationDestinationBean.getNotificationDestinations(), httpHeaders), String.class);
    }
}
