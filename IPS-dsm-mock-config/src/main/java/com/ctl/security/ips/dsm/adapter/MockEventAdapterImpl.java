package com.ctl.security.ips.dsm.adapter;

import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.dsm.domain.FirewallEventTransportMarshaller;
import manager.ArrayOfFirewallEventTransport;
import manager.FirewallEventListTransport;
import manager.FirewallEventTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Sean Robb on 3/30/2015.
 */
@Component("MockEventAdapter")
public class MockEventAdapterImpl implements EventAdapter {

    @Autowired
    private FirewallEventTransportMarshaller firewallEventTransportMarshaller;

    private FirewallEventListTransport firewallEventListTransport;

    public MockEventAdapterImpl() {
        firewallEventListTransport = new FirewallEventListTransport();
        firewallEventListTransport.setFirewallEvents(new ArrayOfFirewallEventTransport());
    }

    @Override
    public void triggerEvent(FirewallEvent firewallEvent) {
        FirewallEventTransport firewallEventTransport = firewallEventTransportMarshaller.convert(firewallEvent);
        firewallEventListTransport.getFirewallEvents()
                .getItem()
                .add(firewallEventTransport);
    }

    @Override
    public FirewallEventListTransport getEventTransportList() {
        return firewallEventListTransport;
    }


}
