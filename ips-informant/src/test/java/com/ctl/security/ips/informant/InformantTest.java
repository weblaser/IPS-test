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
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doThrow;
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

    @Mock
    private File file;

    private String bearerToken;

    private final String lastExecutionDateString = String.valueOf(DateTime.now().minus(100).toDate().getTime());

    @Before
    public void init() {
        file = new File("lastExecutionTest.txt");
        ReflectionTestUtils.setField(classUnderTest, "file", file);
    }

    @Test
    public void run_gathersEvents() throws Exception {
        FirewallEvent firewallEvent = createFirewallEvent("hostName1");

        List<FirewallEvent> firewallEvents = Arrays.asList(firewallEvent);

        setUpMocksForInform(firewallEvents);
        setupReadingLastExecutionDate(lastExecutionDateString);

        classUnderTest.inform();

        verify(dsmEventClient).gatherEvents(any(Date.class), any(Date.class));
    }

    @Test
    public void inform_gathersEventsAndSendsOneEvent() throws Exception {
        List<FirewallEvent> firewallEvents = CreateFirewallEvents(1);
        List<EventBean> eventBeans = createEventBeans(firewallEvents);

        setUpMocksForInform(firewallEvents);
        setupReadingLastExecutionDate(lastExecutionDateString);

        classUnderTest.inform();

        verifyNotificationsOfEventBeans(eventBeans);
    }

    @Test
    public void inform_gathersEventsAndSendsAnArrayOfEvents() throws Exception {
        List<FirewallEvent> firewallEvents = CreateFirewallEvents(5);
        List<EventBean> eventBeans = createEventBeans(firewallEvents);

        setUpMocksForInform(firewallEvents);
        setupReadingLastExecutionDate(lastExecutionDateString);

        classUnderTest.inform();

        verifyNotificationsOfEventBeans(eventBeans);
    }

    @Test
    public void inform_writesExecutionTimeToAFile() throws Exception {
        setUpMocksForInform(null);
        setupReadingLastExecutionDate(lastExecutionDateString);

        classUnderTest.inform();

        String dateReadString = FileUtils.readFileToString(file);
        assertNotNull(dateReadString);

        Date fromDate = new Date();
        fromDate.setTime(Long.parseLong(dateReadString));

        verify(dsmEventClient).gatherEvents(any(Date.class), eq(fromDate));
    }

    @Test
    public void inform_readsExecutionTimeToAFile() throws Exception {
        setUpMocksForInform(null);
        setupReadingLastExecutionDate(lastExecutionDateString);

        classUnderTest.inform();

        Date lastExecutionDate = new Date();
        lastExecutionDate.setTime(Long.parseLong(lastExecutionDateString));
        verify(dsmEventClient).gatherEvents(eq(lastExecutionDate), any(Date.class));
    }

    @Test
    public void inform_nullEventsAreGathered() throws Exception {
        setUpMocksForInform(null);
        setupReadingLastExecutionDate(lastExecutionDateString);

        classUnderTest.inform();

        verify(dsmEventClient).gatherEvents(any(Date.class), any(Date.class));
    }

    @Test
    public void inform_noEventsAreGathered() throws Exception {
        List<FirewallEvent> firewallEvents = new ArrayList<>();
        setUpMocksForInform(firewallEvents);
        setupReadingLastExecutionDate(lastExecutionDateString);

        classUnderTest.inform();

        verify(dsmEventClient).gatherEvents(any(Date.class), any(Date.class));
    }

    @Test
    public void inform_couldNotWriteLastExecutionDate() throws Exception {
        setUpMocksForInform(null);
        setupReadingLastExecutionDate(lastExecutionDateString);
        setupWritingLastExecutionDateNoFileFailure();

        classUnderTest.inform();

        //TODO Verify that logging has been done
    }

    @Test
    public void inform_couldNotReadLastExecutionDateParseFailure() throws Exception {
        setUpMocksForInform(null);
        setupReadingLastExecutionDateParseFailure();

        classUnderTest.inform();
        verify(dsmEventClient).gatherEvents(any(Date.class), any(Date.class));
    }

    @Test
    public void inform_couldNotReadLastExecutionDateNoFileFailure() throws Exception {
        setUpMocksForInform(null);
        setupReadingLastExecutionDateNoFileFailure();

        classUnderTest.inform();
        verify(dsmEventClient).gatherEvents(any(Date.class), any(Date.class));
    }

    private void setUpMocksForInform(List<FirewallEvent> firewallEvents) throws DsmEventClientException, IOException {
        when(dsmEventClient.gatherEvents(any(Date.class), any(Date.class)))
                .thenReturn(firewallEvents);
        when(authenticationClient.authenticateV2Api(any(ClcAuthenticationRequest.class)))
                .thenReturn(clcAuthenticationResponse);
        when(clcAuthenticationResponse.getBearerToken())
                .thenReturn(bearerToken);
        ReflectionTestUtils.setField(classUnderTest, "defaultGatheringLength", "1");
    }

    private void setupReadingLastExecutionDate(String lastExecutionDateString) throws IOException {
        FileUtils.write(file, lastExecutionDateString, false);
    }

    private void setupReadingLastExecutionDateParseFailure() throws IOException {
        FileUtils.write(file, "FAILURE", false);
    }

    private void setupReadingLastExecutionDateNoFileFailure() throws IOException {
        FileUtils.forceDelete(file);
    }

    private void setupWritingLastExecutionDateNoFileFailure() throws IOException {
        FileUtils.forceDelete(file);
    }

    private FirewallEvent createFirewallEvent(String hostName) {
        FirewallEvent firewallEvent = new FirewallEvent();
        firewallEvent.setHostName(hostName);
        return firewallEvent;
    }

    private EventBean createEventBean(FirewallEvent firewallEvent) {
        EventBean eventBean = new EventBean(firewallEvent.getHostName(), Informant.ACCOUNT, firewallEvent);
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

    private List<EventBean> createEventBeans(List<FirewallEvent> firewallEvents) {
        List<EventBean> eventBeans = new ArrayList<>();

        for (FirewallEvent currentFirewallEvent : firewallEvents) {
            EventBean eventBean = createEventBean(currentFirewallEvent);
            eventBeans.add(eventBean);
        }
        return eventBeans;
    }

}