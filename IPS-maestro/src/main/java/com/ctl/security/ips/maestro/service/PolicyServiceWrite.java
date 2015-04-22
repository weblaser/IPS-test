package com.ctl.security.ips.maestro.service;


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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PolicyServiceWrite extends PolicyService {

    @Autowired
    private DsmPolicyClient dsmPolicyClient;

    @Autowired
    private CmdbService cmdbService;

    @Autowired
    private DsmTenantClient dsmTenantClient;

    @Autowired
    private PackageInstallationService packageInstallationService;

    @Autowired
    private DsmAgentInstallPackageFactory dsmAgentInstallPackageFactory;

    public Policy createPolicyForAccount(PolicyBean policyBean) throws DsmClientException, AgentInstallException {
        PolicyBean newlyCreatedPolicyBean = dsmPolicyClient.createPolicyWithParentPolicy(policyBean);
        InstallationBean installationBean = buildInstallationBean(newlyCreatedPolicyBean);
        cmdbService.installProduct(installationBean);
        SecurityTenant createdSecurityTenant = dsmTenantClient.createDsmTenant(new SecurityTenant());
        packageInstallationService.installClcPackage(
                dsmAgentInstallPackageFactory.configurePackageRequest(createdSecurityTenant, newlyCreatedPolicyBean),
                newlyCreatedPolicyBean.getAccountAlias(),
                newlyCreatedPolicyBean.getBearerToken());

        return newlyCreatedPolicyBean.getPolicy().setTenantId(createdSecurityTenant.getTenantId().toString());
    }

    public void deletePolicyForAccount(PolicyBean policyBean) throws DsmClientException {
        dsmPolicyClient.securityProfileDelete(Arrays.asList(Integer.parseInt(policyBean.getPolicy().getVendorPolicyId())));
        cmdbService.uninstallProduct(buildInstallationBean(policyBean));
    }

}