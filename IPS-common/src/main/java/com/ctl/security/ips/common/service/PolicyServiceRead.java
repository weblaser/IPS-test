package com.ctl.security.ips.common.service;

import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin.wilde on 2/17/2015.
 */

@Component
public class PolicyServiceRead {

    public static final String VALID_ACCOUNT = "TCCD";
    public static final String TEST_ID = "test-vendorPolicyId";


    public List<Policy> getPoliciesForAccount(String account) {

        if (VALID_ACCOUNT.equalsIgnoreCase(account)) {
            List<Policy> hopeful = new ArrayList<>();
            Policy policy = buildPolicy();
            hopeful.add(policy);
            return hopeful;
        }
        throw new PolicyNotFoundException("Policy not found for accountId: " + account);
    }

    public Policy getPolicyForAccount(String account, String id) {

        if (VALID_ACCOUNT.equalsIgnoreCase(account)) {
            return buildPolicy();
        }
        throw new PolicyNotFoundException("Policy " + id + " not found for accountId: " + account);
    }

    public Policy buildPolicy() {
        Policy policy = new Policy();
        policy.setVendorPolicyId(TEST_ID);
        policy.setStatus(PolicyStatus.ACTIVE);
        return policy;
    }
}
