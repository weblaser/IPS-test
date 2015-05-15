package com.ctl.security.ips.dsm;

import manager.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Sean Robb on 5/15/2015.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DsmAgentClientTest {

    private final String BAD_STATUS = "Not Managed (Offline)";
    private final String GOOD_STATUS = "Managed (Online)";
    private final String BAD_DPI_STATUS = "Intrusion Prevention: Off, Installed, 310 rules";
    private final String GOOD_DPI_STATUS = "Intrusion Prevention: On, Prevent, 310 rules";

    @InjectMocks
    private DsmAgentClient classUnderTest;

    @Mock
    private DsmLogInClient dsmLoginClient;

    @Mock
    private Manager manager;

    @Mock
    private HostTransport hostTransport;

    @Mock
    private HostStatusTransport hostStatusTransport;

    @Mock
    private ArrayOfProtectionStatusTransport arrayOfProtectionStatusTransport;

    private List<ProtectionStatusTransport> protectionStatusTransportsList;

    private String username = "joe";
    private String password = "password";
    private String sessionId = "12345";
    private String tenantSessionId = "54321";
    private Integer hostId = 1;
    private final String hostName = "someHostName";
    private final String accountAlias = "Valid_AA";

    @Before
    public void init() {
        ReflectionTestUtils.setField(classUnderTest, "username", username);
        ReflectionTestUtils.setField(classUnderTest, "password", password);
        protectionStatusTransportsList = new ArrayList<>();
    }

    @Test
    public void verify_VerifyDSMLoggedIn() throws Exception {
        protectionStatusTransportsList.clear();
        protectionStatusTransportsList.add(getProtectionStatusTransport(BAD_DPI_STATUS, BAD_STATUS));
        setUpMocksForHostFound();

        Boolean installed = classUnderTest.verifyAgentInstall(accountAlias, hostName);

        verify(dsmLoginClient).connectToDSMClient(username, password);
        verify(dsmLoginClient).connectTenantToDSMClient(accountAlias, sessionId);
        verify(dsmLoginClient).endSession(sessionId);
        verify(dsmLoginClient).endSession(tenantSessionId);
        assertNotNull(installed);
    }

    @Test
    public void verify_VerifyHostNameRetrieved() throws Exception {
        protectionStatusTransportsList.clear();
        protectionStatusTransportsList.add(getProtectionStatusTransport(BAD_DPI_STATUS, BAD_STATUS));
        setUpMocksForHostFound();

        Boolean installed = classUnderTest.verifyAgentInstall(accountAlias, hostName);

        assertNotNull(installed);
        verify(manager).hostRetrieveByName(hostName, tenantSessionId);
    }

    @Test
    public void verify_VerifyHostNameStatusRetrieved() throws Exception {
        protectionStatusTransportsList.clear();
        protectionStatusTransportsList.add(getProtectionStatusTransport(BAD_DPI_STATUS, BAD_STATUS));
        setUpMocksForHostFound();

        Boolean installed = classUnderTest.verifyAgentInstall(accountAlias, hostName);

        assertNotNull(installed);
        verify(manager).hostGetStatus(hostId, tenantSessionId);
    }

    @Test
    public void verify_VerifyProtectionFalseStatusRetrieved() throws Exception {
        setUpMocksForHostFound();

        Boolean installed = classUnderTest.verifyAgentInstall(accountAlias, hostName);

        assertNotNull(installed);
        assertFalse(installed);
    }

    @Test
    public void verify_VerifyProtectionFalseStatusRetrievedFromDpiUninstalled() throws Exception {
        protectionStatusTransportsList.clear();
        protectionStatusTransportsList.add(getProtectionStatusTransport(GOOD_DPI_STATUS, BAD_STATUS));
        setUpMocksForHostFound();

        Boolean installed = classUnderTest.verifyAgentInstall(accountAlias, hostName);

        assertNotNull(installed);
        assertFalse(installed);
    }

    @Test
    public void verify_VerifyDPIStatusRetrievedFromDpiUninstalled() throws Exception {
        protectionStatusTransportsList.clear();
        protectionStatusTransportsList.add(getProtectionStatusTransport(BAD_DPI_STATUS, GOOD_STATUS));
        setUpMocksForHostFound();

        Boolean installed = classUnderTest.verifyAgentInstall(accountAlias, hostName);

        assertNotNull(installed);
        assertFalse(installed);
    }

    @Test
    public void verify_VerifyTrueStatusRetrievedFromDpiUninstalled() throws Exception {
        protectionStatusTransportsList.clear();
        protectionStatusTransportsList.add(getProtectionStatusTransport(GOOD_DPI_STATUS, GOOD_STATUS));
        setUpMocksForHostFound();

        Boolean installed = classUnderTest.verifyAgentInstall(accountAlias, hostName);

        assertNotNull(installed);
        assertTrue(installed);
    }

    @Test
    public void verify_FalseIfHostnameIsNotFound() throws Exception {
        setUpMocksForHostNotFound();

        Boolean installed = classUnderTest.verifyAgentInstall(accountAlias, hostName);

        assertNotNull(installed);
        assertFalse(installed);
    }

    private ProtectionStatusTransport getProtectionStatusTransport(String dpi_status, String status) {
        ProtectionStatusTransport protectionStatusTransport = new ProtectionStatusTransport();
        protectionStatusTransport.setProtectionType(EnumProtectionType.AGENT);
        protectionStatusTransport.setDpiStatus(dpi_status);
        protectionStatusTransport.setStatus(status);
        return protectionStatusTransport;
    }

    private void setUpMocksForHostFound() throws Exception {
        when(dsmLoginClient.connectToDSMClient(username, password)).thenReturn(sessionId);
        when(dsmLoginClient.connectTenantToDSMClient(accountAlias, sessionId)).thenReturn(tenantSessionId);
        when(manager.hostRetrieveByName(hostName, tenantSessionId)).thenReturn(hostTransport);
        when(hostTransport.getID()).thenReturn(hostId);
        when(manager.hostGetStatus(hostId, tenantSessionId)).thenReturn(hostStatusTransport);
        when(hostStatusTransport.getProtectionStatusTransports()).thenReturn(arrayOfProtectionStatusTransport);
        when(arrayOfProtectionStatusTransport.getItem()).thenReturn(protectionStatusTransportsList);
    }

    private void setUpMocksForHostNotFound() throws Exception {
        when(dsmLoginClient.connectToDSMClient(username, password)).thenReturn(sessionId);
        when(dsmLoginClient.connectTenantToDSMClient(accountAlias, sessionId)).thenReturn(tenantSessionId);
        when(manager.hostRetrieveByName(hostName, tenantSessionId)).thenReturn(null);
    }
}