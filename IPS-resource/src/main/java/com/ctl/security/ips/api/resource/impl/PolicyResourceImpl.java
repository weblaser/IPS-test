package com.ctl.security.ips.api.resource.impl;

import com.ctl.security.ips.api.jms.PolicyMessageSender;
import com.ctl.security.ips.api.resource.PolicyResource;
import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.exception.DsmClientException;
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
    public void createPolicyForAccount(String accountId, Policy policy, String bearerToken) {
        PolicyBean policyBean = new PolicyBean(accountId, policy, bearerToken.substring(7));
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
    public void deletePolicyForAccount(String accountId, String policyId, String serverDomainName, String username, String bearerToken) throws DsmClientException {
        Policy policy = new Policy();
        policy.setVendorPolicyId(policyId);
        policy.setHostName(serverDomainName);
        policy.setUsername(username);
        policyMessageSender.deletePolicyForAccount(new PolicyBean(accountId, policy, bearerToken.substring(7)));
    }

}
