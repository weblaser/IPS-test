package com.ctl.security.ips.api.resource.impl;

import com.ctl.security.ips.api.jms.PolicyMessageSender;
import com.ctl.security.ips.api.resource.PolicyResource;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import com.ctl.security.ips.service.PolicyServiceRead;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PolicyResourceImplTest {

    private static final String TEST_ACCOUNT = "TCCD";
    private static final String TEST_ID = "12345";

    @InjectMocks
    PolicyResource classUnderTest = new PolicyResourceImpl();

    @Mock
    private PolicyServiceRead policyServiceRead;

    @Mock
    private PolicyMessageSender policyMessageSender;

    @Test
    public void testCreatePolicyForAccount() throws DsmClientException {
        //arrange
        Policy policyToBeCreated = new Policy();
        Policy expectedPolicy = new Policy();
        expectedPolicy.setVendorPolicyId(TEST_ID);
        String accountId = TEST_ACCOUNT;

        PolicyBean policyBean = new PolicyBean(accountId, policyToBeCreated);

        //act
        classUnderTest.createPolicyForAccount(TEST_ACCOUNT, policyToBeCreated);

        //assert
        verify(policyMessageSender).createPolicyForAccount(eq(policyBean));
    }

    @Test
    public void testGetPoliciesForAccount() {
        //arrange
        List<Policy> hopeful = new ArrayList<Policy>();
        Policy expected = buildPolicy();
        hopeful.add(expected);
        when(policyServiceRead.getPoliciesForAccount(TEST_ACCOUNT)).thenReturn(hopeful);

        //act
        List<Policy> policies = classUnderTest.getPoliciesForAccount(TEST_ACCOUNT);

        //assert
        for (Policy actual : policies) {
            assertEquals(expected, actual);
        }
        verify(policyServiceRead).getPoliciesForAccount(TEST_ACCOUNT);
    }

    @Test
    public void testGetPolicyForAccount() {
        //arrange
        Policy expected = buildPolicy();
        when(policyServiceRead.getPolicyForAccount(TEST_ACCOUNT, TEST_ID)).thenReturn(expected);

        //act
        Policy actual = classUnderTest.getPolicyForAccount(TEST_ACCOUNT, TEST_ID);

        //assert
        assertEquals(expected, actual);
        verify(policyServiceRead).getPolicyForAccount(TEST_ACCOUNT, TEST_ID);
    }


    @Test
    public void testUpdatePolicyForAccount() {
        //act
        classUnderTest.updatePolicyForAccount(TEST_ACCOUNT, TEST_ID, new Policy());

        //assert
        verify(policyServiceRead).updatePolicyForAccount(eq(TEST_ACCOUNT), eq(TEST_ID), any(Policy.class));
    }

    @Test
    public void testDeletePolicyForAccount() throws DsmClientException {
        //act
        String username = null;
        String serverDomainName = null;
        String accountId = TEST_ACCOUNT;
        Policy policy = new Policy();
        String vendorPolicyId = TEST_ID;
        policy.setVendorPolicyId(vendorPolicyId);
        PolicyBean policyBean = new PolicyBean(accountId, policy);

        classUnderTest.deletePolicyForAccount(TEST_ACCOUNT, TEST_ID, username, serverDomainName);

        //assert
        verify(policyMessageSender).deletePolicyForAccount(policyBean);
    }

    private Policy buildPolicy() {
        Policy policy = new Policy();
        policy.setVendorPolicyId(TEST_ID);
        policy.setStatus(PolicyStatus.ACTIVE);
        return policy;
    }

}
