package com.ctl.security.ips.informant;

import com.ctl.security.clc.client.common.domain.ClcAuthenticationRequest;
import com.ctl.security.clc.client.common.domain.ClcAuthenticationResponse;
import com.ctl.security.clc.client.core.bean.AuthenticationClient;
import com.ctl.security.ips.client.EventClient;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.dsm.DsmEventClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.JobExecutionContext;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InformantTest {

    @InjectMocks
    Informant classUnderTest;

    @Mock
    DsmEventClient dsmEventClient;

    @Mock
    EventClient eventClient;

    @Mock
    private AuthenticationClient authenticationClient;

    @Mock
    private ClcAuthenticationResponse clcAuthenticationResponse;

    private String bearerToken;

    @Test
    public void run_gathersEventsAndSendsEvents() throws Exception {
        String tenantName = Informant.TCCD;
        JobExecutionContext jobExecutionContext=null;
        FirewallEvent firewallEvent1 = new FirewallEvent();
        firewallEvent1.setHostName("hostName1");
        FirewallEvent firewallEvent2 = new FirewallEvent();
        firewallEvent2.setHostName("hostName2");
        List<FirewallEvent> firewallEvents = Arrays.asList(firewallEvent1, firewallEvent2);
        EventBean eventBean1 = new EventBean(firewallEvent1.getHostName(), tenantName, firewallEvent1);
        EventBean eventBean2 = new EventBean(firewallEvent2.getHostName(), tenantName, firewallEvent2);

        when(dsmEventClient.gatherEvents(any(Date.class), any(Date.class)))
                .thenReturn(firewallEvents);
        when(authenticationClient.authenticateV2Api(any(ClcAuthenticationRequest.class)))
                .thenReturn(clcAuthenticationResponse);
        when(clcAuthenticationResponse.getBearerToken())
                .thenReturn(bearerToken);

        classUnderTest.execute(jobExecutionContext);

        verify(dsmEventClient).gatherEvents(any(Date.class), any(Date.class));
        verify(authenticationClient).authenticateV2Api(any(ClcAuthenticationRequest.class));
        verify(eventClient).notify(eventBean1, bearerToken);
        verify(eventClient).notify(eventBean2, bearerToken);
    }

}