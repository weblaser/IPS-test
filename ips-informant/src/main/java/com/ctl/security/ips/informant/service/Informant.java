package com.ctl.security.ips.informant.service;

import com.ctl.security.clc.client.common.domain.ClcAuthenticationRequest;
import com.ctl.security.clc.client.common.domain.ClcAuthenticationResponse;
import com.ctl.security.clc.client.core.bean.AuthenticationClient;
import com.ctl.security.ips.client.EventClient;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.dsm.DsmEventClient;
import com.ctl.security.ips.dsm.exception.DsmEventClientException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


/**
 * Created by Sean Robb on 3/24/2015.
 */

@Component
public class Informant {
    private static final String USERNAME = "Bugs";
    private static final String PASSWORD = "vZb]9yKv==Bnmozn";
    public static final String ACCOUNT = "TCCD";

    @Autowired
    private DsmEventClient dsmEventClient;

    @Autowired
    private EventClient eventClient;

    @Autowired
    private AuthenticationClient authenticationClient;

    private String authenticate() {
        ClcAuthenticationResponse clcAuthenticationResponse = authenticationClient
                .authenticateV2Api(new ClcAuthenticationRequest(USERNAME, PASSWORD));
        String bearerToken = clcAuthenticationResponse.getBearerToken();
        return bearerToken;
    }

    private void sendEvents(List<FirewallEvent> firewallEvents, String bearerToken) {
        for (FirewallEvent firewallEvent : firewallEvents) {
            //TODO retrieve Tenant Name from DSM
            String tenantName = ACCOUNT;
            EventBean eventBean = new EventBean(firewallEvent.getHostName(), tenantName, firewallEvent);
            eventClient.notify(eventBean, bearerToken);
        }

    }

    @Scheduled(cron = "${${spring.profiles.active}.informant.cron}")
    public void inform(){
        try {
            Date toDate = DateTime.now().toDate();

            Date fromDate = DateTime.now().minusMinutes(5).toDate();
            List<FirewallEvent> events = dsmEventClient.gatherEvents(fromDate, toDate);
            String bearerToken = authenticate();
            sendEvents(events, bearerToken);
        } catch (DsmEventClientException e) {
            e.printStackTrace();
        }
    }

}
