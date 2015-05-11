package com.ctl.security.ips.dsm;

import com.ctl.security.clc.client.core.bean.ServerClient;
import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.domain.SecurityProfileTransportMarshaller;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import manager.*;
import org.apache.commons.lang.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by Chad.Middleton on 1/15/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class DsmPolicyClientTest {

    @InjectMocks
    private DsmPolicyClient classUnderTest;

    @Mock
    private Manager manager;

    @Mock
    private DsmLogInClient dsmLogInClient;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ServerClient serverClient;

    @Mock
    private SecurityProfileTransportMarshaller securityProfileTransportMarshaller;

    private String LINUX = "rhel_64bit";
    private String WINDOWS_2008 = "windows2008_64Bit";
    private String WINDOWS_2012 = "windows2012DataCenter_64Bit";

    private String CLC_LINUX = "CLC Linux Server";
    private String CLC_WINDOWS_2008 = "CLC Windows Server 2008";
    private String CLC_WINDOWS_2012 = "CLC Windows Server 2012";

    private String username = "joe";
    private String password = "password";
    private String sessionId = "12345";
    private PolicyBean policyBean;
    private Policy expectedPolicy;
    private Policy parentPolicy;

    @Before
    public void setup() throws Exception {
        Map<String, String> osTypeMap = new HashMap<>();
        osTypeMap.put(LINUX, CLC_LINUX);
        osTypeMap.put(WINDOWS_2008, CLC_WINDOWS_2008);
        osTypeMap.put(WINDOWS_2012, CLC_WINDOWS_2012);
        ReflectionTestUtils.setField(classUnderTest, "osTypeMap", osTypeMap);

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
        expectedPolicy.setParentPolicyId(parentPolicy.getVendorPolicyId());

        SecurityProfileTransport securityProfileTransportToBeCreated = new SecurityProfileTransport();
        SecurityProfileTransport expectedSecurityProfileTransport = new SecurityProfileTransport();
        SecurityProfileTransport parentProfileTransport = new SecurityProfileTransport();
        SecurityProfileTransport createdPolicyProfileTransport = new SecurityProfileTransport();
        createdPolicyProfileTransport.setParentSecurityProfileID(NumberUtils.createInteger(parentPolicy.getVendorPolicyId()));

        if (os.equals(WINDOWS_2008)) {
            when(manager.securityProfileRetrieveByName(CLC_WINDOWS_2008, sessionId)).thenReturn(parentProfileTransport);
        } else if (os.equals(WINDOWS_2012)) {
            when(manager.securityProfileRetrieveByName(CLC_WINDOWS_2012, sessionId)).thenReturn(parentProfileTransport);
        } else {
            when(manager.securityProfileRetrieveByName(CLC_LINUX, sessionId)).thenReturn(parentProfileTransport);
        }

        when(manager.securityProfileSave(securityProfileTransportToBeCreated, sessionId)).thenReturn(expectedSecurityProfileTransport, createdPolicyProfileTransport);
        when(securityProfileTransportMarshaller.convert(policyToBeCreated)).thenReturn(securityProfileTransportToBeCreated);
        when(securityProfileTransportMarshaller.convert(expectedSecurityProfileTransport)).thenReturn(expectedPolicy);
        when(securityProfileTransportMarshaller.convert(parentProfileTransport)).thenReturn(parentPolicy);
        when(serverClient.getServerDetails(anyString(), anyString(), anyString()).getOs()).thenReturn(os);
    }

    private void setupUsernamePasswordWhen(String username, String password, String sessionId) throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthenticationException_Exception {
        ReflectionTestUtils.setField(classUnderTest, "username", username);
        ReflectionTestUtils.setField(classUnderTest, "password", password);
        when(dsmLogInClient.connectToDSMClient(eq(username), eq(password))).thenReturn(sessionId);
    }


    @Test
    public void createCtlSecurityProfile_createsCtlSecurityProfile() throws Exception {
        setupMocks(WINDOWS_2008);

        //act
        PolicyBean actualPolicyBean = classUnderTest.createPolicyWithParentPolicy(policyBean);

        //assert
        assertEquals(expectedPolicy, actualPolicyBean.getPolicy());
        assertEquals(parentPolicy.getVendorPolicyId(), actualPolicyBean.getPolicy().getParentPolicyId());
        verify(dsmLogInClient, times(2)).endSession(sessionId);
    }

    @Test(expected = DsmClientException.class)
    public void createCtlSecurityProfile_createsCtlSecurityProfileOsTypeNull() throws Exception {
        setupMocks("hahahaha this OS will not be found");

        //act
        PolicyBean actualPolicyBean = classUnderTest.createPolicyWithParentPolicy(policyBean);
        fail("DsmClientException should have been thrown");
    }

    @Test(expected = DsmClientException.class)
    public void createPolicyOnDSMClientTestFail() throws Exception {
        //arrange
        setupMocks("rhel_64bit");

        when(manager.securityProfileSave(any(SecurityProfileTransport.class), eq(sessionId))).thenThrow(ManagerLockoutException_Exception.class);

        //act
        classUnderTest.createPolicyWithParentPolicy(policyBean);
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
