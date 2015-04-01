package com.ctl.security.ips.maestro.service;

import com.ctl.security.clc.client.common.domain.ClcExecutePackageRequest;
import com.ctl.security.data.client.service.CmdbService;
import com.ctl.security.data.common.domain.mongo.Product;
import com.ctl.security.data.common.domain.mongo.ProductType;
import com.ctl.security.data.common.domain.mongo.bean.InstallationBean;
import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.common.domain.Policy.PolicyStatus;
import com.ctl.security.ips.common.domain.SecurityTenant;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.DsmTenantClient;
import com.ctl.security.ips.dsm.exception.DsmClientException;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PolicyServiceWriteTest {

    private static final String VALID_ACCOUNT = "TCCD";
    private static final String TEST_ID = "12345";
    private static final String TOKEN = "Token";
    private static final String USERNAME = "Bob Loblaw";
    private static final String HOSTNAME = "VM2BINSTLD";

    @InjectMocks
    private PolicyServiceWrite classUnderTest;

    @Mock
    private DsmPolicyClient dsmPolicyClient;

    @Mock
    private CmdbService cmdbService;

    @Mock
    private PolicyServiceRead policyServiceRead;

    @Mock
    private DsmTenantClient dsmTenantClient;

    @Mock
    private PackageInstallationService packageInstallationService;
    @Mock
    private ClcExecutePackageRequest clcExecutePackageRequest;

    @Test
    public void createPolicy_createsPolicy() throws ManagerLockoutException_Exception, ManagerAuthenticationException_Exception, ManagerAuthorizationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerSecurityException_Exception, ManagerTimeoutException_Exception, DsmClientException {
        //arrange
        Policy policyToBeCreated = buildPolicy();
        Policy expectedNewlyCreatedPolicy = new Policy();
        when(dsmPolicyClient.createCtlSecurityProfile(policyToBeCreated)).thenReturn(expectedNewlyCreatedPolicy);
        Product product = buildProduct();
        PolicyBean policyBean = new PolicyBean(VALID_ACCOUNT, policyToBeCreated, TOKEN);
        SecurityTenant securityTenant = new SecurityTenant();
        SecurityTenant createdSecurityTenant = new SecurityTenant().setTenantId(1);
        when(dsmTenantClient.createDsmTenant(securityTenant)).thenReturn(createdSecurityTenant);

        //act
        Policy actualNewlyPersistedPolicy = classUnderTest.createPolicyForAccount(policyBean);

        //assert
        verify(dsmPolicyClient).createCtlSecurityProfile(policyToBeCreated);
        assertNotNull(actualNewlyPersistedPolicy);
        assertEquals(expectedNewlyCreatedPolicy, actualNewlyPersistedPolicy);
        verify(cmdbService).installProduct(new InstallationBean(policyBean.getPolicy().getUsername(), VALID_ACCOUNT, policyBean.getPolicy().getHostName(), product));
    }

    @Test
    public void testCreatePolicy_createsPolicyWithTenant() throws ManagerLockoutException_Exception, ManagerAuthenticationException_Exception, ManagerAuthorizationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerSecurityException_Exception, ManagerTimeoutException_Exception, DsmClientException {

        //arrange
        Policy policyToBeCreated = new Policy();
        Policy expectedNewlyCreatedPolicy = new Policy();
        when(dsmPolicyClient.createCtlSecurityProfile(policyToBeCreated)).thenReturn(expectedNewlyCreatedPolicy);
        PolicyBean policyBean = new PolicyBean(VALID_ACCOUNT, policyToBeCreated, TOKEN);
        SecurityTenant securityTenant = new SecurityTenant();
        SecurityTenant createdSecurityTenant = new SecurityTenant().setTenantId(1);
        when(dsmTenantClient.createDsmTenant(securityTenant)).thenReturn(createdSecurityTenant);

        //act
        Policy actualNewlyPersistedPolicy = classUnderTest.createPolicyForAccount(policyBean);

        //assert
        verify(dsmTenantClient).createDsmTenant(securityTenant);
        assertEquals(expectedNewlyCreatedPolicy.getTenantId(), actualNewlyPersistedPolicy.getTenantId());
    }

    @Test
    public void testCreatePolicy_createsPolicyWithTenantAndPackageInstall() throws ManagerLockoutException_Exception, ManagerAuthenticationException_Exception, ManagerAuthorizationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerSecurityException_Exception, ManagerTimeoutException_Exception, DsmClientException {

        //arrange
        Policy policyToBeCreated = new Policy();
        Policy expectedNewlyCreatedPolicy = new Policy();
        when(dsmPolicyClient.createCtlSecurityProfile(policyToBeCreated)).thenReturn(expectedNewlyCreatedPolicy);
        PolicyBean policyBean = new PolicyBean(VALID_ACCOUNT, policyToBeCreated, TOKEN);
        SecurityTenant securityTenant = new SecurityTenant();
        SecurityTenant createdSecurityTenant = new SecurityTenant().setTenantId(1);
        when(dsmTenantClient.createDsmTenant(securityTenant)).thenReturn(createdSecurityTenant);

        //act
        classUnderTest.createPolicyForAccount(policyBean);

        //assert
        verify(packageInstallationService).installClcPackage(any(ClcExecutePackageRequest.class), anyString(), anyString());
    }

    @Test
    public void testDeletePolicyForAccount() throws DsmClientException {
        //arrange

        //act
        PolicyBean policyBean = new PolicyBean(VALID_ACCOUNT, buildPolicy().setUsername(USERNAME).setHostName(HOSTNAME), TOKEN);
        classUnderTest.deletePolicyForAccount(policyBean);

        //assert
        verify(dsmPolicyClient).securityProfileDelete(any(List.class));
        verify(cmdbService).uninstallProduct(new InstallationBean(USERNAME, VALID_ACCOUNT, HOSTNAME, buildProduct()));
    }


    private Policy buildPolicy() {
        return new Policy().setVendorPolicyId(TEST_ID).setStatus(PolicyStatus.ACTIVE).setUsername(USERNAME).setHostName(HOSTNAME);
    }

    private Product buildProduct() {
        return new Product().
                setName(PolicyServiceRead.TREND_MICRO_IPS).
                setType(ProductType.IPS);
    }
}