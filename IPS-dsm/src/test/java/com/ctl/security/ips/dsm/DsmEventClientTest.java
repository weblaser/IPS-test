package com.ctl.security.ips.dsm;

import com.ctl.security.ips.client.EventClient;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.dsm.domain.FirewallEventTransportMarshaller;
import com.ctl.security.ips.dsm.exception.DsmEventClientException;
import junit.framework.TestCase;
import manager.*;
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
    private String exceptionTestMessage ="This is a Test";

    private void setupUsernamePasswordWhen(String username, String password, String sessionId) throws Exception {
        ReflectionTestUtils.setField(classUnderTest, "username", username);
        ReflectionTestUtils.setField(classUnderTest, "password", password);

        when(dsmLogInClient.connectToDSMClient(eq(username), eq(password))).thenReturn(sessionId);
    }

    private void setupUsernamePasswordWhen(String username, String password, Throwable throwable) throws Exception {
        ReflectionTestUtils.setField(classUnderTest, "username", username);
        ReflectionTestUtils.setField(classUnderTest, "password", password);

        when(dsmLogInClient.connectToDSMClient(eq(username), eq(password))).thenThrow(throwable);
    }

    @Test
    public void gatherEvents_gathersEventsFromDsm() throws Exception {

        setupUsernamePasswordWhen(username, password, sessionId);

        FirewallEventListTransport firewallEventListTransport = mock(FirewallEventListTransport.class);
        ArrayOfFirewallEventTransport arrayOfFirewallEventTransport = mock(ArrayOfFirewallEventTransport.class);

        FirewallEventTransport firewallEventTransport1 = new FirewallEventTransport();
        FirewallEventTransport firewallEventTransport2 = new FirewallEventTransport();
        List<FirewallEventTransport> firewallEventTransports = new ArrayList();
        firewallEventTransports.add(firewallEventTransport1);
        firewallEventTransports.add(firewallEventTransport2);

        FirewallEvent firewallEvent1 = new FirewallEvent();
        FirewallEvent firewallEvent2 = new FirewallEvent();

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
        for (FirewallEventTransport firewallEventTransport : firewallEventTransports) {
            verify(firewallEventTransportMarshaller).convert(firewallEventTransport);
        }


        verify(dsmLogInClient).endSession(sessionId);
    }

    @Test
    public void gatherEvents_throws_ManagerException_Exception_GetFirewallEvents() throws Exception {
        DsmEventClientException dsmEventClientException = null;

        setupUsernamePasswordWhen(username, password, sessionId);

        ManagerException_Exception managerException_exception = new ManagerException_Exception(exceptionTestMessage);

        when(manager.firewallEventRetrieve(any(TimeFilterTransport.class),
                any(HostFilterTransport.class),
                any(IDFilterTransport.class),
                anyString())).thenThrow(managerException_exception);

        try {
            List<FirewallEvent> events = classUnderTest.gatherEvents(new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException, managerException_exception,sessionId);
    }

    @Test
    public void gatherEvents_throws_ManagerAuthenticationException_Exception_GetFirewallEvents() throws Exception {
        DsmEventClientException dsmEventClientException = null;

        setupUsernamePasswordWhen(username, password, sessionId);

        ManagerAuthenticationException_Exception managerAuthenticationException_exception;
        managerAuthenticationException_exception = new ManagerAuthenticationException_Exception(exceptionTestMessage);


        when(manager.firewallEventRetrieve(any(TimeFilterTransport.class),
                any(HostFilterTransport.class),
                any(IDFilterTransport.class),
                anyString())).thenThrow(managerAuthenticationException_exception);


        try {
            List<FirewallEvent> events = classUnderTest.gatherEvents(new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException,managerAuthenticationException_exception,sessionId);
    }

    @Test
    public void gatherEvents_throws_ManagerTimeoutException_Exception_GetFirewallEvents() throws Exception {
        DsmEventClientException dsmEventClientException = null;

        setupUsernamePasswordWhen(username, password, sessionId);

        ManagerTimeoutException_Exception managerTimeoutException_exception;
        managerTimeoutException_exception = new ManagerTimeoutException_Exception(exceptionTestMessage);

        when(manager.firewallEventRetrieve(any(TimeFilterTransport.class),
                any(HostFilterTransport.class),
                any(IDFilterTransport.class),
                anyString())).thenThrow(managerTimeoutException_exception);


        try {
            List<FirewallEvent> events = classUnderTest.gatherEvents(new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException,managerTimeoutException_exception,sessionId);
    }

    @Test
    public void gatherEvents_throws_ManagerValidationException_Exception_GetFirewallEvents() throws Exception {
        DsmEventClientException dsmEventClientException = null;

        setupUsernamePasswordWhen(username, password, sessionId);

        ManagerValidationException_Exception managerValidationException_exception;
        managerValidationException_exception = new ManagerValidationException_Exception(exceptionTestMessage);
        when(manager.firewallEventRetrieve(any(TimeFilterTransport.class),
                any(HostFilterTransport.class),
                any(IDFilterTransport.class),
                anyString())).thenThrow(managerValidationException_exception);


        try {
            List<FirewallEvent> events = classUnderTest.gatherEvents(new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException,managerValidationException_exception,sessionId);
    }

    @Test
    public void gatherEvents_throws_ManagerSecurityException_Exception_ConnectToDSMClient() throws Exception {
        DsmEventClientException dsmEventClientException = null;

        ManagerSecurityException_Exception managerSecurityException_exception;
        managerSecurityException_exception = new ManagerSecurityException_Exception(exceptionTestMessage);
        setupUsernamePasswordWhen(username, password, managerSecurityException_exception);

        try {
            List<FirewallEvent> events = classUnderTest.gatherEvents(new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException,managerSecurityException_exception,null);
    }

    @Test
    public void gatherEvents_throws_ManagerAuthenticationException_Exception_ConnectToDSMClient() throws Exception {
        DsmEventClientException dsmEventClientException = null;

        ManagerAuthenticationException_Exception managerAuthenticationException_exception;
        managerAuthenticationException_exception = new ManagerAuthenticationException_Exception(exceptionTestMessage);

        setupUsernamePasswordWhen(username, password, managerAuthenticationException_exception);

        try {
            List<FirewallEvent> events = classUnderTest.gatherEvents(new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException,managerAuthenticationException_exception,null);
    }

    @Test
    public void gatherEvents_throws_ManagerLockoutException_Exception_ConnectToDSMClient() throws Exception {
        DsmEventClientException dsmEventClientException = null;

        ManagerLockoutException_Exception managerLockoutException_exception;
        managerLockoutException_exception = new ManagerLockoutException_Exception(exceptionTestMessage);

        setupUsernamePasswordWhen(username, password, managerLockoutException_exception);

        try {
            List<FirewallEvent> events = classUnderTest.gatherEvents(new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException,managerLockoutException_exception,null);
    }

    @Test
    public void gatherEvents_throws_ManagerCommunicationException_Exception_ConnectToDSMClient() throws Exception {
        DsmEventClientException dsmEventClientException = null;

        ManagerCommunicationException_Exception managerCommunicationException_exception;
        managerCommunicationException_exception = new ManagerCommunicationException_Exception(exceptionTestMessage);

        setupUsernamePasswordWhen(username, password, managerCommunicationException_exception);

        try {
            List<FirewallEvent> events = classUnderTest.gatherEvents(new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException,managerCommunicationException_exception,null);
    }

    @Test
    public void gatherEvents_throws_ManagerMaxSessionsException_Exception_ConnectToDSMClient() throws Exception {
        DsmEventClientException dsmEventClientException = null;

        ManagerMaxSessionsException_Exception managerMaxSessionsException_exception;
        managerMaxSessionsException_exception = new ManagerMaxSessionsException_Exception(exceptionTestMessage);

        setupUsernamePasswordWhen(username, password, managerMaxSessionsException_exception);

        try {
            List<FirewallEvent> events = classUnderTest.gatherEvents(new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException,managerMaxSessionsException_exception,null);
    }

    @Test
    public void gatherEvents_throws_ManagerException_Exception_ConnectToDSMClient() throws Exception {
        DsmEventClientException dsmEventClientException = null;

        ManagerException_Exception managerException_exception = new ManagerException_Exception(exceptionTestMessage);

        setupUsernamePasswordWhen(username, password, managerException_exception);

        try {
            List<FirewallEvent> events = classUnderTest.gatherEvents(new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException, managerException_exception, null);
    }

    private void verifyCorrectExceptionAndEndSession(DsmEventClientException dsmEventClientException, Throwable managerException_exception,String sessionId) {
        assertNotNull(dsmEventClientException);

        assertEquals(new DsmEventClientException(managerException_exception).toString(),
                dsmEventClientException.toString());
        verify(dsmLogInClient).endSession(sessionId);
    }

}