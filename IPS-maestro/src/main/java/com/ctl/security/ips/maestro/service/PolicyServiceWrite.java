package com.ctl.security.ips.maestro.service;

import com.ctl.security.clc.client.common.domain.ClcExecutePackageRequest;
import com.ctl.security.clc.client.common.domain.SoftwarePackage;
import com.ctl.security.data.client.service.CmdbService;
import com.ctl.security.data.common.domain.mongo.bean.InstallationBean;
import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.common.domain.SecurityTenant;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.DsmTenantClient;
import com.ctl.security.ips.dsm.exception.AgentInstallException;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import com.ctl.security.ips.dsm.factory.DsmAgentInstallPackageFactory;
import com.ctl.security.ips.service.PolicyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PolicyServiceWrite extends PolicyService {

    private static final Logger logger = LogManager.getLogger(PolicyServiceWrite.class);

    @Value("${ips.packageExecution.deleteAgent}")
    private String deleteAgentPackageId;

    @Autowired
    private DsmPolicyClient dsmPolicyClient;

    @Autowired
    private CmdbService cmdbService;

    @Autowired
    private DsmTenantClient dsmTenantClient;

    @Autowired
    private PackageExecutionService packageExecutionService;

    @Autowired
    private DsmAgentInstallPackageFactory dsmAgentInstallPackageFactory;

    public Policy createPolicyForAccount(PolicyBean policyBean) throws DsmClientException, AgentInstallException {
        logger.info("Creating policy for account " + policyBean.getPolicy().getName());
        PolicyBean newlyCreatedPolicyBean = dsmPolicyClient.createPolicyWithParentPolicy(policyBean);

        SecurityTenant createdSecurityTenant = dsmTenantClient.createDsmTenant(buildSecurityTenant(policyBean));
        logger.info("Tenant " + createdSecurityTenant.getTenantId() + " was created");

        installAgentForTenantUsingPolicy(createdSecurityTenant, newlyCreatedPolicyBean);

        return newlyCreatedPolicyBean.getPolicy().setTenantId(createdSecurityTenant.getTenantId().toString());
    }

    private void installAgentForTenantUsingPolicy(SecurityTenant tenant, PolicyBean policyBean) throws AgentInstallException {
        packageExecutionService.executePackage(
                dsmAgentInstallPackageFactory.configurePackageRequest(tenant, policyBean),
                policyBean.getAccountAlias(),
                policyBean.getBearerToken());

        InstallationBean installationBean = buildInstallationBean(policyBean);
        cmdbService.installProduct(installationBean);
    }

    private SecurityTenant buildSecurityTenant(PolicyBean policyBean) {
        return new SecurityTenant().setTenantName(policyBean.getPolicy().getUsername()).setAdminEmail("test@test.com")
                .setAdminPassword("secretpassword").setAdminAccount("TestAdmin");
    }

    public void deletePolicyForAccount(PolicyBean policyBean) throws DsmClientException {
        dsmPolicyClient.securityProfileDelete(Arrays.asList(Integer.parseInt(policyBean.getPolicy().getVendorPolicyId())));
        packageExecutionService.executePackage(createDeletePackageRequest(policyBean.getPolicy()), policyBean.getAccountAlias(), policyBean.getBearerToken());
        cmdbService.uninstallProduct(buildInstallationBean(policyBean));
    }

    private ClcExecutePackageRequest createDeletePackageRequest(Policy policy) {
        ClcExecutePackageRequest packageRequest = new ClcExecutePackageRequest();
        packageRequest.addServer(policy.getHostName());
        packageRequest.setSoftwarePackage(new SoftwarePackage().setPackageId(deleteAgentPackageId));
        return packageRequest;
    }
}