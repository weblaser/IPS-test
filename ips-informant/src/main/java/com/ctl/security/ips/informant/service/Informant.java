package com.ctl.security.ips.informant.service;

import com.ctl.security.clc.client.common.domain.ClcAuthenticationRequest;
import com.ctl.security.clc.client.common.domain.ClcAuthenticationResponse;
import com.ctl.security.clc.client.core.bean.AuthenticationClient;
import com.ctl.security.data.client.cmdb.UserClient;
import com.ctl.security.data.client.domain.user.UserResource;
import com.ctl.security.data.client.domain.user.UserResources;
import com.ctl.security.ips.client.EventClient;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.dsm.DsmEventClient;
import com.ctl.security.ips.dsm.exception.DsmEventClientException;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Sean Robb on 3/24/2015.
 *
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
    private UserClient userClient;

    @Autowired
    private File file;

    @Value("${${spring.profiles.active:local}.informant.defaultGatherLength}")
    private String defaultGatheringLength;

    @Value("${${spring.profiles.active:local}.informant.dsm_lag}")
    public Integer DSM_LAGTIME_MIN;

    private String authenticate() {
        ClcAuthenticationResponse clcAuthenticationResponse = authenticationClient
                .authenticateV2Api(new ClcAuthenticationRequest(USERNAME, PASSWORD));
        String bearerToken = clcAuthenticationResponse.getBearerToken();
        return bearerToken;
    }

    private void sendEvents(List<EventBean> firewallEventBeans, String bearerToken) {
        for (EventBean firewallEventBean : firewallEventBeans) {
            eventClient.notify(firewallEventBean, bearerToken);
        }
    }

    @Scheduled(cron = "${${spring.profiles.active}.informant.cron}")
    public void inform() {

        Date fromDate = new Date();
        Date toDate = DateTime.now().minusMinutes(DSM_LAGTIME_MIN).toDate();
        try {
            String ticksReadString = FileUtils.readFileToString(file);
            if (ticksReadString == null) {
                ticksReadString = "";
            }
            fromDate.setTime(Long.parseLong(ticksReadString));

            if(fromDate.after(toDate)){
                //TODO make this exception more specific
                throw new Exception("The From Date can not be after the To Date.");
            }

        } catch (Exception e) {
            fromDate = DateTime.now().minusHours(Integer.parseInt(defaultGatheringLength)).toDate();
            //TODO Log that file was not found
        }

        List<EventBean> events = new ArrayList<>();

        UserResources allUsers = userClient.getAllUsers();

        for (UserResource userResource : allUsers.getContent()) {
            try {
                String accountId = userResource.getContent().getAccountId();
                List<FirewallEvent> currentEvents = dsmEventClient.gatherEvents(accountId, fromDate, toDate);

                for (FirewallEvent firewallEvent : currentEvents) {
                    EventBean eventBean = new EventBean(firewallEvent.getHostName(), accountId, firewallEvent);
                    events.add(eventBean);
                }
            } catch (DsmEventClientException e) {
                e.printStackTrace();
                //TODO log that eventClient had errors gathering events
            }
        }

        if (!events.isEmpty()) {
            String bearerToken = authenticate();
            sendEvents(events, bearerToken);
        }

        try {
            FileUtils.writeStringToFile(file, String.valueOf(toDate.getTime()));
        } catch (IOException e) {
            e.printStackTrace();
            //TODO log that file failed to write the to date
        }

    }

}
