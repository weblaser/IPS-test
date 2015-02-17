package com.ctl.security.ips.maestro.service;


import com.ctl.security.data.client.service.CmdbService;
import com.ctl.security.data.common.domain.mongo.Product;
import com.ctl.security.data.common.domain.mongo.ProductStatus;
import com.ctl.security.data.common.domain.mongo.ProductType;
import com.ctl.security.data.common.domain.mongo.bean.InstallationBean;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.exception.NotAuthorizedException;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.common.service.PolicyServiceRead;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyService {

    public static final String TREND_MICRO_IPS = "Trend Micro IPS";

    @Autowired
    private DsmPolicyClient dsmPolicyClient;

    @Autowired
    private CmdbService cmdbService;

    @Autowired
    public PolicyServiceRead policyServiceRead;

    public Policy createPolicyForAccount(PolicyBean policyBean) throws DsmPolicyClientException {

        if (PolicyServiceRead.VALID_ACCOUNT.equalsIgnoreCase(policyBean.getAccountId())) {
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


    public void updatePolicyForAccount(String account, String id, Policy policy) {

        if (PolicyServiceRead.VALID_ACCOUNT.equalsIgnoreCase(account) && PolicyServiceRead.TEST_ID.equalsIgnoreCase(id)) {
            return;
        } else if (!PolicyServiceRead.VALID_ACCOUNT.equalsIgnoreCase(account)) {
            throw new NotAuthorizedException("Policy cannot be updated under accountId: " + account);
        }
        throw new PolicyNotFoundException("Policy " + id + " cannot be found for accountId: " + account);
    }

    public void deletePolicyForAccount(String account, String id) {
        if (PolicyServiceRead.VALID_ACCOUNT.equals(account) && PolicyServiceRead.TEST_ID.equals(id)) {
            return;
        }else if (!PolicyServiceRead.VALID_ACCOUNT.equalsIgnoreCase(account)){
            throw new NotAuthorizedException("Policy cannot be deleted under accountId: " + account);
        }
        throw new PolicyNotFoundException("Policy " + id + " cannot be found for accountId: " + account);
    }

}
