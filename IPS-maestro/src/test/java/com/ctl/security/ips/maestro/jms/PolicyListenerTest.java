package com.ctl.security.ips.maestro.jms;

import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import com.ctl.security.ips.maestro.service.PolicyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.jms.Session;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PolicyListenerTest {

    @InjectMocks
    private PolicyListener classUnderTest;

    @Mock
    private PolicyService policyService;

    @Test
    public void createPolicyForAccount_createsPolicyForAccount() throws DsmPolicyClientException {
        PolicyBean policyBean = null;

        classUnderTest.createPolicyForAccount(policyBean);

        verify(policyService).createPolicyForAccount(policyBean);
    }

}