package com.ctl.security.ips.informant;

import com.ctl.security.clc.client.common.domain.ClcAuthenticationRequest;
import com.ctl.security.clc.client.common.domain.ClcAuthenticationResponse;
import com.ctl.security.clc.client.core.bean.AuthenticationClient;
import com.ctl.security.ips.client.EventClient;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.dsm.DsmEventClient;
import com.ctl.security.ips.dsm.exception.DsmEventClientException;
import com.ctl.security.ips.informant.service.Informant;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InformantTest {

    @InjectMocks
    private Informant classUnderTest;

    @Mock
    private DsmEventClient dsmEventClient;

    @Mock
    private EventClient eventClient;

    @Mock
    private AuthenticationClient authenticationClient;

    @Mock
    private ClcAuthenticationResponse clcAuthenticationResponse;

    private String bearerToken;

    @Test
    public void run_gathersEvents() throws Exception {
        FirewallEvent firewallEvent = createFirewallEvent("hostName1");

        List<FirewallEvent> firewallEvents = Arrays.asList(firewallEvent);

        setUpMocksForInform(firewallEvents);

        classUnderTest.inform();

        verify(dsmEventClient).gatherEvents(any(Date.class), any(Date.class));
    }

    @Test
    public void run_gathersEventsAndSendsOneEvent() throws Exception {
        List<FirewallEvent> firewallEvents = CreateFirewallEvents(1);
        List<EventBean> eventBeans = createEventBeans(firewallEvents);

        setUpMocksForInform(firewallEvents);

        classUnderTest.inform();

        verifyNotificationsOfEventBeans(eventBeans);
    }

    @Test
    public void run_gathersEventsAndSendsAnArrayOfEvents() throws Exception {
        List<FirewallEvent> firewallEvents = CreateFirewallEvents(5);
        List<EventBean> eventBeans = createEventBeans(firewallEvents);

        setUpMocksForInform(firewallEvents);

        classUnderTest.inform();

        verifyNotificationsOfEventBeans(eventBeans);
    }

    private void setUpMocksForInform(List<FirewallEvent> firewallEvents) throws DsmEventClientException {
        when(dsmEventClient.gatherEvents(any(Date.class), any(Date.class)))
                .thenReturn(firewallEvents);
        when(authenticationClient.authenticateV2Api(any(ClcAuthenticationRequest.class)))
                .thenReturn(clcAuthenticationResponse);
        when(clcAuthenticationResponse.getBearerToken())
                .thenReturn(bearerToken);
    }

    private FirewallEvent createFirewallEvent(String hostName) {
        FirewallEvent firewallEvent = new FirewallEvent();
        firewallEvent.setHostName(hostName);
        return firewallEvent;
    }

    private EventBean createEventBean(FirewallEvent firewallEvent) {
        EventBean eventBean = new EventBean(firewallEvent.getHostName(), Informant.TCCD, firewallEvent);
        return eventBean;
    }

    private void verifyNotificationsOfEventBeans(List<EventBean> eventBeans) {
        for (EventBean currentEventBean : eventBeans) {
            verify(eventClient).notify(currentEventBean, bearerToken);
        }
    }

    private List<FirewallEvent> CreateFirewallEvents(int count) {
        List<FirewallEvent> firewallEvents = new ArrayList<>();
        List<EventBean> eventBeans = new ArrayList<>();
        for (int eventCount = 0; eventCount < count; eventCount++) {
            FirewallEvent firewallEvent = createFirewallEvent("Host Name " + eventCount);
            firewallEvents.add(firewallEvent);

            EventBean eventBean = createEventBean(firewallEvent);
            eventBeans.add(eventBean);
        }
        return firewallEvents;
    }

    private   List<EventBean> createEventBeans(List<FirewallEvent> firewallEvents){
        List<EventBean> eventBeans = new ArrayList<>();

        for (FirewallEvent currentFirewallEvent : firewallEvents) {
            EventBean eventBean = createEventBean(currentFirewallEvent);
            eventBeans.add(eventBean);
        }
        return eventBeans;
    }

}