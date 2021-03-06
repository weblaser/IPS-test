package com.ctl.security.ips.dsm;

import com.ctl.security.ips.client.EventClient;
import com.ctl.security.ips.common.domain.Event.DpiEvent;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.dsm.domain.DpiEventTransportMarshaller;
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
import java.util.Random;

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
    private DpiEventTransportMarshaller dpiEventTransportMarshaller;

    @Mock
    private EventClient eventClient;

    @Mock
    private EventBean eventBean;

    private String username = "joe";
    private String password = "password";
    private String sessionId = "12345";
    private String bearerToken = "bearerToken";
    private String exceptionTestMessage = "This is a Test";
    private String SOME_TENANT = "Tenant";

    private List<String> setupUsernamePasswordWhen(List<String> tenants, String username, String password) throws Exception {
        ReflectionTestUtils.setField(classUnderTest, "username", username);
        ReflectionTestUtils.setField(classUnderTest, "password", password);

        int currentSessionId = 0;
        List<String> sessionIds = new ArrayList<>();

        when(dsmLogInClient.connectToDSMClient(eq(username), eq(password))).thenReturn(sessionId);
        for (String currentTenant : tenants) {
            currentSessionId++;
            String tenantSessionId = Integer.toString(currentSessionId);
            when(dsmLogInClient.connectTenantToDSMClient(eq(currentTenant), eq(sessionId)))
                    .thenReturn(tenantSessionId);
            sessionIds.add(tenantSessionId);
        }

        return sessionIds;
    }

    private void throwExceptionWhenConnectToDSM(String username, String password, Throwable throwable) throws Exception {
        ReflectionTestUtils.setField(classUnderTest, "username", username);
        ReflectionTestUtils.setField(classUnderTest, "password", password);

        when(dsmLogInClient.connectToDSMClient(eq(username), eq(password))).thenThrow(throwable);
    }

    private void throwExceptionWhenTenantConnectToDSM(List<String> tenants, String username, String password, Throwable throwable) throws Exception {
        ReflectionTestUtils.setField(classUnderTest, "username", username);
        ReflectionTestUtils.setField(classUnderTest, "password", password);

        when(dsmLogInClient.connectToDSMClient(eq(username), eq(password))).thenThrow(throwable);

        for (String currentTenant : tenants) {
            when(dsmLogInClient.connectTenantToDSMClient(eq(currentTenant), eq(username), eq(password)))
                    .thenThrow(throwable);
        }
    }

    @Test
    public void gatherEvents_gathersEventsFromDsm() throws Exception {
        List<String> tenants = getTenants(10);
        List<String> sessionIds = setupUsernamePasswordWhen(tenants, username, password);
        List<List<DPIEventTransport>> allDpiEventTransports = new ArrayList<>();


        for (String currentSessionId : sessionIds) {
            int amount = new Random().nextInt(10);
            List<DPIEventTransport> dpiEventTransports = getDPIEventTransports(amount, currentSessionId);
            allDpiEventTransports.add(dpiEventTransports);
        }


        for (int index = 0; index < tenants.size(); index++) {
            List<DpiEvent> currentEvents = classUnderTest.gatherEvents(tenants.get(index), new Date(), new Date());

            for (DPIEventTransport dpiEventTransport : allDpiEventTransports.get(index)) {
                verify(dpiEventTransportMarshaller).convert(dpiEventTransport);
            }

            assertNotNull(currentEvents);
            verify(dsmLogInClient).endSession(sessionIds.get(index));
        }
    }

    @Test
    public void gatherEvents_throws_ManagerException_Exception_GetDPIEvents() throws Exception {
        DsmEventClientException dsmEventClientException = null;

        ArrayList<String> tenants = new ArrayList<>();
        tenants.add(SOME_TENANT);
        String sessionId = setupUsernamePasswordWhen(tenants, username, password).get(0);

        ManagerException_Exception managerException_exception = new ManagerException_Exception(exceptionTestMessage);

        when(manager.dpiEventRetrieve(any(TimeFilterTransport.class),
                any(HostFilterTransport.class),
                any(IDFilterTransport.class),
                eq(sessionId))).thenThrow(managerException_exception);

        try {
            List<DpiEvent> events = classUnderTest.gatherEvents(SOME_TENANT, new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException, managerException_exception, sessionId, 1);
    }

    @Test
    public void gatherEvents_throws_ManagerAuthenticationException_Exception_dpiEventRetrieve() throws Exception {
        DsmEventClientException dsmEventClientException = null;

        ArrayList<String> tenants = new ArrayList<>();
        tenants.add(SOME_TENANT);
        String sessionId = setupUsernamePasswordWhen(tenants, username, password).get(0);

        ManagerAuthenticationException_Exception managerAuthenticationException_exception;
        managerAuthenticationException_exception = new ManagerAuthenticationException_Exception(exceptionTestMessage);

        when(manager.dpiEventRetrieve(any(TimeFilterTransport.class),
                any(HostFilterTransport.class),
                any(IDFilterTransport.class),
                anyString())).thenThrow(managerAuthenticationException_exception);


        try {
            List<DpiEvent> events = classUnderTest.gatherEvents(SOME_TENANT, new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException, managerAuthenticationException_exception, sessionId, 1);
    }

    @Test
    public void gatherEvents_throws_ManagerTimeoutException_Exception_dpiEventRetrieve() throws Exception {
        DsmEventClientException dsmEventClientException = null;

        ArrayList<String> tenants = new ArrayList<>();
        tenants.add(SOME_TENANT);
        String sessionId = setupUsernamePasswordWhen(tenants, username, password).get(0);

        ManagerTimeoutException_Exception managerTimeoutException_exception;
        managerTimeoutException_exception = new ManagerTimeoutException_Exception(exceptionTestMessage);

        when(manager.dpiEventRetrieve(any(TimeFilterTransport.class),
                any(HostFilterTransport.class),
                any(IDFilterTransport.class),
                anyString())).thenThrow(managerTimeoutException_exception);


        try {
            List<DpiEvent> events = classUnderTest.gatherEvents(SOME_TENANT, new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException, managerTimeoutException_exception, sessionId, 1);
    }

    @Test
    public void gatherEvents_throws_ManagerValidationException_Exception_dpiEventRetrieve() throws Exception {
        DsmEventClientException dsmEventClientException = null;

        ArrayList<String> tenants = new ArrayList<>();
        tenants.add(SOME_TENANT);
        String sessionId = setupUsernamePasswordWhen(tenants, username, password).get(0);

        ManagerValidationException_Exception managerValidationException_exception;
        managerValidationException_exception = new ManagerValidationException_Exception(exceptionTestMessage);

        when(manager.dpiEventRetrieve(any(TimeFilterTransport.class),
                any(HostFilterTransport.class),
                any(IDFilterTransport.class),
                anyString())).thenThrow(managerValidationException_exception);

        try {
            List<DpiEvent> events = classUnderTest.gatherEvents(SOME_TENANT, new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException, managerValidationException_exception, sessionId, 1);
    }

    @Test
    public void gatherEvents_throws_ManagerSecurityException_Exception_ConnectToDSMClient() throws Exception {
        DsmEventClientException dsmEventClientException = null;
        ArrayList<String> tenants = new ArrayList<>();
        tenants.add(SOME_TENANT);

        ManagerSecurityException_Exception managerSecurityException_exception;
        managerSecurityException_exception = new ManagerSecurityException_Exception(exceptionTestMessage);
        throwExceptionWhenConnectToDSM(username, password, managerSecurityException_exception);

        try {
            List<DpiEvent> events = classUnderTest.gatherEvents(SOME_TENANT, new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException, managerSecurityException_exception, null, 2);
    }

    @Test
    public void gatherEvents_throws_ManagerAuthenticationException_Exception_ConnectToDSMClient() throws Exception {
        DsmEventClientException dsmEventClientException = null;
        ArrayList<String> tenants = new ArrayList<>();
        tenants.add(SOME_TENANT);


        ManagerAuthenticationException_Exception managerAuthenticationException_exception;
        managerAuthenticationException_exception = new ManagerAuthenticationException_Exception(exceptionTestMessage);

        throwExceptionWhenConnectToDSM(username, password, managerAuthenticationException_exception);

        try {
            List<DpiEvent> events = classUnderTest.gatherEvents(SOME_TENANT, new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException, managerAuthenticationException_exception, null, 2);
    }

    @Test
    public void gatherEvents_throws_ManagerLockoutException_Exception_ConnectToDSMClient() throws Exception {
        DsmEventClientException dsmEventClientException = null;
        ArrayList<String> tenants = new ArrayList<>();
        tenants.add(SOME_TENANT);


        ManagerLockoutException_Exception managerLockoutException_exception;
        managerLockoutException_exception = new ManagerLockoutException_Exception(exceptionTestMessage);

        throwExceptionWhenConnectToDSM(username, password, managerLockoutException_exception);

        try {
            List<DpiEvent> events = classUnderTest.gatherEvents(SOME_TENANT, new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException, managerLockoutException_exception, null, 2);
    }

    @Test
    public void gatherEvents_throws_ManagerCommunicationException_Exception_ConnectToDSMClient() throws Exception {
        DsmEventClientException dsmEventClientException = null;
        ArrayList<String> tenants = new ArrayList<>();
        tenants.add(SOME_TENANT);


        ManagerCommunicationException_Exception managerCommunicationException_exception;
        managerCommunicationException_exception = new ManagerCommunicationException_Exception(exceptionTestMessage);

        throwExceptionWhenConnectToDSM(username, password, managerCommunicationException_exception);

        try {
            List<DpiEvent> events = classUnderTest.gatherEvents(SOME_TENANT, new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException, managerCommunicationException_exception, null, 2);
    }

    @Test
    public void gatherEvents_throws_ManagerMaxSessionsException_Exception_ConnectToDSMClient() throws Exception {
        DsmEventClientException dsmEventClientException = null;
        ArrayList<String> tenants = new ArrayList<>();
        tenants.add(SOME_TENANT);


        ManagerMaxSessionsException_Exception managerMaxSessionsException_exception;
        managerMaxSessionsException_exception = new ManagerMaxSessionsException_Exception(exceptionTestMessage);

        throwExceptionWhenConnectToDSM(username, password, managerMaxSessionsException_exception);

        try {
            List<DpiEvent> events = classUnderTest.gatherEvents(SOME_TENANT, new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException, managerMaxSessionsException_exception, null, 2);
    }

    @Test
    public void gatherEvents_throws_ManagerException_Exception_ConnectToDSMClient() throws Exception {
        DsmEventClientException dsmEventClientException = null;
        ArrayList<String> tenants = new ArrayList<>();
        tenants.add(SOME_TENANT);


        ManagerException_Exception managerException_exception = new ManagerException_Exception(exceptionTestMessage);

        throwExceptionWhenConnectToDSM(username, password, managerException_exception);

        try {
            List<DpiEvent> events = classUnderTest.gatherEvents(SOME_TENANT, new Date(), new Date());
        } catch (DsmEventClientException e) {
            dsmEventClientException = e;
        }

        verifyCorrectExceptionAndEndSession(dsmEventClientException, managerException_exception, null, 2);
    }

    private void verifyCorrectExceptionAndEndSession(DsmEventClientException dsmEventClientException, Throwable managerException_exception, String sessionId, Integer amount) {
        assertNotNull(dsmEventClientException);

        assertEquals(new DsmEventClientException(managerException_exception).toString(),
                dsmEventClientException.toString());
        verify(dsmLogInClient, times(amount)).endSession(sessionId);
    }

    private List<String> getTenants(Integer amount) {
        List<String> tenants = new ArrayList<>();
        for (Integer count = 0; count < amount; count++) {
            tenants.add("tenant" + count);
        }
        return tenants;
    }

    private List<DPIEventTransport> getDPIEventTransports(Integer amount, String sessionId) throws ManagerAuthenticationException_Exception, ManagerTimeoutException_Exception, ManagerValidationException_Exception, ManagerException_Exception {
        DPIEventListTransport dpiEventListTransport = mock(DPIEventListTransport.class);
        ArrayOfDPIEventTransport arrayOfDPIEventTransport = mock(ArrayOfDPIEventTransport.class);

        List<DPIEventTransport> dpiEventTransports = new ArrayList();

        when(dpiEventListTransport.getDPIEvents())
                .thenReturn(arrayOfDPIEventTransport);
        when(arrayOfDPIEventTransport.getItem())
                .thenReturn(dpiEventTransports);

        for (Integer count = 0; count < amount; count++) {
            DPIEventTransport dpiEventTransport = new DPIEventTransport();
            DpiEvent dpiEvent = new DpiEvent();

            when(dpiEventTransportMarshaller.convert(dpiEventTransport))
                    .thenReturn(dpiEvent);

            dpiEventTransports.add(dpiEventTransport);
        }

        when(manager.dpiEventRetrieve(any(TimeFilterTransport.class),
                any(HostFilterTransport.class),
                any(IDFilterTransport.class),
                eq(sessionId)))
                .thenReturn(dpiEventListTransport);

        return dpiEventTransports;
    }
}