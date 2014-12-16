package com.ctl.security.ips.api.resource.impl;

import com.ctl.security.ips.api.resource.PolicyResource;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.crud.service.PolicyService;
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
    private static final String TEST_ID = "test-id";

    @InjectMocks
    PolicyResource classUnderTest = new PolicyResourceImpl();

    @Mock
    private PolicyService policyService;

    @Test
    public void testGetPoliciesForAccount() {
        //arrange
        List<Policy> hopeful = new ArrayList<Policy>();
        Policy expected = buildPolicy();
        hopeful.add(expected);
        when(policyService.getPoliciesForAccount(TEST_ACCOUNT)).thenReturn(hopeful);

        //act
        List<Policy> policies = classUnderTest.getPoliciesForAccount(TEST_ACCOUNT);

        //assert
        for (Policy actual : policies) {
            assertEquals(expected, actual);
        }
        verify(policyService).getPoliciesForAccount(TEST_ACCOUNT);
    }

    @Test
    public void testGetPolicyForAccount() {
        //arrange
        Policy expected = buildPolicy();
        when(policyService.getPolicyForAccount(TEST_ACCOUNT, TEST_ID)).thenReturn(expected);

        //act
        Policy actual = classUnderTest.getPolicyForAccount(TEST_ACCOUNT, TEST_ID);

        //assert
        assertEquals(expected, actual);
        verify(policyService).getPolicyForAccount(TEST_ACCOUNT, TEST_ID);
    }

    @Test
    public void testCreatePolicyForAccount() {
        //arrange
        when(policyService.createPolicyForAccount(eq(TEST_ACCOUNT), any(Policy.class))).thenReturn(TEST_ID);

        //act
        String actual = classUnderTest.createPolicyForAccount(TEST_ACCOUNT, new Policy());

        //assert
        assertEquals(TEST_ID, actual);
        verify(policyService).createPolicyForAccount(eq(TEST_ACCOUNT), any(Policy.class));
    }

    @Test
    public void testUpdatePolicyForAccount() {
        //act
        classUnderTest.updatePolicyForAccount(TEST_ACCOUNT, TEST_ID, new Policy());

        //assert
        verify(policyService).updatePolicyForAccount(eq(TEST_ACCOUNT), eq(TEST_ID), any(Policy.class));
    }

    @Test
    public void testDeletePolicyForAccount(){
        //act
        classUnderTest.deletePolicyForAccount(TEST_ACCOUNT, TEST_ID);

        //assert
        verify(policyService).deletePolicyForAccount(eq(TEST_ACCOUNT), eq(TEST_ID));
    }

    private Policy buildPolicy() {
        Policy policy = new Policy();
        policy.setId(TEST_ID);
        policy.setStatus(PolicyStatus.ACTIVE);
        return policy;
    }

}
