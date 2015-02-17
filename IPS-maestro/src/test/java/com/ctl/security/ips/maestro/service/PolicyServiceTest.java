package com.ctl.security.ips.maestro.service;

import com.ctl.security.data.client.service.CmdbService;
import com.ctl.security.data.common.domain.mongo.Product;
import com.ctl.security.data.common.domain.mongo.ProductStatus;
import com.ctl.security.data.common.domain.mongo.ProductType;
import com.ctl.security.data.common.domain.mongo.bean.InstallationBean;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.common.exception.NotAuthorizedException;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import manager.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PolicyServiceTest {

    private static final String VALID_ACCOUNT = "TCCD";
    private static final String TEST_ID = "test-vendorPolicyId";
    private static final String INVALID_ACCOUNT = "TCCX";
    private static final String TEST_ID_2 = "test-vendorPolicyId-2";

    @InjectMocks
    private PolicyService classUnderTest;

    @Mock
    private DsmPolicyClient dsmPolicyClient;

    @Mock
    private CmdbService cmdbService;

    @Test
    public void createPolicy_createsPolicy() throws ManagerLockoutException_Exception, ManagerAuthenticationException_Exception, ManagerAuthorizationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerSecurityException_Exception, ManagerTimeoutException_Exception, DsmPolicyClientException {
        Policy policyToBeCreated = new Policy();
        Policy expectedNewlyCreatedPolicy = new Policy();
        when(dsmPolicyClient.createCtlSecurityProfile(policyToBeCreated)).thenReturn(expectedNewlyCreatedPolicy);
//        Policy expectedNewlyPersistedPolicy = new Policy();
//        when(policyDao.saveCtlSecurityProfile(expectedNewlyCreatedPolicy)).thenReturn(expectedNewlyPersistedPolicy);
        String username = null;
        String accountId = VALID_ACCOUNT;
        String serverDomainName = null;
        Product product = new Product().
                setName(PolicyService.TREND_MICRO_IPS).
                setStatus(ProductStatus.ACTIVE).
                setType(ProductType.IPS);
        InstallationBean installationBean = new InstallationBean(username, accountId, serverDomainName, product);

        PolicyBean policyBean = new PolicyBean(accountId, policyToBeCreated);
        Policy actualNewlyPersistedPolicy = classUnderTest.createPolicyForAccount(policyBean);


        verify(dsmPolicyClient).createCtlSecurityProfile(policyToBeCreated);
        assertNotNull(actualNewlyPersistedPolicy);
        assertEquals(expectedNewlyCreatedPolicy, actualNewlyPersistedPolicy);
        verify(cmdbService).installProduct(eq(installationBean));
    }


    @Test(expected = NotAuthorizedException.class)
    public void testCreatePolicyForAccountNotAuthorizedException() throws DsmPolicyClientException {
        //act
        String username = null;
        String accountId = INVALID_ACCOUNT;
        String serverDomainName = null;
        Product product = null;
        InstallationBean installationBean = new InstallationBean(username, accountId, serverDomainName, product);
        Policy policy = new Policy();

        PolicyBean policyBean = new PolicyBean(accountId, policy);
        classUnderTest.createPolicyForAccount(policyBean);
    }

    @Test
    public void testGetPoliciesForAccount() {
        //act
        List<com.ctl.security.ips.common.domain.Policy> policies = classUnderTest.getPoliciesForAccount(VALID_ACCOUNT);

        //assert
        com.ctl.security.ips.common.domain.Policy expected = buildPolicy();
        for (com.ctl.security.ips.common.domain.Policy actual : policies) {
            assertEquals(expected, actual);
        }
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testGetPoliciesForAccountPolicyNotFoundException() {
        //act
        List<com.ctl.security.ips.common.domain.Policy> policies = classUnderTest.getPoliciesForAccount(INVALID_ACCOUNT);
    }


    @Test
    public void testGetPolicyForAccount() {
        //act
        com.ctl.security.ips.common.domain.Policy actual = classUnderTest.getPolicyForAccount(VALID_ACCOUNT, TEST_ID);

        //assert
        com.ctl.security.ips.common.domain.Policy expected = buildPolicy();
        assertEquals(expected, actual);
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testGetPolicyForAccountPolicyNotFoundException() {
        //act
        classUnderTest.getPolicyForAccount(INVALID_ACCOUNT, TEST_ID);
    }


    @Test
    public void testUpdatePolicyForAccount() {
        //act
        classUnderTest.updatePolicyForAccount(VALID_ACCOUNT, TEST_ID, new com.ctl.security.ips.common.domain.Policy());
    }

    @Test(expected = NotAuthorizedException.class)
    public void testUpdatePolicyForAccountNotAuthorizedException() {
        //act
        classUnderTest.updatePolicyForAccount(INVALID_ACCOUNT, TEST_ID, new com.ctl.security.ips.common.domain.Policy());
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testUpdatePolicyForAccountPolicyNotFoundException() {
        //act
        classUnderTest.updatePolicyForAccount(VALID_ACCOUNT, TEST_ID_2, new com.ctl.security.ips.common.domain.Policy());
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

    private com.ctl.security.ips.common.domain.Policy buildPolicy() {
        return new com.ctl.security.ips.common.domain.Policy().setVendorPolicyId(TEST_ID).setStatus(PolicyStatus.ACTIVE);
    }
}
