package com.ctl.security.ips.service;

import com.ctl.security.dsm.DsmPolicyClient;
import com.ctl.security.dsm.domain.CtlSecurityProfile;
import com.ctl.security.dsm.exception.DsmPolicyClientException;
import com.ctl.security.ips.dao.PolicyDao;
import manager.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by kevin.wilde on 1/19/2015.
 */

@RunWith(MockitoJUnitRunner.class)
public class PolicyServiceTmpTest {

    @InjectMocks
    private PolicyServiceTmp classUnderTest;

    @Mock
    private DsmPolicyClient dsmPolicyClient;

    @Mock
    private PolicyDao policyDao;

    @Test
    public void createPolicy_createsPolicy() throws ManagerLockoutException_Exception, ManagerAuthenticationException_Exception, ManagerAuthorizationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerSecurityException_Exception, ManagerTimeoutException_Exception, DsmPolicyClientException {
        CtlSecurityProfile ctlSecurityProfileToBeCreated = new CtlSecurityProfile();
        CtlSecurityProfile expectedNewlyCreatedCtlSecurityProfile = new CtlSecurityProfile();
        when(dsmPolicyClient.createCtlSecurityProfile(ctlSecurityProfileToBeCreated)).thenReturn(expectedNewlyCreatedCtlSecurityProfile);
        CtlSecurityProfile expectedNewlyPersistedCtlSecurityProfile = new CtlSecurityProfile();
        when(policyDao.saveCtlSecurityProfile(expectedNewlyCreatedCtlSecurityProfile)).thenReturn(expectedNewlyPersistedCtlSecurityProfile);


        CtlSecurityProfile actualNewlyPersistedCtlSecurityProfile = classUnderTest.createPolicy(ctlSecurityProfileToBeCreated);


        verify(dsmPolicyClient).createCtlSecurityProfile(ctlSecurityProfileToBeCreated);
        verify(policyDao).saveCtlSecurityProfile(actualNewlyPersistedCtlSecurityProfile);
        assertNotNull(actualNewlyPersistedCtlSecurityProfile);
        assertEquals(expectedNewlyCreatedCtlSecurityProfile, actualNewlyPersistedCtlSecurityProfile);
    }

}
