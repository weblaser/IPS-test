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
import com.ctl.security.ips.dsm.exception.DsmClientException;
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

    public Policy createPolicyForAccount(PolicyBean policyBean) throws DsmClientException {
        Policy newlyCreatedPolicy = dsmPolicyClient.createCtlSecurityProfile(policyBean.getPolicy());
        InstallationBean installationBean = buildInstallationBean(policyBean);
        cmdbService.installProduct(installationBean);
        SecurityTenant createdSecurityTenant = dsmTenantClient.createDsmTenant(new SecurityTenant());
        ClcExecutePackageRequest clcExecutePackageRequest = new ClcExecutePackageRequest().addServer(policyBean.getPolicy().getHostName());
        clcExecutePackageRequest.getSoftwarePackage()
                .addParameter("DSM.Tenant.ID", createdSecurityTenant.getTenantId().toString())
                .addParameter("DSM.Agent.Activation.Password", createdSecurityTenant.getAgentInitiatedActivationPassword())
                .addParameter("DSM.Policy.ID", newlyCreatedPolicy.getVendorPolicyId())
                .addParameter("DSM.Name", "") //TODO find this damn thing!!!!
                .addParameter("T3.Bearer.Token", policyBean.getBearerToken())
                .addParameter("T3.Account.Alias", policyBean.getAccountId());
        packageInstallationService.installClcPackage(clcExecutePackageRequest, policyBean.getAccountId(), policyBean.getBearerToken());
        return newlyCreatedPolicy.setTenantId(createdSecurityTenant.getTenantId().toString());
    }

    public void deletePolicyForAccount(PolicyBean policyBean) throws DsmClientException {
        dsmPolicyClient.securityProfileDelete(Arrays.asList(Integer.parseInt(policyBean.getPolicy().getVendorPolicyId())));
        cmdbService.uninstallProduct(buildInstallationBean(policyBean));
    }

}