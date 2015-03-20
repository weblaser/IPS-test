package com.ctl.security.ips.dsm;

import com.ctl.security.ips.client.EventClient;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.dsm.domain.FirewallEventTransportMarshaller;
import com.ctl.security.ips.dsm.domain.SecurityProfileTransportMarshaller;
import junit.framework.TestCase;
import manager.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DsmEventClientTest extends TestCase {

    @InjectMocks
    private DsmEventClient classUnderTest;

    @Mock
    private Manager manager;

    @Mock
    private DsmLogInClient dsmLogInClient;

    @Mock
    private FirewallEventTransportMarshaller firewallEventTransportMarshaller;

    @Mock
    private EventClient eventClient;

    @Mock
    private EventBean eventBean;

    private String username = "joe";
    private String password = "password";
    private String sessionId = "12345";
    private String bearerToken = "bearerToken";

    @Before
    public void setup() throws Exception {
        setupUsernamePasswordWhen(username, password, sessionId);
    }

    private void setupUsernamePasswordWhen(String username, String password, String sessionId) throws Exception {
        ReflectionTestUtils.setField(classUnderTest, "username", username);
        ReflectionTestUtils.setField(classUnderTest, "password", password);

        when(dsmLogInClient.connectToDSMClient(eq(username), eq(password))).thenReturn(sessionId);
    }

    @Test
    public void gatherEvents_gathersEventsFromDsm() throws Exception {
        FirewallEventListTransport firewallEventListTransport = mock(FirewallEventListTransport.class);
        ArrayOfFirewallEventTransport arrayOfFirewallEventTransport = mock(ArrayOfFirewallEventTransport.class);

        FirewallEventTransport firewallEventTransport1 = new FirewallEventTransport();
        FirewallEventTransport firewallEventTransport2 = new FirewallEventTransport();
        FirewallEvent firewallEvent1 = new FirewallEvent();
        FirewallEvent firewallEvent2 = new FirewallEvent();
        List<FirewallEventTransport> firewallEventTransports = new ArrayList();
        firewallEventTransports.add(firewallEventTransport1);
        firewallEventTransports.add(firewallEventTransport2);

        when(manager.firewallEventRetrieve(any(TimeFilterTransport.class),
                any(HostFilterTransport.class),
                any(IDFilterTransport.class),
                anyString()))
                .thenReturn(firewallEventListTransport);
        when(firewallEventListTransport.getFirewallEvents())
                .thenReturn(arrayOfFirewallEventTransport);
        when(arrayOfFirewallEventTransport.getItem())
                .thenReturn(firewallEventTransports);
        when(firewallEventTransportMarshaller.convert(firewallEventTransport1))
                .thenReturn(firewallEvent1);
        when(firewallEventTransportMarshaller.convert(firewallEventTransport2))
                .thenReturn(firewallEvent2);

        List<FirewallEvent> events = classUnderTest.gatherEvents(new Date(), new Date());
        assertNotNull(events);
        assertEquals(firewallEventTransports.size(), events.size());
        verify(firewallEventTransportMarshaller).convert(firewallEventTransport1);
        verify(firewallEventTransportMarshaller).convert(firewallEventTransport2);
    }

    @Test
    public void sendEvents_sendsEventsToAPIEndPoint() {
        FirewallEvent event1 = new FirewallEvent();
        FirewallEvent event2 = new FirewallEvent();
        event1.setHostName("Test Host Name 1");
        event1.setReason("Cause 1");
        event2.setHostName("Test Host Name 2");
        event2.setReason("Cause 2");

        List<FirewallEvent> events = new ArrayList<FirewallEvent>();
        events.add(event1);
        events.add(event2);

        doNothing().when(eventClient).notify(any(EventBean.class), anyString());

        classUnderTest.sendEvents(events,bearerToken);

        verify(eventClient, times(events.size())).notify(any(EventBean.class), eq(bearerToken));
    }


}