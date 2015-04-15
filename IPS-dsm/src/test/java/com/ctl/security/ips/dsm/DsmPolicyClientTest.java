package com.ctl.security.ips.dsm;

import com.ctl.security.clc.client.core.bean.ServerClient;
import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.domain.SecurityProfileTransportMarshaller;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import com.ctl.security.ips.dsm.util.Os;
import manager.*;
import org.apache.commons.lang.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by Chad.Middleton on 1/15/2015.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DsmPolicyClientTest {

    @InjectMocks
    private DsmPolicyClient classUnderTest;

    @Mock
    private Manager manager;

    @Mock
    private DsmLogInClient dsmLogInClient;

    @Mock
    private ServerClient serverClient;

    @Mock
    private SecurityProfileTransportMarshaller securityProfileTransportMarshaller;

    private String LINUX = "rhel_64bit";
    private String WINDOWS = "Windows_2008";

    private String username = "joe";
    private String password = "password";
    private String sessionId = "12345";
    private PolicyBean policyBean;
    private Policy expectedPolicy;
    private Policy parentPolicy;

    @Before
    public void setup() throws Exception {
        setupUsernamePasswordWhen(username, password, sessionId);
        expectedPolicy = null;
        policyBean = null;
        parentPolicy = null;
    }

    private void setupMocks(String os) throws Exception {
        Policy policyToBeCreated = new Policy();
        policyBean = new PolicyBean("mockAlias", policyToBeCreated, "mockToken");

        parentPolicy = new Policy();
        parentPolicy.setVendorPolicyId("123938274");

        expectedPolicy = new Policy();

        SecurityProfileTransport securityProfileTransportToBeCreated = new SecurityProfileTransport();
        SecurityProfileTransport expectedSecurityProfileTransport = new SecurityProfileTransport();
        SecurityProfileTransport parentProfileTransport = new SecurityProfileTransport();
        SecurityProfileTransport createdPolicyProfileTransport = new SecurityProfileTransport();
        createdPolicyProfileTransport.setParentSecurityProfileID(NumberUtils.createInteger(parentPolicy.getVendorPolicyId()));

        if(os.equals(WINDOWS)) {
            when(manager.securityProfileRetrieveByName(Os.CLC_WINDOWS.getVaule(), sessionId)).thenReturn(parentProfileTransport);
        } else {
            when(manager.securityProfileRetrieveByName(Os.CLC_LINUX.getVaule(), sessionId)).thenReturn(parentProfileTransport);
        }
        when(manager.securityProfileSave(securityProfileTransportToBeCreated, sessionId)).thenReturn(expectedSecurityProfileTransport, createdPolicyProfileTransport);
        when(securityProfileTransportMarshaller.convert(policyToBeCreated)).thenReturn(securityProfileTransportToBeCreated);
        when(securityProfileTransportMarshaller.convert(expectedSecurityProfileTransport)).thenReturn(expectedPolicy);
        when(securityProfileTransportMarshaller.convert(parentProfileTransport)).thenReturn(parentPolicy);
        when(serverClient.getOS(anyString(), anyString(), anyString())).thenReturn(os);
    }

    @Test
    public void createCtlSecurityProfile_createsCtlSecurityProfileWindows() throws Exception {
        setupMocks(WINDOWS);

        //act
        Policy actualPolicy = classUnderTest.createCtlSecurityProfile(policyBean);

        //assert
        assertEquals(expectedPolicy, actualPolicy);
        assertEquals(parentPolicy.getVendorPolicyId(), actualPolicy.getParentPolicyId());
        verify(dsmLogInClient, times(2)).endSession(sessionId);
    }

    @Test
    public void createCtlSecurityProfile_createsCtlSecurityProfileLinux() throws Exception {
        setupMocks(LINUX);

        //act
        Policy actualPolicy = classUnderTest.createCtlSecurityProfile(policyBean);

        //assert
        assertEquals(expectedPolicy, actualPolicy);
        assertEquals(parentPolicy.getVendorPolicyId(), actualPolicy.getParentPolicyId());
        verify(dsmLogInClient, times(2)).endSession(sessionId);
    }

    private void setupUsernamePasswordWhen(String username, String password, String sessionId) throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthenticationException_Exception {
        ReflectionTestUtils.setField(classUnderTest, "username", username);
        ReflectionTestUtils.setField(classUnderTest, "password", password);
        when(dsmLogInClient.connectToDSMClient(eq(username), eq(password))).thenReturn(sessionId);
    }

    @Test (expected = DsmClientException.class)
    public void createPolicyOnDSMClientTestFail() throws Exception {
        //arrange
        setupMocks("rhel_64bit");

        when(manager.securityProfileSave(any(SecurityProfileTransport.class), eq(sessionId))).thenThrow(ManagerLockoutException_Exception.class);

        //act
        classUnderTest.createCtlSecurityProfile(policyBean);
    }


    @Test
    public void retrieveSecurityProfileById_retrievesSecurityProfileById() throws DsmClientException, ManagerAuthenticationException_Exception, ManagerTimeoutException_Exception, ManagerException_Exception {
        int id = 0;
        SecurityProfileTransport expectedSecurityProfileTransport = new SecurityProfileTransport();
        when(manager.securityProfileRetrieve(id, sessionId)).thenReturn(expectedSecurityProfileTransport);
        Policy expectedPolicy = new Policy();
        when(securityProfileTransportMarshaller.convert(expectedSecurityProfileTransport)).thenReturn(expectedPolicy);

        Policy actualPolicy = classUnderTest.retrieveSecurityProfileById(id);

        assertNotNull(actualPolicy);
        assertEquals(expectedPolicy.getVendorPolicyId(), actualPolicy.getVendorPolicyId());
        verify(dsmLogInClient).endSession(sessionId);
    }

    @Test
    public void retrieveSecurityProfileByName_retrievesSecurityProfileByName() throws DsmClientException, ManagerAuthenticationException_Exception, ManagerTimeoutException_Exception, ManagerException_Exception {
        String name = "name";
        SecurityProfileTransport expectedSecurityProfileTransport = new SecurityProfileTransport();
        when(manager.securityProfileRetrieveByName(name, sessionId)).thenReturn(expectedSecurityProfileTransport);
        Policy expectedPolicy = new Policy();
        when(securityProfileTransportMarshaller.convert(expectedSecurityProfileTransport)).thenReturn(expectedPolicy);

        Policy actualPolicy = classUnderTest.retrieveSecurityProfileByName(name);

        assertNotNull(actualPolicy);
        assertEquals(expectedPolicy.getVendorPolicyId(), actualPolicy.getVendorPolicyId());
        verify(dsmLogInClient).endSession(sessionId);
    }

    @Test
    public void securityProfileDelete_deletesSecurityProfile() throws DsmClientException, ManagerException_Exception, ManagerTimeoutException_Exception, ManagerAuthenticationException_Exception, ManagerAuthorizationException_Exception {
        int id = 0;
        List<Integer> ids = Arrays.asList(id);
        classUnderTest.securityProfileDelete(ids);

        verify(dsmLogInClient).endSession(sessionId);
        verify(manager).securityProfileDelete(ids, sessionId);

    }
}
