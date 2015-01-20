package com.ctl.security.dsm;

import com.ctl.security.dsm.DsmPolicyClient;
import com.ctl.security.dsm.DsmLogInClient;
import com.ctl.security.dsm.domain.CtlSecurityProfile;
import com.ctl.security.dsm.domain.SecurityProfileTransportMarshaller;
import manager.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    private SecurityProfileTransportMarshaller securityProfileTransportMarshaller;

    private String username = "joe";
    private String password = "password";
    private String sessionId = "12345";

    @Before
    public void setup() throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception {
        setupUsernamePasswordWhen(username, password, sessionId);
    }

    @Test
    public void createPolicyOnDSMClientTestSuccess() throws Exception {
        //arrange
        SecurityProfileTransport expectedSecurityProfileTransport = new SecurityProfileTransport();

        when(manager.securityProfileSave(any(SecurityProfileTransport.class), eq("12345"))).thenReturn(expectedSecurityProfileTransport);
        //act
        SecurityProfileTransport actualSecurityProfileTransport = classUnderTest.createPolicyOnDSMClient(new SecurityProfileTransport());
        //assert
        assertEquals(expectedSecurityProfileTransport, actualSecurityProfileTransport);
    }

    private void setupUsernamePasswordWhen(String username, String password, String sessionId) throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthenticationException_Exception {
        ReflectionTestUtils.setField(classUnderTest, "username", username);
        ReflectionTestUtils.setField(classUnderTest, "password", password);
        when(dsmLogInClient.connectToDSMClient(eq(username), eq(password))).thenReturn(sessionId);
    }

    @Test (expected = ManagerLockoutException_Exception.class)
    public void createPolicyOnDSMClientTestFail() throws ManagerLockoutException_Exception, ManagerAuthenticationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerSecurityException_Exception, ManagerValidationException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerAuthorizationException_Exception, ManagerTimeoutException_Exception {
        //arrange
        when(manager.securityProfileSave(any(SecurityProfileTransport.class), eq(sessionId))).thenThrow(ManagerLockoutException_Exception.class);

        //act
        classUnderTest.createPolicyOnDSMClient(new SecurityProfileTransport());
    }


    @Test
    public void createCtlSecurityProfile_createsCtlSecurityProfile() throws ManagerValidationException_Exception, ManagerAuthenticationException_Exception, ManagerTimeoutException_Exception, ManagerAuthorizationException_Exception, ManagerIntegrityConstraintException_Exception, ManagerException_Exception, ManagerMaxSessionsException_Exception, ManagerLockoutException_Exception, ManagerSecurityException_Exception, ManagerCommunicationException_Exception {
        CtlSecurityProfile ctlSecurityProfileToBeCreated = new CtlSecurityProfile();

        CtlSecurityProfile newlyCreatedCtlSecurityProfile = classUnderTest.createCtlSecurityProfile(ctlSecurityProfileToBeCreated);

        assertNotNull(newlyCreatedCtlSecurityProfile);

        verify(securityProfileTransportMarshaller).convert(ctlSecurityProfileToBeCreated);

        verify(dsmLogInClient).connectToDSMClient(username, password);

        SecurityProfileTransport securityProfileTransport = null;
        verify(manager).securityProfileSave(securityProfileTransport, sessionId);
    }

}
