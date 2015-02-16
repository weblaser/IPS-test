package com.ctl.security.ips.maestro.service;


import com.ctl.security.data.client.service.CmdbService;
import com.ctl.security.data.common.domain.mongo.Product;
import com.ctl.security.data.common.domain.mongo.ProductStatus;
import com.ctl.security.data.common.domain.mongo.ProductType;
import com.ctl.security.data.common.domain.mongo.bean.InstallationBean;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.common.exception.NotAuthorizedException;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import com.ctl.security.ips.common.jms.PolicyOperation;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
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

    @JmsListener(destination = PolicyOperation.CREATE_POLICY_FOR_ACCOUNT)
    public Policy createPolicyForAccount(PolicyBean policyBean) throws DsmPolicyClientException {

        if (VALID_ACCOUNT.equalsIgnoreCase(policyBean.getAccountId())) {
            Policy newlyCreatedPolicy = dsmPolicyClient.createCtlSecurityProfile(policyBean.getPolicy());

            String username = policyBean.getPolicy().getUsername();
            String serverDomainName = policyBean.getPolicy().getServerDomainName();
            Product product = new Product().
                    setName(TREND_MICRO_IPS).
                    setStatus(ProductStatus.ACTIVE).
                    setType(ProductType.IPS);
            InstallationBean installationBean = new InstallationBean(username, policyBean.getAccountId(), serverDomainName, product);


            cmdbService.installProduct(installationBean);
            return newlyCreatedPolicy;
        }
        throw new NotAuthorizedException("Policy cannot be created under accountId: " + policyBean.getAccountId());
    }


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


    public void updatePolicyForAccount(String account, String id, Policy policy) {

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

    private Policy buildPolicy() {
        Policy policy = new Policy();
        policy.setVendorPolicyId(TEST_ID);
        policy.setStatus(PolicyStatus.ACTIVE);
        return policy;
    }
}
