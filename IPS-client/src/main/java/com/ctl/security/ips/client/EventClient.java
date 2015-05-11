package com.ctl.security.ips.client;

import com.ctl.security.ips.common.exception.IpsException;
import com.ctl.security.ips.common.jms.bean.EventBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by sean.robb on 3/10/2015.
 */
@Component
public class EventClient {

    private static final Logger logger = LogManager.getLogger(EventClient.class);

    @Autowired
    private ClientComponent clientComponent;

    public static final String EVENT = "events";

    @Value("${${spring.profiles.active:local}.ips.host}")
    private String hostUrl;

    @Autowired
    private RestTemplate restTemplate;

    public void notify(EventBean eventBean, String bearerToken) {
        String address = hostUrl + EVENT +"/"+ eventBean.getAccountId()+"/"+eventBean.getHostName();

        logger.info("Event Occurred: " + eventBean.getEvent().toString());
        HttpHeaders httpHeaders = clientComponent.createHeaders(bearerToken);

        ResponseEntity<String> responseEntity = restTemplate.exchange(address,
                HttpMethod.POST, new HttpEntity<>(eventBean.getEvent(), httpHeaders), String.class);

        if(!responseEntity.getStatusCode().is2xxSuccessful()){
            throw new IpsException("Failed to Post FirewallEvent to " + address);
        }
    }
}
