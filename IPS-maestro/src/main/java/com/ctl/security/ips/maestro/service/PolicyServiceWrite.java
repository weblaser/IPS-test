package com.ctl.security.ips.maestro.service;


import com.ctl.security.data.client.service.CmdbService;
import com.ctl.security.data.common.domain.mongo.bean.InstallationBean;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import com.ctl.security.ips.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PolicyServiceWrite extends PolicyService {


    @Autowired
    private DsmPolicyClient dsmPolicyClient;

    @Autowired
    private CmdbService cmdbService;

    public Policy createPolicyForAccount(PolicyBean policyBean) throws DsmPolicyClientException {
        Policy newlyCreatedPolicy = dsmPolicyClient.createCtlSecurityProfile(policyBean.getPolicy());
        InstallationBean installationBean = buildInstallationBean(policyBean);
        cmdbService.installProduct(installationBean);
        return newlyCreatedPolicy;
    }





}