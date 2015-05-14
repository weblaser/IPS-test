package com.ctl.security.ips.maestro.jms;

import com.ctl.security.clc.client.common.exception.PackageExecutionException;
import com.ctl.security.ips.common.jms.PolicyOperation;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.exception.AgentInstallException;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import com.ctl.security.ips.maestro.service.PolicyServiceWrite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class PolicyListener {

    @Autowired
    private PolicyServiceWrite policyServiceWrite;

    @JmsListener(destination = PolicyOperation.CREATE_POLICY_FOR_ACCOUNT)
    public void createPolicyForAccount(PolicyBean policyBean) throws DsmClientException, AgentInstallException, PackageExecutionException {
        policyServiceWrite.createPolicyForAccount(policyBean);
    }

    @JmsListener(destination = PolicyOperation.DELETE_POLICY_FOR_ACCOUNT)
    public void deletePolicyForAccount(PolicyBean policyBean) throws DsmClientException {
        policyServiceWrite.deletePolicyForAccount(policyBean);
    }
}
