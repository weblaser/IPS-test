package com.ctl.security.ips.test.cucumber.adapter;

import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.dsm.adapter.EventAdapter;
import manager.FirewallEventListTransport;

/**
 * Created by Sean Robb on 3/30/2015.
 */


public class EventAdapterImpl implements EventAdapter {

    @Override
    public void postEvent(FirewallEvent firewallEvent) {

    }

    @Override
    public FirewallEventListTransport getEventTransportList() {
        return null;
    }
}
