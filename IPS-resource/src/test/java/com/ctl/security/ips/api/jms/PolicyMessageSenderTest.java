package com.ctl.security.ips.api.jms;

import com.ctl.security.ips.common.jms.PolicyOperation;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PolicyMessageSenderTest {

    @InjectMocks
    private PolicyMessageSender classUnderTest;

    @Mock
    private JmsTemplate jmsTemplate;

    @Test
    public void createPolicyForAccount_sendsMessage() {
        PolicyBean policyBean = null;
        
        classUnderTest.createPolicyForAccount(policyBean);

        verify(jmsTemplate).convertAndSend(PolicyOperation.CREATE_POLICY_FOR_ACCOUNT, policyBean);
    }

    @Test
    public void testDeletePolicyForAccount_sendsMessage() throws Exception {
        PolicyBean policyBean = null;

        classUnderTest.deletePolicyForAccount(policyBean);

        verify(jmsTemplate).convertAndSend(PolicyOperation.DELETE_POLICY_FOR_ACCOUNT, policyBean);
    }
}