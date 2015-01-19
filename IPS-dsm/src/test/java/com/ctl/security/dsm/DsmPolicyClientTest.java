package com.ctl.security.dsm;

import com.ctl.security.dsm.DsmPolicyClient;
import com.ctl.security.dsm.DsmLogInClient;
import manager.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by Chad.Middleton on 1/15/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class DsmPolicyClientTest {

    @Mock
    private Manager manager;

    @Mock
    private DsmLogInClient dsmLogInClient;

    @InjectMocks
    private DsmPolicyClient classUnderTest;

    @Test
    public void createPolicyOnDSMClientTestSuccess() throws Exception {
        //arrange
        SecurityProfileTransport transport = new SecurityProfileTransport();
        when(dsmLogInClient.connectToDSMClient(eq("joe"), eq("password"))).thenReturn("12345");
        when(manager.securityProfileSave(any(SecurityProfileTransport.class), eq("12345"))).thenReturn(transport);
        //act
        SecurityProfileTransport actual = classUnderTest.createPolicyOnDSMClient("joe", "password", new SecurityProfileTransport());
        //assert
        assertEquals(transport, actual);
    }

    @Test (expected = ManagerLockoutException_Exception.class)
    public void createPolicyOnDSMClientTestFail() throws ManagerLockoutException_Exception, ManagerAuthenticationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerSecurityException_Exception, ManagerValidationException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerAuthorizationException_Exception, ManagerTimeoutException_Exception {
        //arrange
        when(dsmLogInClient.connectToDSMClient(eq("joe"), eq("password"))).thenReturn("12345");
        when(manager.securityProfileSave(any(SecurityProfileTransport.class), eq("12345"))).thenThrow(ManagerLockoutException_Exception.class);
        //act
        classUnderTest.createPolicyOnDSMClient("joe", "password", new SecurityProfileTransport());
    }

}
