package com.ctl.security.ips.dsm.domain;

import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import manager.FirewallEventTransport;
import org.springframework.stereotype.Component;

/**
 * Created by Gary Hebel on 3/19/15.
 */
@Component
public class FirewallEventTransportMarshaller {

    public FirewallEventTransport convert(FirewallEvent firewallEvent) {
       FirewallEventTransport firewallEventTransport = new FirewallEventTransport();
       firewallEventTransport.setHostName(firewallEvent.getHostName());
       firewallEventTransport.setReason(firewallEvent.getReason());
       return firewallEventTransport;
    }

    public FirewallEvent convert(FirewallEventTransport firewallEventTransport) {
        FirewallEvent firewallEvent = new FirewallEvent();
        firewallEvent.setHostName(firewallEventTransport.getHostName());
        firewallEvent.setReason(firewallEventTransport.getReason());
        return firewallEvent;
    }
}
