package com.ctl.security.ips.api.resource.impl;

import com.ctl.security.ips.api.jms.PolicyMessageSender;
import com.ctl.security.ips.api.resource.PolicyResource;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import com.ctl.security.ips.service.PolicyServiceRead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PolicyResourceImpl implements PolicyResource {

    @Autowired
    private PolicyServiceRead policyServiceRead;

    @Autowired
    private PolicyMessageSender policyMessageSender;

    @Override
    public void createPolicyForAccount(String accountId, Policy policy) {
        PolicyBean policyBean = new PolicyBean(accountId, policy);
        policyMessageSender.createPolicyForAccount(policyBean);
    }

    @Override
    public List<Policy> getPoliciesForAccount(String accountId) {
        return policyServiceRead.getPoliciesForAccount(accountId);
    }

    @Override
    public Policy getPolicyForAccount(String account, String policyId) {
        return policyServiceRead.getPolicyForAccount(account, policyId);
    }

    @Override
    public void updatePolicyForAccount(String account, String policyId, Policy policy) {
        policyServiceRead.updatePolicyForAccount(account, policyId, policy);
    }

    //TODO this method needs to take a policy object as a body in order to get all the data we need
    @Override
    public void deletePolicyForAccount(String accountId,String policyId, Policy policy) throws DsmPolicyClientException {
//        PolicyBean policyBean = new PolicyBean(accountId, policy);
//        policyMessageSender.deletePolicyForAccount(policyBean);
        policyServiceRead.deletePolicyForAccount(new PolicyBean(accountId, policy));
    }

}
