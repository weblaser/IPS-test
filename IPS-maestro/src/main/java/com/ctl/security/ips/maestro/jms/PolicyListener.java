package com.ctl.security.ips.maestro.jms;

import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.jms.PolicyOperation;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import com.ctl.security.ips.maestro.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Session;

/**
 * Created by kevin.wilde on 2/13/2015.
 */

@Component
public class PolicyListener {

    @Autowired
    private PolicyService policyService;

    @JmsListener(destination = PolicyOperation.CREATE_POLICY_FOR_ACCOUNT)
    public void createPolicyForAccount(PolicyBean policyBean) throws DsmPolicyClientException {
        System.out.println("hello");

        policyService.createPolicyForAccount(policyBean);
    }
}
