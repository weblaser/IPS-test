package com.ctl.security.ips.service;

import com.ctl.security.data.client.service.CmdbService;
import com.ctl.security.data.common.domain.mongo.Product;
import com.ctl.security.data.common.domain.mongo.ProductType;
import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.common.domain.Policy.PolicyStatus;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class PolicyServiceReadTest {

    private static final String VALID_ACCOUNT = "TCCD";
    private static final String TEST_ID = "12345";
    private static final String USERNAME = "username";
    private static final String SERVER_DOMAIN_NAME = "testServer";

    @InjectMocks
    private PolicyServiceRead classUnderTest;

    @Mock
    private CmdbService cmdbService;

    @Mock
    private DsmPolicyClient dsmPolicyClient;

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


    @Test
    public void testUpdatePolicyForAccount() {
        //act
        classUnderTest.updatePolicyForAccount(VALID_ACCOUNT, TEST_ID, new Policy());
    }




    private Policy buildPolicy() {
        return new Policy().setVendorPolicyId(TEST_ID).setStatus(PolicyStatus.ACTIVE);
    }
    private Product buildProduct() {
        return new Product().
                setName(PolicyServiceRead.TREND_MICRO_IPS).
                setType(ProductType.IPS);
    }
}