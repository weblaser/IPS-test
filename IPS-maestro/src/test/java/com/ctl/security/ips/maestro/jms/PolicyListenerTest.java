package com.ctl.security.ips.maestro.jms;

import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import com.ctl.security.ips.maestro.service.PolicyServiceWrite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PolicyListenerTest {

    @InjectMocks
    private PolicyListener classUnderTest;

    @Mock
    private PolicyServiceWrite policyServiceWrite;

    @Test
    public void createPolicyForAccount_createsPolicyForAccount() throws DsmClientException {
        PolicyBean policyBean = null;

        classUnderTest.createPolicyForAccount(policyBean);

        verify(policyServiceWrite).createPolicyForAccount(policyBean);
    }
    @Test
    public void deletePolicyForAccount_deletesPolicyForAccount() throws DsmClientException {
        PolicyBean policyBean = null;

        classUnderTest.deletePolicyForAccount(policyBean);

        verify(policyServiceWrite).deletePolicyForAccount(policyBean);
    }

}