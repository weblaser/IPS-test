package com.ctl.security.ips.maestro.service;

import com.ctl.security.clc.client.common.domain.ClcExecutePackageRequest;
import com.ctl.security.clc.client.common.domain.ClcServerDetailsResponse;
import com.ctl.security.clc.client.core.bean.ServerClient;
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
import com.ctl.security.ips.dsm.exception.AgentInstallException;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import com.ctl.security.ips.dsm.factory.DsmAgentInstallPackageFactory;
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
import static org.mockito.Matchers.eq;
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

    @Mock
    private ServerClient serverClient;

    @Mock
    private ClcServerDetailsResponse clcServerDetailsResponse;

    @Mock
    private DsmAgentInstallPackageFactory dsmAgentInstallPackageFactory;

    @Test
    public void createPolicy_createsPolicy() throws ManagerLockoutException_Exception, ManagerAuthenticationException_Exception, ManagerAuthorizationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerSecurityException_Exception, ManagerTimeoutException_Exception, DsmClientException, AgentInstallException {
        //arrange
        Policy policyToBeCreated = buildPolicy();
        Policy policyToBeCreatedWithParent = buildPolicy().setParentPolicyId("1");

        PolicyBean policyToBeCreatedBean = new PolicyBean(VALID_ACCOUNT, policyToBeCreated, TOKEN);
        PolicyBean expectedNewlyCreatedPolicy = new PolicyBean(VALID_ACCOUNT, policyToBeCreatedWithParent, TOKEN);

        Product product = buildProduct();
        SecurityTenant createdSecurityTenant = new SecurityTenant().setTenantId(1);
        String hostOs = "Windows_OS";

        when(dsmTenantClient.createDsmTenant(any(SecurityTenant.class))).thenReturn(createdSecurityTenant);
        when(dsmPolicyClient.createPolicyWithParentPolicy(policyToBeCreatedBean)).thenReturn(expectedNewlyCreatedPolicy);
        when(serverClient.getServerDetails(policyToBeCreatedBean.getAccountAlias(),
                policyToBeCreatedBean.getPolicy().getHostName(),
                policyToBeCreatedBean.getBearerToken()))
                .thenReturn(clcServerDetailsResponse);
        when(clcServerDetailsResponse.getOs()).thenReturn(hostOs);

        //act
        Policy result = classUnderTest.createPolicyForAccount(policyToBeCreatedBean);

        //assert
        verify(dsmPolicyClient).createPolicyWithParentPolicy(policyToBeCreatedBean);
        assertNotNull(result);
        assertEquals(expectedNewlyCreatedPolicy.getPolicy(), result);
        verify(cmdbService).installProduct(
                new InstallationBean(policyToBeCreatedBean.getPolicy().getUsername(),
                        VALID_ACCOUNT,
                        policyToBeCreatedBean.getPolicy().getHostName(),
                        product));
    }

    @Test
    public void testCreatePolicy_createsPolicyWithTenant() throws ManagerLockoutException_Exception, ManagerAuthenticationException_Exception, ManagerAuthorizationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerSecurityException_Exception, ManagerTimeoutException_Exception, DsmClientException, AgentInstallException {

        //arrange
        Policy policyToBeCreated = new Policy().setUsername("This is my test createTenant");
        PolicyBean policyToBeCreatedBean = new PolicyBean(VALID_ACCOUNT, policyToBeCreated, TOKEN);
        PolicyBean expectedNewlyCreatedPolicy = new PolicyBean(VALID_ACCOUNT, policyToBeCreated, TOKEN);
        SecurityTenant createdSecurityTenant = new SecurityTenant().setTenantId(1);
        String hostOs = "Windows_OS";

        when(dsmTenantClient.createDsmTenant(any(SecurityTenant.class))).thenReturn(createdSecurityTenant);
        when(dsmPolicyClient.createPolicyWithParentPolicy(policyToBeCreatedBean)).thenReturn(expectedNewlyCreatedPolicy);
        when(serverClient.getServerDetails(policyToBeCreatedBean.getAccountAlias(),
                        policyToBeCreatedBean.getPolicy().getHostName(),
                        policyToBeCreatedBean.getBearerToken())
        ).thenReturn(clcServerDetailsResponse);

        when(clcServerDetailsResponse.getOs()).thenReturn(hostOs);

        //act
        Policy actualNewlyPersistedPolicy = classUnderTest.createPolicyForAccount(policyToBeCreatedBean);

        //assert
        verify(dsmTenantClient).createDsmTenant(any(SecurityTenant.class));
        assertEquals(expectedNewlyCreatedPolicy.getPolicy().getTenantId(), actualNewlyPersistedPolicy.getTenantId());
    }

    @Test
    public void testCreatePolicy_createsPolicyWithTenantAndPackageInstall() throws ManagerLockoutException_Exception, ManagerAuthenticationException_Exception, ManagerAuthorizationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerSecurityException_Exception, ManagerTimeoutException_Exception, DsmClientException, AgentInstallException {

        //arrange
        Policy policyToBeCreated = new Policy().setUsername("This is my test createTenant");
        PolicyBean policyToBeCreatedBean = new PolicyBean(VALID_ACCOUNT, policyToBeCreated, TOKEN);
        PolicyBean expectedNewlyCreatedPolicy = new PolicyBean(VALID_ACCOUNT, policyToBeCreated, TOKEN);
        SecurityTenant createdSecurityTenant = new SecurityTenant().setTenantId(1);
        String hostOs = "Windows_OS";

        when(dsmTenantClient.createDsmTenant(any(SecurityTenant.class))).thenReturn(createdSecurityTenant);
        when(serverClient.getServerDetails(policyToBeCreatedBean.getAccountAlias(), policyToBeCreatedBean.getPolicy().getHostName(), policyToBeCreatedBean.getBearerToken())).thenReturn(clcServerDetailsResponse);
        when(dsmPolicyClient.createPolicyWithParentPolicy(policyToBeCreatedBean)).thenReturn(expectedNewlyCreatedPolicy);
        when(clcServerDetailsResponse.getOs()).thenReturn(hostOs);

        //act
        classUnderTest.createPolicyForAccount(policyToBeCreatedBean);

        //assert
        verify(packageInstallationService).installClcPackage(any(ClcExecutePackageRequest.class), anyString(), anyString());
    }

    @Test
    public void testCreatePolicy_createsPolicyWithTenantAndPackageInstallOnLinux() throws ManagerLockoutException_Exception, ManagerAuthenticationException_Exception, ManagerAuthorizationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerSecurityException_Exception, ManagerTimeoutException_Exception, DsmClientException, AgentInstallException {

        //arrange
        Policy policyToBeCreated = new Policy().setUsername("This is my test createTenant");
        PolicyBean policyToBeCreatedBean = new PolicyBean(VALID_ACCOUNT, policyToBeCreated, TOKEN);
        PolicyBean expectedNewlyCreatedPolicy = new PolicyBean(VALID_ACCOUNT, policyToBeCreated, TOKEN);
        SecurityTenant createdSecurityTenant = new SecurityTenant().setTenantId(1);
        String hostOs = "Linus_OS";

        when(dsmTenantClient.createDsmTenant(any(SecurityTenant.class))).thenReturn(createdSecurityTenant);
        when(dsmPolicyClient.createPolicyWithParentPolicy(policyToBeCreatedBean)).thenReturn(expectedNewlyCreatedPolicy);
        when(serverClient.getServerDetails(policyToBeCreatedBean.getAccountAlias(),
                        policyToBeCreatedBean.getPolicy().getHostName(),
                        policyToBeCreatedBean.getBearerToken())
        ).thenReturn(clcServerDetailsResponse);

        when(clcServerDetailsResponse.getOs()).thenReturn(hostOs);

        //act
        classUnderTest.createPolicyForAccount(policyToBeCreatedBean);

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