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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PolicyServiceTest {

    private static final String VALID_ACCOUNT = "TCCD";
    private static final String TEST_ID = "12345";
    private static final String INVALID_ACCOUNT = "TCCX";
    private static final String TEST_ID_2 = "4567";
    private static final String USERNAME = "username";
    private static final String SERVER_DOMAIN_NAME = "testServer";

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
        String username = null;
        String accountId = VALID_ACCOUNT;
        String serverDomainName = null;
        Product product = buildProduct();

        PolicyBean policyBean = new PolicyBean(accountId, policyToBeCreated);
        Policy actualNewlyPersistedPolicy = classUnderTest.createPolicyForAccount(policyBean);


        verify(dsmPolicyClient).createCtlSecurityProfile(policyToBeCreated);
        assertNotNull(actualNewlyPersistedPolicy);
        assertEquals(expectedNewlyCreatedPolicy, actualNewlyPersistedPolicy);
        verify(cmdbService).installProduct(new InstallationBean(username, accountId, serverDomainName, product));
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

    @Test
    public void testGetPolicyForAccount() {
        //act
        Policy actual = classUnderTest.getPolicyForAccount(VALID_ACCOUNT, TEST_ID);

        //assert
        Policy expected = buildPolicy();
        assertEquals(expected, actual);
    }

    @Test
    public void testUpdatePolicyForAccount() {
        //act
        classUnderTest.updatePolicyForAccount(VALID_ACCOUNT, TEST_ID, new Policy());
    }

    @Test
    public void testDeletePolicyForAccount() throws DsmPolicyClientException {
        //arrange

        //act
        PolicyBean policyBean = new PolicyBean(VALID_ACCOUNT, buildPolicy().setUsername(USERNAME).setServerDomainName(SERVER_DOMAIN_NAME));
        classUnderTest.deletePolicyForAccount(policyBean);

        //assert
        verify(dsmPolicyClient).securityProfileDelete(any(List.class));
        verify(cmdbService).uninstallProduct(new InstallationBean(USERNAME, VALID_ACCOUNT, SERVER_DOMAIN_NAME, buildProduct()));
    }

    private Policy buildPolicy() {
        return new Policy().setVendorPolicyId(TEST_ID).setStatus(PolicyStatus.ACTIVE);
    }

    private Product buildProduct() {
        return new Product().
                setName(PolicyService.TREND_MICRO_IPS).
                setType(ProductType.IPS);
    }
}
