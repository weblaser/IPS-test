package com.ctl.security.ips.dsm.factory;

import com.ctl.security.clc.client.common.domain.ClcExecutePackageRequest;
import com.ctl.security.clc.client.core.bean.ServerClient;
import com.ctl.security.ips.common.domain.SecurityTenant;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.exception.AgentInstallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * Created by Chad Middleton on 4/20/2015.
 *
 */
@Service
public class DsmAgentInstallPackageFactory {


    @Autowired
    private ServerClient serverClient;

    @Value("${${spring.profiles.active:local}.dsm.rest.host}")
    private String dsmMothership;

    @Value("#{SecurityLibraryPropertySplitter.map('${ips.packageExecution.osLinks}')}")
    private HashMap<String, String> osOptions;

    public ClcExecutePackageRequest configurePackageRequest(SecurityTenant createdSecurityTenant, PolicyBean newlyCreatedPolicyBean) throws AgentInstallException {
        ClcExecutePackageRequest clcExecutePackageRequest = new ClcExecutePackageRequest()
                .addServer(newlyCreatedPolicyBean.getPolicy().getHostName());
        clcExecutePackageRequest.getSoftwarePackage().setPackageId(configurePackageId(newlyCreatedPolicyBean));
        configureParameters(clcExecutePackageRequest, newlyCreatedPolicyBean, createdSecurityTenant);
        return clcExecutePackageRequest;
    }

    private String configurePackageId(PolicyBean policyBean) throws AgentInstallException {
        String hostOs = serverClient.getServerDetails(policyBean.getAccountAlias(), policyBean.getPolicy().getHostName(), policyBean.getBearerToken()).getOs();

        String uuidForOs = osOptions.get(hostOs);
        if (StringUtils.isEmpty(uuidForOs)) {
            throw new AgentInstallException(new Exception(hostOs + " package UUID not found"));
        }
        return uuidForOs;
    }

    private void configureParameters(ClcExecutePackageRequest clcExecutePackageRequest, PolicyBean policyBean, SecurityTenant createdSecurityTenant) {
        clcExecutePackageRequest.getSoftwarePackage()
                .addParameter("DSM.Tenant.ID", createdSecurityTenant.getGuid())
                .addParameter("DSM.Agent.Activation.Password", createdSecurityTenant.getAgentInitiatedActivationPassword())
                .addParameter("DSM.Policy.ID", policyBean.getPolicy().getVendorPolicyId())
                .addParameter("DSM.Name", dsmMothership)
                .addParameter("T3.Bearer.Token", policyBean.getBearerToken())
                .addParameter("T3.Account.Alias", policyBean.getAccountAlias());
    }
}

