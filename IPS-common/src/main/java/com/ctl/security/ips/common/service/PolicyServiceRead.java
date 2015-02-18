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

    private static final String TEST_ID = "12345";


    public List<Policy> getPoliciesForAccount(String account) {
        List<Policy> hopeful = new ArrayList<>();
        Policy policy = buildPolicy();
        hopeful.add(policy);
        return hopeful;
    }

    public Policy getPolicyForAccount(String account, String id) {
        return buildPolicy();
    }

    private Policy buildPolicy() {
        return new Policy().setVendorPolicyId(TEST_ID).setStatus(PolicyStatus.ACTIVE);
    }
}
