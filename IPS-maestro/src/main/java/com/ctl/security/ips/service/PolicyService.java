package com.ctl.security.ips.service;


import com.ctl.security.data.client.service.CmdbService;
import com.ctl.security.data.common.domain.mongo.Product;
import com.ctl.security.data.common.domain.mongo.ProductStatus;
import com.ctl.security.data.common.domain.mongo.ProductType;
import com.ctl.security.data.common.domain.mongo.bean.InstallationBean;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.common.exception.NotAuthorizedException;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PolicyService {

    private static final String VALID_ACCOUNT = "TCCD";
    private static final String TEST_ID = "test-vendorPolicyId";
    public static final String TREND_MICRO_IPS = "Trend Micro IPS";

    @Autowired
    private DsmPolicyClient dsmPolicyClient;

    @Autowired
    private CmdbService cmdbService;

    public Policy createPolicyForAccount(String accountId, Policy policy) throws DsmPolicyClientException {

        if (VALID_ACCOUNT.equalsIgnoreCase(accountId)) {
            Policy newlyCreatedPolicy = dsmPolicyClient.createCtlSecurityProfile(policy);

            String username = policy.getUsername();
            String serverDomainName = policy.getServerDomainName();
            Product product = new Product().
                    setName(TREND_MICRO_IPS).
                    setStatus(ProductStatus.ACTIVE).
                    setType(ProductType.IPS);
            InstallationBean installationBean = new InstallationBean(username, accountId, serverDomainName, product);


            cmdbService.installProduct(installationBean);
            return newlyCreatedPolicy;
        }
        throw new NotAuthorizedException("Policy cannot be created under accountId: " + accountId);
    }


    public List<com.ctl.security.ips.common.domain.Policy> getPoliciesForAccount(String account) {

        if (VALID_ACCOUNT.equalsIgnoreCase(account)) {
            List<com.ctl.security.ips.common.domain.Policy> hopeful = new ArrayList<com.ctl.security.ips.common.domain.Policy>();
            com.ctl.security.ips.common.domain.Policy policy = buildPolicy();
            hopeful.add(policy);
            return hopeful;
        }
        throw new PolicyNotFoundException("Policy not found for accountId: " + account);
    }

    public com.ctl.security.ips.common.domain.Policy getPolicyForAccount(String account, String id) {

        if (VALID_ACCOUNT.equalsIgnoreCase(account)) {
            return buildPolicy();
        }
        throw new PolicyNotFoundException("Policy " + id + " not found for accountId: " + account);
    }


    public void updatePolicyForAccount(String account, String id, com.ctl.security.ips.common.domain.Policy policy) {

        if (VALID_ACCOUNT.equalsIgnoreCase(account) && TEST_ID.equalsIgnoreCase(id)) {
            return;
        } else if (!VALID_ACCOUNT.equalsIgnoreCase(account)) {
            throw new NotAuthorizedException("Policy cannot be updated under accountId: " + account);
        }
        throw new PolicyNotFoundException("Policy " + id + " cannot be found for accountId: " + account);
    }

    public void deletePolicyForAccount(String account, String id) {
        if (VALID_ACCOUNT.equals(account) && TEST_ID.equals(id)) {
            return;
        }else if (!VALID_ACCOUNT.equalsIgnoreCase(account)){
            throw new NotAuthorizedException("Policy cannot be deleted under accountId: " + account);
        }
        throw new PolicyNotFoundException("Policy " + id + " cannot be found for accountId: " + account);
    }

    private com.ctl.security.ips.common.domain.Policy buildPolicy() {
        com.ctl.security.ips.common.domain.Policy policy = new com.ctl.security.ips.common.domain.Policy();
        policy.setVendorPolicyId(TEST_ID);
        policy.setStatus(PolicyStatus.ACTIVE);
        return policy;
    }
}
