package com.ctl.security.ips.service;

import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.domain.CtlSecurityProfile;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.common.exception.NotAuthorizedException;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import com.ctl.security.ips.dao.PolicyDao;
import manager.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PolicyServiceTest {

    private static final String VALID_ACCOUNT = "TCCD";
    private static final String TEST_ID = "test-id";
    private static final String INVALID_ACCOUNT = "TCCX";
    private static final String TEST_ID_2 = "test-id-2";

    @InjectMocks
    private PolicyService classUnderTest;

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


    @Test
    public void testGetPoliciesForAccount() {
        //act
        List<Policy> policies = classUnderTest.getPoliciesForAccount(VALID_ACCOUNT);

        //assert
        Policy expected = buildPolicy();
        for (Policy actual : policies) {
            assertEquals(expected, actual);
        }
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testGetPoliciesForAccountPolicyNotFoundException() {
        //act
        List<Policy> policies = classUnderTest.getPoliciesForAccount(INVALID_ACCOUNT);
    }


    @Test
    public void testGetPolicyForAccount() {
        //act
        Policy actual = classUnderTest.getPolicyForAccount(VALID_ACCOUNT, TEST_ID);

        //assert
        Policy expected = buildPolicy();
        assertEquals(expected, actual);
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testGetPolicyForAccountPolicyNotFoundException() {
        //act
        classUnderTest.getPolicyForAccount(INVALID_ACCOUNT, TEST_ID);
    }

    @Test
    public void testCreatePolicyForAccount() {

        //act
        String actual = classUnderTest.createPolicyForAccount(VALID_ACCOUNT, new Policy());

        //assert
        String expectedRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
        assertNotNull(actual);
        assertTrue(actual.matches(expectedRegex));
    }

    @Test(expected = NotAuthorizedException.class)
    public void testCreatePolicyForAccountNotAuthorizedException() {
        //act
        classUnderTest.createPolicyForAccount(INVALID_ACCOUNT, new Policy());
    }

    @Test
    public void testUpdatePolicyForAccount() {
        //act
        classUnderTest.updatePolicyForAccount(VALID_ACCOUNT, TEST_ID, new Policy());
    }

    @Test(expected = NotAuthorizedException.class)
    public void testUpdatePolicyForAccountNotAuthorizedException() {
        //act
        classUnderTest.updatePolicyForAccount(INVALID_ACCOUNT, TEST_ID, new Policy());
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testUpdatePolicyForAccountPolicyNotFoundException() {
        //act
        classUnderTest.updatePolicyForAccount(VALID_ACCOUNT, TEST_ID_2, new Policy());
    }

    @Test
    public void testDeletePolicyForAccount() {
        //act
        classUnderTest.deletePolicyForAccount(VALID_ACCOUNT, TEST_ID);
    }

    @Test(expected = NotAuthorizedException.class)
    public void testDeletePolicyForAccountNotAuthorizedException() {
        //act
        classUnderTest.deletePolicyForAccount(INVALID_ACCOUNT, TEST_ID);
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testDeletePolicyForAccountPolicyNotFoundException() {
        //act
        classUnderTest.deletePolicyForAccount(VALID_ACCOUNT, TEST_ID_2);
    }

    private Policy buildPolicy() {
        return new Policy().setId(TEST_ID).setStatus(PolicyStatus.ACTIVE);
    }
}
