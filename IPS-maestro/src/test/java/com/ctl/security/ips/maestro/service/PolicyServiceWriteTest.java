package com.ctl.security.ips.maestro.service;

import com.ctl.security.data.client.service.CmdbService;
import com.ctl.security.data.common.domain.mongo.Product;
import com.ctl.security.data.common.domain.mongo.ProductType;
import com.ctl.security.data.common.domain.mongo.bean.InstallationBean;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import com.ctl.security.ips.service.PolicyServiceRead;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PolicyServiceWriteTest {

    private static final String VALID_ACCOUNT = "TCCD";
    private static final String TEST_ID = "12345";


    @InjectMocks
    private PolicyServiceWrite classUnderTest;

    @Mock
    private DsmPolicyClient dsmPolicyClient;

    @Mock
    private CmdbService cmdbService;

    @Mock
    private PolicyServiceRead policyServiceRead;

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
    public void testDeletePolicyForAccount() throws DsmPolicyClientException {
        //arrange
        String username = null;
        String accountId = VALID_ACCOUNT;
        String serverDomainName = null;

        //act
        PolicyBean policyBean = new PolicyBean(accountId, buildPolicy().setUsername(username).setHostName(serverDomainName));
        classUnderTest.deletePolicyForAccount(policyBean);

        //assert
        verify(dsmPolicyClient).securityProfileDelete(any(List.class));
        verify(cmdbService).uninstallProduct(new InstallationBean(username, accountId, serverDomainName, buildProduct()));
    }


    private Policy buildPolicy() {
        return new Policy().setVendorPolicyId(TEST_ID).setStatus(PolicyStatus.ACTIVE);
    }

    private Product buildProduct() {
        return new Product().
                setName(PolicyServiceRead.TREND_MICRO_IPS).
                setType(ProductType.IPS);
    }
}