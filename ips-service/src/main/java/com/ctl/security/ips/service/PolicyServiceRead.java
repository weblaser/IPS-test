package com.ctl.security.ips.service;

import com.ctl.security.data.client.service.CmdbService;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin.wilde on 2/17/2015.
 *
 */

@Component
public class PolicyServiceRead extends PolicyService {

    private static final String TEST_ID = "12345";
    public static final String TREND_MICRO_IPS = "Trend Micro IPS";

    @Autowired
    private DsmPolicyClient dsmPolicyClient;

    @Autowired
    private CmdbService cmdbService;


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

    public void updatePolicyForAccount(String account, String id, Policy policy) {

    }


}
