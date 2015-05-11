package com.ctl.security.ips.informant;

import com.ctl.security.clc.client.common.domain.ClcAuthenticationRequest;
import com.ctl.security.clc.client.common.domain.ClcAuthenticationResponse;
import com.ctl.security.clc.client.core.bean.AuthenticationClient;
import com.ctl.security.data.client.cmdb.UserClient;
import com.ctl.security.data.client.domain.user.UserResource;
import com.ctl.security.data.client.domain.user.UserResources;
import com.ctl.security.ips.client.EventClient;
import com.ctl.security.ips.common.domain.Event.DpiEvent;
import com.ctl.security.ips.common.domain.Event.Event;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.dsm.DsmEventClient;
import com.ctl.security.ips.dsm.exception.DsmEventClientException;
import com.ctl.security.ips.informant.service.Informant;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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

    @Mock
    private UserClient userClient;

    private String bearerToken;

    private final String lastExecutionDateString = String.valueOf(DateTime.now().minus(100).toDate().getTime());

    @Before
    public void init() {
        file = new File("lastExecutionTest.txt");
        ReflectionTestUtils.setField(classUnderTest, "file", file);
        ReflectionTestUtils.setField(classUnderTest, "DSM_LAGTIME_MIN", 0);
    }

    @Test
    public void inform_gathersEventsForDifferentAccounts() throws Exception {
        List<String> accountIds = getAccountIds(5);
        List<Event> events = new ArrayList<>();
        events.addAll(createDpiEvents(5));
        setUpMocksForEvents(accountIds, events);

        setUpMocksForUsers(accountIds);
        setUpMocksForInform();
        setupReadingLastExecutionDate(lastExecutionDateString);

        classUnderTest.inform();

        verifyEventsGatheredFor(accountIds);
    }

    @Test
    public void inform_gathersEventsAndSendsOneEvent() throws Exception {
        List<Event> events = new ArrayList<>();
        events.addAll(createDpiEvents(1));
        List<String> accountIds = getAccountIds(1);
        List<EventBean> eventBeans = createEventBeans(accountIds.get(0), events);


        setUpMocksForEvents(accountIds, events);

        setUpMocksForUsers(accountIds);
        setUpMocksForInform();
        setupReadingLastExecutionDate(lastExecutionDateString);

        classUnderTest.inform();

        verifyNotificationsOfEventBeans(eventBeans);
    }

    @Test
    public void inform_gathersEventsAndSendsAnArrayOfEvents() throws Exception {
        List<String> accountIds = getAccountIds(5);
        List<Event> events = new ArrayList<>();
        events.addAll(createDpiEvents(5));
        List<EventBean> eventBeans = createEventBeans(accountIds.get(0), events);

        setUpMocksForEvents(accountIds, events);

        setUpMocksForUsers(accountIds);
        setUpMocksForInform();

        setupReadingLastExecutionDate(lastExecutionDateString);

        classUnderTest.inform();

        verifyNotificationsOfEventBeans(eventBeans);
    }

    @Test
    public void inform_writesExecutionTimeToAFile() throws Exception {
        List<String> accountIds = getAccountIds(5);
        List<Event> events = new ArrayList<>();
        events.addAll(createDpiEvents(5));

        setUpMocksForEvents(accountIds, events);

        setUpMocksForUsers(accountIds);
        setUpMocksForInform();
        setupReadingLastExecutionDate(lastExecutionDateString);

        classUnderTest.inform();

        String dateReadString = FileUtils.readFileToString(file);
        assertNotNull(dateReadString);

        Date fromDate = new Date();
        fromDate.setTime(Long.parseLong(dateReadString));

        verifyEventsGatheredFor(accountIds);
    }

    @Test
    public void inform_readsExecutionTimeToAFile() throws Exception {
        List<String> accountIds = getAccountIds(5);
        List<Event> events = new ArrayList<>();
        events.addAll(createDpiEvents(5));

        setUpMocksForEvents(accountIds, events);

        setUpMocksForUsers(accountIds);
        setUpMocksForInform();
        setupReadingLastExecutionDate(lastExecutionDateString);

        classUnderTest.inform();

        Date lastExecutionDate = new Date();
        lastExecutionDate.setTime(Long.parseLong(lastExecutionDateString));
        verifyEventsGatheredFor(accountIds);
    }

    @Test
    public void inform_nullEventsAreGathered() throws Exception {
        List<String> accountIds = getAccountIds(5);
        List<Event> events = new ArrayList<>();
        events.addAll(createDpiEvents(5));

        setUpMocksForEvents(accountIds, events);

        setUpMocksForUsers(accountIds);
        setUpMocksForInform();
        setupReadingLastExecutionDate(lastExecutionDateString);

        classUnderTest.inform();

        verifyEventsGatheredFor(accountIds);
    }

    @Test
    public void inform_noEventsAreGathered() throws Exception {
        List<Event> events = new ArrayList<>();
        List<String> accountIds = getAccountIds(1);

        setUpMocksForEvents(accountIds, events);

        setUpMocksForUsers(accountIds);
        setUpMocksForInform();
        setupReadingLastExecutionDate(lastExecutionDateString);

        classUnderTest.inform();

        verifyEventsGatheredFor(accountIds);
    }

    @Test
    public void inform_couldNotWriteLastExecutionDate() throws Exception {
        List<Event> events = new ArrayList<>();
        List<String> accountIds = getAccountIds(1);

        setUpMocksForEvents(accountIds, events);
        setUpMocksForUsers(accountIds);
        setUpMocksForInform();
        setupReadingLastExecutionDate(lastExecutionDateString);
        setupWritingLastExecutionDateNoFileFailure();

        classUnderTest.inform();

        //TODO Verify that logging has been done
    }

    @Test
    public void inform_couldNotReadLastExecutionDateParseFailure() throws Exception {
        List<Event> events = new ArrayList<>();
        List<FirewallEvent> firewallEvents = new ArrayList<>();
        List<String> accountIds = getAccountIds(1);

        setUpMocksForEvents(accountIds, events);
        setUpMocksForUsers(accountIds);
        setUpMocksForInform();
        setupReadingLastExecutionDateParseFailure();

        classUnderTest.inform();
        verifyEventsGatheredFor(accountIds);
    }

    @Test
    public void inform_couldNotReadLastExecutionDateNoFileFailure() throws Exception {
        List<Event> events = new ArrayList<>();
        List<String> accountIds = getAccountIds(1);

        setUpMocksForEvents(accountIds, events);
        setUpMocksForUsers(accountIds);
        setUpMocksForInform();
        setupReadingLastExecutionDateNoFileFailure();

        classUnderTest.inform();
        verifyEventsGatheredFor(accountIds);
    }

    private void setUpMocksForInform() throws DsmEventClientException, IOException {
        when(authenticationClient.authenticateV2Api(any(ClcAuthenticationRequest.class)))
                .thenReturn(clcAuthenticationResponse);
        when(clcAuthenticationResponse.getBearerToken())
                .thenReturn(bearerToken);
        ReflectionTestUtils.setField(classUnderTest, "defaultGatheringLength", "1");
    }

    private void setEventsForAccountId(String accountId, List<Event> events) throws DsmEventClientException {
        when(dsmEventClient.gatherEvents(eq(accountId), any(Date.class), any(Date.class)))
                .thenReturn(events);
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

    private void verifyNotificationsOfEventBeans(List<EventBean> eventBeans) {
        for (EventBean currentEventBean : eventBeans) {
            verify(eventClient).notify(currentEventBean, bearerToken);
        }
    }

    private List<FirewallEvent> createFirewallEvents(int count) {
        List<FirewallEvent> firewallEvents = new ArrayList<>();
        for (int eventCount = 0; eventCount < count; eventCount++) {
            FirewallEvent firewallEvent = new FirewallEvent();
            firewallEvent.setHostName("Hostname" + eventCount);
            firewallEvent.setReason("Reason" + eventCount);
            firewallEvents.add(firewallEvent);
        }
        return firewallEvents;
    }

    private List<DpiEvent> createDpiEvents(int count) {
        List<DpiEvent> firewallEvents = new ArrayList<>();
        for (int eventCount = 0; eventCount < count; eventCount++) {
            DpiEvent firewallEvent = new DpiEvent();
            firewallEvent.setHostName("Hostname" + eventCount);
            firewallEvent.setReason("Reason" + eventCount);
            firewallEvents.add(firewallEvent);
        }
        return firewallEvents;
    }

    private List<EventBean> createEventBeans(String accountId, List<Event> events) {
        List<EventBean> eventBeans = new ArrayList<>();

        for (Event currentEvents : events) {
            EventBean eventBean = new EventBean(currentEvents.getHostName(), accountId, currentEvents);
            eventBeans.add(eventBean);
        }
        return eventBeans;
    }

    private List<String> getAccountIds(int amount) {
        List<String> accountIds = new ArrayList<>();
        for (int index = 0; index < amount; index++) {
            accountIds.add("Account" + index);
        }
        return accountIds;
    }

    private void verifyEventsGatheredFor(List<String> accountIds) throws DsmEventClientException {
        for (String currentAccountId : accountIds) {
            verify(dsmEventClient).gatherEvents(eq(currentAccountId), any(Date.class), any(Date.class));
        }
    }

    private void setUpMocksForEvents(List<String> accountIds, List<Event> events) throws DsmEventClientException {
        for (String currentAccount : accountIds) {
            setEventsForAccountId(currentAccount, events);
        }
    }

    private void setUpMocksForUsers(List<String> accountIds) {
        List<UserResource> userResourcesList = new ArrayList<>();

        for (String currentAccountId : accountIds) {
            UserResource newUserResource = new UserResource();
            newUserResource.getContent().setAccountId(currentAccountId);
            userResourcesList.add(newUserResource);
        }

        when(userClient.getAllUsers()).thenReturn(new UserResources(userResourcesList));
    }

}