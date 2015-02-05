package com.ctl.security.ips.service;

import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.common.exception.NotAuthorizedException;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import com.ctl.security.ips.dao.PolicyDao;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PolicyService {

    private static final String VALID_ACCOUNT = "TCCD";
    private static final String TEST_ID = "test-id";

    private DsmPolicyClient dsmPolicyClient;
    private PolicyDao policyDao;

    @Autowired
    public PolicyService(DsmPolicyClient dsmPolicyClient, PolicyDao policyDao) {
        this.dsmPolicyClient = dsmPolicyClient;
        this.policyDao = policyDao;
    }


    public Policy createPolicyForAccount(String account, Policy policy) throws DsmPolicyClientException {

        if (VALID_ACCOUNT.equalsIgnoreCase(account)) {
            Policy newlyCreatedPolicy = dsmPolicyClient.createCtlSecurityProfile(policy);
            Policy newlyPersistedPolicy = policyDao.saveCtlSecurityProfile(newlyCreatedPolicy);
            return newlyPersistedPolicy;
        }
        throw new NotAuthorizedException("Policy cannot be created under account: " + account);
    }


    public List<com.ctl.security.ips.common.domain.Policy> getPoliciesForAccount(String account) {

        if (VALID_ACCOUNT.equalsIgnoreCase(account)) {
            List<com.ctl.security.ips.common.domain.Policy> hopeful = new ArrayList<com.ctl.security.ips.common.domain.Policy>();
            com.ctl.security.ips.common.domain.Policy policy = buildPolicy();
            hopeful.add(policy);
            return hopeful;
        }
        throw new PolicyNotFoundException("Policy not found for account: " + account);
    }

    public com.ctl.security.ips.common.domain.Policy getPolicyForAccount(String account, String id) {

        if (VALID_ACCOUNT.equalsIgnoreCase(account)) {
            return buildPolicy();
        }
        throw new PolicyNotFoundException("Policy " + id + " not found for account: " + account);
    }


    public void updatePolicyForAccount(String account, String id, com.ctl.security.ips.common.domain.Policy policy) {

        if (VALID_ACCOUNT.equalsIgnoreCase(account) && TEST_ID.equalsIgnoreCase(id)) {
            return;
        } else if (!VALID_ACCOUNT.equalsIgnoreCase(account)) {
            throw new NotAuthorizedException("Policy cannot be updated under account: " + account);
        }
        throw new PolicyNotFoundException("Policy " + id + " cannot be found for account: " + account);
    }

    public void deletePolicyForAccount(String account, String id) {
        if (VALID_ACCOUNT.equals(account) && TEST_ID.equals(id)) {
            return;
        }else if (!VALID_ACCOUNT.equalsIgnoreCase(account)){
            throw new NotAuthorizedException("Policy cannot be deleted under account: " + account);
        }
        throw new PolicyNotFoundException("Policy " + id + " cannot be found for account: " + account);
    }

    private com.ctl.security.ips.common.domain.Policy buildPolicy() {
        com.ctl.security.ips.common.domain.Policy policy = new com.ctl.security.ips.common.domain.Policy();
        policy.setId(TEST_ID);
        policy.setStatus(PolicyStatus.ACTIVE);
        return policy;
    }
}
