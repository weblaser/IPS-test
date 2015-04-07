package com.ctl.security.ips.informant.service;

import com.ctl.security.clc.client.common.domain.ClcAuthenticationRequest;
import com.ctl.security.clc.client.common.domain.ClcAuthenticationResponse;
import com.ctl.security.clc.client.core.bean.AuthenticationClient;
import com.ctl.security.ips.client.EventClient;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.dsm.DsmEventClient;
import com.ctl.security.ips.dsm.exception.DsmEventClientException;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Autowired
    private File file;

    @Value("${${spring.profiles.active:local}.informant.defaultGatherLength}")
    private String defaultGatheringLength;

    private String authenticate() {
        ClcAuthenticationResponse clcAuthenticationResponse = authenticationClient
                .authenticateV2Api(new ClcAuthenticationRequest(USERNAME, PASSWORD));
        String bearerToken = clcAuthenticationResponse.getBearerToken();
        return bearerToken;
    }

    private void sendEvents(List<FirewallEvent> firewallEvents, String bearerToken) {
        for (FirewallEvent firewallEvent : firewallEvents) {
            String tenantName = ACCOUNT;
            EventBean eventBean = new EventBean(firewallEvent.getHostName(), tenantName, firewallEvent);
            eventClient.notify(eventBean, bearerToken);
        }
    }

    @Scheduled(cron = "${${spring.profiles.active}.informant.cron}")
    public void inform() {
        try {
            Date toDate = new Date();
            Date fromDate = new Date();
            try {
                String ticksReadString = FileUtils.readFileToString(file);
                if (ticksReadString == null) {
                    ticksReadString = "";
                }
                fromDate.setTime(Long.parseLong(ticksReadString));

            } catch (IOException | IllegalArgumentException e) {
                fromDate = DateTime.now().minusHours(Integer.parseInt(defaultGatheringLength)).toDate();
                //TODO Log that file was not found
            }

            List<FirewallEvent> events = dsmEventClient.gatherEvents(fromDate, toDate);

            if (events != null) {
                String bearerToken = authenticate();
                sendEvents(events, bearerToken);
            }

            FileUtils.writeStringToFile(file, String.valueOf(toDate.getTime()));

        } catch (DsmEventClientException | IOException e) {
            e.printStackTrace();
        }
    }

}
