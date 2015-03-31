package com.ctl.security.ips.dsm.adapter;

import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.dsm.domain.FirewallEventTransportMarshaller;
import manager.ArrayOfFirewallEventTransport;
import manager.FirewallEventListTransport;
import manager.FirewallEventTransport;

/**
 * Created by Sean Robb on 3/30/2015.
 */

public class MockEventAdapterImpl implements EventAdapter {

    private FirewallEventTransportMarshaller firewallEventTransportMarshaller;

    private FirewallEventListTransport firewallEventListTransport;

    @Override
    public void postEvent(FirewallEvent firewallEvent) {
        FirewallEventTransport firewallEventTransport = firewallEventTransportMarshaller.convert(firewallEvent);
        ArrayOfFirewallEventTransport arrayOfFirewallEventTransport = new ArrayOfFirewallEventTransport();
        arrayOfFirewallEventTransport = firewallEventListTransport.getFirewallEvents();
        arrayOfFirewallEventTransport.getItem().add(firewallEventTransport);
        firewallEventListTransport.setFirewallEvents(arrayOfFirewallEventTransport);
    }

    @Override
    public FirewallEventListTransport getEventTransportList() {
        return firewallEventListTransport;
    }


}
