package com.ctl.security.ips.dsm.domain;

import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import manager.FirewallEventTransport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Gary Hebel on 3/19/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class FirewallEventTransportMarshallerTest {

    @InjectMocks
    private FirewallEventTransportMarshaller classUnderTest;

    @Test
    public void convert_convertsFromFirewallEventToFirewallEventTransport() {
        FirewallEvent firewallEvent = new FirewallEvent();
        firewallEvent.setHostName("hostname");
        firewallEvent.setReason("because");
        FirewallEventTransport firewallEventTransport = classUnderTest.convert(firewallEvent);

        assertNotNull(firewallEventTransport);
        assertEquals(firewallEvent.getHostName(), firewallEventTransport.getHostName());
        assertEquals(firewallEvent.getReason(), firewallEventTransport.getReason());
    }

    @Test
    public void convert_convertsFromFirewallEventTransportToFirewallEvent() {
        FirewallEventTransport firewallEventTransport = new FirewallEventTransport();
        firewallEventTransport.setHostName("hostname");
        firewallEventTransport.setReason("because");
        FirewallEvent firewallEvent = classUnderTest.convert(firewallEventTransport);

        assertNotNull(firewallEvent);
        assertEquals(firewallEventTransport.getHostName(), firewallEvent.getHostName());
        assertEquals(firewallEventTransport.getReason(), firewallEvent.getReason());
    }
}
