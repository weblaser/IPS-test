package com.ctl.security.ips.test.cucumber.adapter;

import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.dsm.adapter.EventAdapter;
import manager.FirewallEventListTransport;
import org.springframework.stereotype.Component;

/**
 * Created by Sean Robb on 3/30/2015.
 */

@Component("EventAdapter")
public class EventAdapterImpl implements EventAdapter {

    @Override
    public void triggerEvent(FirewallEvent firewallEvent) {

    }

    @Override
    public FirewallEventListTransport getEventTransportList() {
        return null;
    }
}
