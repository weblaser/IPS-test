package com.ctl.security.ips.informant.service;

import com.ctl.security.ips.dsm.DsmEventClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by Sean Robb on 3/24/2015.
 */

@Component
//public class Informant implements Job {
public class Informant {
    private static final String USERNAME = "kweber.tccd";
    private static final String PASSWORD = "1qaz@WSX";
    public static final String TCCD = "TCCD";

    @Autowired
    private DsmEventClient dsmEventClient;

    public Informant(){
        System.out.println("hello");
    }
//
//    @Autowired
//    private EventClient eventClient;
//
//    @Autowired
//    private AuthenticationClient authenticationClient;
//
//    private String authenticate() {
//        ClcAuthenticationResponse clcAuthenticationResponse = authenticationClient
//                .authenticateV2Api(new ClcAuthenticationRequest(USERNAME, PASSWORD));
//        String bearerToken = clcAuthenticationResponse.getBearerToken();
//        return bearerToken;
//    }
//
//    private void sendEvents(List<FirewallEvent> firewallEvents, String bearerToken) {
//        for (FirewallEvent firewallEvent : firewallEvents) {
//            //TODO retrieve Tenant Name from DSM
//            String tenantName = TCCD;
//            EventBean eventBean = new EventBean(firewallEvent.getHostName(), tenantName, firewallEvent);
//            eventClient.notify(eventBean, bearerToken);
//        }
//
//    }
//
////    @Override
//    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        try {
//            Date toDate = DateTime.now().minusMinutes(5).toDate();
//            Date formDate = DateTime.now().toDate();
//            List<FirewallEvent> events = dsmEventClient.gatherEvents(formDate, toDate);
//            String bearerToken = authenticate();
//            sendEvents(events, bearerToken);
//        } catch (DsmEventClientException e) {
//            e.printStackTrace();
//        }
//    }

}
