package com.ctl.security.ips.client;

import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by sean.robb on 3/10/2015.
 */
@Component
public class EventClient {

    private static final Logger logger = Logger.getLogger(NotificationClient.class);

    @Autowired
    private ClientComponent clientComponent;

    public static final String EVENT = "event";

    @Value("${${spring.profiles.active:local}.ips.host}")
    private String hostUrl;

    @Autowired
    private RestTemplate restTemplate;

    public void notify(EventBean eventBean, String bearerToken) {
        String address = hostUrl + EVENT +"/"+ eventBean.getAccountId()+"/"+eventBean.getHostName();

        logger.log(Level.INFO, "Event Occurred: " + eventBean.getEvent().getMessage());

        HttpHeaders httpHeaders = clientComponent.createHeaders(bearerToken);
        restTemplate.exchange(address,
                HttpMethod.PUT, new HttpEntity<>(eventBean.getEvent(), httpHeaders), String.class);
    }
}
