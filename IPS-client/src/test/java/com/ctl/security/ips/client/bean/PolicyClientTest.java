package com.ctl.security.ips.client.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.common.exception.NotAuthorizedException;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import com.ctl.security.library.common.http.ClientTest;

/**
 * Created by Kevin.Weber on 10/29/2014.
 */
@RunWith(MockitoJUnitRunner.class)
public class PolicyClientTest extends ClientTest {

    private static final String VALID_ACCOUNT = "TCCD";
    private static final String TEST_ID = "test-id";
    private static final String INVALID_ACCOUNT = "TCCX";
    private static final String SAMPLE_TOKEN = "Bearer sampletoken";

    @InjectMocks
    private PolicyClient classUnderTest = new PolicyClient();

    @Test
    public void testGetPoliciesForAccount() {
        //arrange
        List<Policy> policyList = buildPolicyList();
        when(ctlSecurityResponseHandler.getResponseObject(ctlSecurityResponse, Policy[].class)).thenReturn(policyList.toArray(new Policy[policyList.size()]));

        //act
        List<Policy> policies = classUnderTest.getPoliciesForAccount(VALID_ACCOUNT, SAMPLE_TOKEN);

        //assert
        Policy expected = policyList.get(0);
        assertNotNull(policies);
        assertTrue(!policies.isEmpty());
        for (Policy actual : policies) {
            assertEquals(expected, actual);
        }
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testGetPoliciesForAccountPolicyNotFoundException() {
        //arrange
        when(ctlSecurityResponse.getStatusCode()).thenReturn(Response.Status.BAD_REQUEST.getStatusCode());

        //act
        classUnderTest.getPoliciesForAccount(INVALID_ACCOUNT, SAMPLE_TOKEN);
    }

    @Test
    public void testGetPolicyForAccount() {
        //arrange
        Policy expected = buildPolicy();
        when(ctlSecurityResponseHandler.getResponseObject(ctlSecurityResponse, Policy.class)).thenReturn(expected);

        //act
        Policy actual = classUnderTest.getPolicyForAccount(VALID_ACCOUNT, TEST_ID, SAMPLE_TOKEN);

        //assert
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testGetPolicyForAccountPolicyNotFoundException() {
        //arrange
        when(ctlSecurityResponse.getStatusCode()).thenReturn(Response.Status.BAD_REQUEST.getStatusCode());

        //act
        classUnderTest.getPolicyForAccount(INVALID_ACCOUNT, TEST_ID, SAMPLE_TOKEN);
    }

    @Test
    public void testCreatePolicyForAccount() {
        //arrange
        String expectedId = UUID.randomUUID().toString();
        Policy inserted = new Policy();
        inserted.setId(expectedId);
        inserted.setStatus(PolicyStatus.ACTIVE);
        when(ctlSecurityResponse.getStatusCode()).thenReturn(Response.Status.OK.getStatusCode());
        when(ctlSecurityResponse.getResponseContent()).thenReturn(expectedId);
        when(ctlSecurityRequest.body(inserted)).thenReturn(ctlSecurityRequest);

        //act
        String actualId = classUnderTest.createPolicyForAccount(VALID_ACCOUNT, inserted, SAMPLE_TOKEN);

        //assert
        assertEquals(expectedId, actualId);
    }

    @Test(expected = NotAuthorizedException.class)
    public void testCreatePolicyForAccountNotAuthorizedException() {
        //arrange
        Policy policy = new Policy();
        when(ctlSecurityResponse.getStatusCode()).thenReturn(Response.Status.FORBIDDEN.getStatusCode());
        when(ctlSecurityRequest.body(policy)).thenReturn(ctlSecurityRequest);

        //act
        classUnderTest.createPolicyForAccount(INVALID_ACCOUNT, policy, SAMPLE_TOKEN);
    }

    @Test
    public void testUpdatePolicyForAccount() {
        //arrange
        Policy policy = new Policy();
        when(ctlSecurityResponse.getStatusCode()).thenReturn(Response.Status.OK.getStatusCode());
        when(ctlSecurityRequest.body(policy)).thenReturn(ctlSecurityRequest);

        //act
        classUnderTest.updatePolicyForAccount(VALID_ACCOUNT, TEST_ID, policy, SAMPLE_TOKEN);
    }

    @Test(expected = NotAuthorizedException.class)
    public void testUpdatePolicyForAccountNotAuthorizedException() {
        //arrange
        Policy policy = new Policy();
        when(ctlSecurityResponse.getStatusCode()).thenReturn(Response.Status.FORBIDDEN.getStatusCode());
        when(ctlSecurityRequest.body(policy)).thenReturn(ctlSecurityRequest);

        //act
        classUnderTest.updatePolicyForAccount(INVALID_ACCOUNT, TEST_ID, policy, SAMPLE_TOKEN);
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testUpdatePolicyForAccountPolicyNotFoundException() {
        //arrange
        Policy policy = new Policy();
        when(ctlSecurityResponse.getStatusCode()).thenReturn(Response.Status.BAD_REQUEST.getStatusCode());
        when(ctlSecurityRequest.body(policy)).thenReturn(ctlSecurityRequest);

        //act
        classUnderTest.updatePolicyForAccount(INVALID_ACCOUNT, TEST_ID, policy, SAMPLE_TOKEN);
    }


    @Test
    public void testDeletePolicyForAccount() {
        //arrange

        //act
        classUnderTest.deletePolicyForAccount(VALID_ACCOUNT, TEST_ID, SAMPLE_TOKEN);
    }

    @Test(expected = NotAuthorizedException.class)
    public void testDeletePolicyForAccountNotAuthorizedException() {
        //arrange
        when(ctlSecurityResponse.getStatusCode()).thenReturn(Response.Status.FORBIDDEN.getStatusCode());

        //act
        classUnderTest.deletePolicyForAccount(INVALID_ACCOUNT, TEST_ID, SAMPLE_TOKEN);
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testDeletePolicyForAccountPolicyNotFoundException() {
        //arrange
        when(ctlSecurityResponse.getStatusCode()).thenReturn(Response.Status.BAD_REQUEST.getStatusCode());

        //act
        classUnderTest.deletePolicyForAccount(INVALID_ACCOUNT, TEST_ID, SAMPLE_TOKEN);
    }

    private List<Policy> buildPolicyList() {
        List<Policy> list = new ArrayList<Policy>();
        Policy policy = buildPolicy();
        list.add(policy);
        return list;
    }

    private Policy buildPolicy() {
        return new Policy().setId(TEST_ID).setStatus(PolicyStatus.ACTIVE);
    }
}
