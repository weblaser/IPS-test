package com.ctl.security.ips.api.resource.impl;

import com.ctl.security.ips.api.resource.PolicyResource;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import com.ctl.security.ips.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PolicyResourceImpl implements PolicyResource {

    @Autowired
    private PolicyService policyService;

    @Override
    public List<Policy> getPoliciesForAccount(String account) {
        return policyService.getPoliciesForAccount(account);
    }

    @Override
    public Policy getPolicyForAccount(String account, String policyId) {
        return policyService.getPolicyForAccount(account, policyId);
    }

    @Override
    public Policy createPolicyForAccount(String account, Policy policy) {
        try {
            return policyService.createPolicyForAccount(account, policy);
        } catch (DsmPolicyClientException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void updatePolicyForAccount(String account, String policyId, Policy policy) {
        policyService.updatePolicyForAccount(account, policyId, policy);
    }

    @Override
    public void deletePolicyForAccount(String account, String policyId) {
        policyService.deletePolicyForAccount(account, policyId);
    }

}
