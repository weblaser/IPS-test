package com.ctl.security.ips.api.resource.impl;

import com.ctl.security.ips.api.jms.PolicyMessageSender;
import com.ctl.security.ips.api.resource.PolicyResource;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import com.ctl.security.ips.maestro.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PolicyResourceImpl implements PolicyResource {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private PolicyMessageSender policyMessageSender;

    @Override
    public void createPolicyForAccount(String accountId, Policy policy) {
        PolicyBean policyBean = new PolicyBean(accountId, policy);
        policyMessageSender.createPolicyForAccount(policyBean);
    }

    @Override
    public List<Policy> getPoliciesForAccount(String accountId) {
        return policyService.getPoliciesForAccount(accountId);
    }

    @Override
    public Policy getPolicyForAccount(String account, String policyId) {
        return policyService.getPolicyForAccount(account, policyId);
    }

    @Override
    public void updatePolicyForAccount(String account, String policyId, Policy policy) {
        policyService.updatePolicyForAccount(account, policyId, policy);
    }

    @Override
    public void deletePolicyForAccount(String account, String policyId) {
        try {
            PolicyBean policyBean = new PolicyBean(account, new Policy().setVendorPolicyId(policyId));
            policyService.deletePolicyForAccount(policyBean);
        } catch (DsmPolicyClientException e) {
            e.printStackTrace();
        }
    }

}
