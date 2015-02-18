package com.ctl.security.ips.common.service;

import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PolicyServiceReadTest {

    private static final String VALID_ACCOUNT = "TCCD";
    private static final String TEST_ID = "test-vendorPolicyId";
    private static final String INVALID_ACCOUNT = "TCCX";

    @InjectMocks
    private PolicyServiceRead classUnderTest;

    @Test
    public void testGetPoliciesForAccount() {
        //act
        List<Policy> policies = classUnderTest.getPoliciesForAccount(VALID_ACCOUNT);

        //assert
        com.ctl.security.ips.common.domain.Policy expected = buildPolicy();
        for (com.ctl.security.ips.common.domain.Policy actual : policies) {
            assertEquals(expected, actual);
        }
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testGetPoliciesForAccountPolicyNotFoundException() {
        //act
        List<com.ctl.security.ips.common.domain.Policy> policies = classUnderTest.getPoliciesForAccount(INVALID_ACCOUNT);
    }


    @Test
    public void testGetPolicyForAccount() {
        //act
        com.ctl.security.ips.common.domain.Policy actual = classUnderTest.getPolicyForAccount(VALID_ACCOUNT, TEST_ID);

        //assert
        com.ctl.security.ips.common.domain.Policy expected = buildPolicy();
        assertEquals(expected, actual);
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testGetPolicyForAccountPolicyNotFoundException() {
        //act
        classUnderTest.getPolicyForAccount(INVALID_ACCOUNT, TEST_ID);
    }

    private com.ctl.security.ips.common.domain.Policy buildPolicy() {
        return new com.ctl.security.ips.common.domain.Policy().setVendorPolicyId(TEST_ID).setStatus(PolicyStatus.ACTIVE);
    }

}