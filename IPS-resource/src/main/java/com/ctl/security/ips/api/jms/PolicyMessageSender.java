package com.ctl.security.ips.api.jms;

import com.ctl.security.ips.common.jms.PolicyOperation;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by kevin.wilde on 2/12/2015.
 *
 */

@Component
public class PolicyMessageSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void createPolicyForAccount(final PolicyBean policyBean) {
        jmsTemplate.convertAndSend(PolicyOperation.CREATE_POLICY_FOR_ACCOUNT, policyBean);
    }

    public void deletePolicyForAccount(final PolicyBean policyBean) {
        jmsTemplate.convertAndSend(PolicyOperation.DELETE_POLICY_FOR_ACCOUNT, policyBean);
    }
}
