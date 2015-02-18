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

    @InjectMocks
    private PolicyServiceRead classUnderTest;

    @Test
    public void testGetPoliciesForAccount() {
        //act
        List<Policy> policies = classUnderTest.getPoliciesForAccount(VALID_ACCOUNT);

        //assert
        Policy expected = buildPolicy();
        for (Policy actual : policies) {
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testGetPolicyForAccount() {
        //act
        Policy actual = classUnderTest.getPolicyForAccount(VALID_ACCOUNT, TEST_ID);

        //assert
        Policy expected = buildPolicy();
        assertEquals(expected, actual);
    }


    private com.ctl.security.ips.common.domain.Policy buildPolicy() {
        return new com.ctl.security.ips.common.domain.Policy().setVendorPolicyId(TEST_ID).setStatus(PolicyStatus.ACTIVE);
    }

}