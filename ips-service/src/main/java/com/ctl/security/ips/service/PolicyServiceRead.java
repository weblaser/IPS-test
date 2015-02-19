package com.ctl.security.ips.service;

import com.ctl.security.data.client.service.CmdbService;
import com.ctl.security.data.common.domain.mongo.Product;
import com.ctl.security.data.common.domain.mongo.ProductType;
import com.ctl.security.data.common.domain.mongo.bean.InstallationBean;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kevin.wilde on 2/17/2015.
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

    //TODO: Move this to PolicyServiceWrite when [SECURITY-301 IPS API - DELETE - Implement the IPS DELETE Active MQ] is implemented
    public void deletePolicyForAccount(PolicyBean policyBean) throws DsmPolicyClientException {
        dsmPolicyClient.securityProfileDelete(Arrays.asList(Integer.parseInt(policyBean.getPolicy().getVendorPolicyId())));
        cmdbService.uninstallProduct(buildInstallationBean(policyBean));
    }

}