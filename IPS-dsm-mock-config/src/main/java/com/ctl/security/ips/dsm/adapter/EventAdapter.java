package com.ctl.security.ips.dsm.adapter;

import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import manager.FirewallEventListTransport;

/**
 * Created by Sean Robb on 3/30/2015.
 */
public interface EventAdapter {
    void postEvent(FirewallEvent firewallEvent);

    FirewallEventListTransport getEventTransportList();
}
