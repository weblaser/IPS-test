package com.ctl.security.ips.client.bean;

import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.common.exception.NotAuthorizedException;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by Kevin.Weber on 10/29/2014.
 */
@RunWith(MockitoJUnitRunner.class)
public class PolicyClientTest {

    private static final String VALID_ACCOUNT = "TCCD";
    private static final String TEST_ID = "test-id";
    private static final String INVALID_ACCOUNT = "TCCX";
    private static final String SAMPLE_TOKEN = "Bearer sampletoken";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ResponseEntity<Policy[]> listEntity;

    @Mock
    private ResponseEntity<Policy> entity;

    @Mock
    private ResponseEntity<String> stringEntity;

    @InjectMocks
    private PolicyClient classUnderTest = new PolicyClient();

    @Test
    public void testGetPoliciesForAccount() {
        //arrange
        List<Policy> policyList = buildPolicyList();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Policy[].class))).thenReturn(listEntity);
        when(listEntity.getBody()).thenReturn(policyList.toArray(new Policy[policyList.size()]));

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
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(Policy[].class))).thenThrow(new RestClientException("400 Bad Request."));

        //act
        classUnderTest.getPoliciesForAccount(INVALID_ACCOUNT, SAMPLE_TOKEN);
    }

    @Test
    public void testGetPolicyForAccount() {
        //arrange
        Policy expected = buildPolicy();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Policy.class))).thenReturn(entity);
        when(entity.getBody()).thenReturn(expected);

        //act
        Policy actual = classUnderTest.getPolicyForAccount(VALID_ACCOUNT, TEST_ID, SAMPLE_TOKEN);

        //assert
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testGetPolicyForAccountPolicyNotFoundException() {
        //arrange
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(Policy.class))).thenThrow(new RestClientException("400 Bad Request."));

        //act
        classUnderTest.getPolicyForAccount(INVALID_ACCOUNT, TEST_ID, SAMPLE_TOKEN);
    }

    @Test
    public void testCreatePolicyForAccount() {
        //arrange
        Policy expectedPolicy = buildPolicy();
        Policy policyToCreate = new Policy().setId(TEST_ID).setStatus(PolicyStatus.ACTIVE);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Policy.class))).thenReturn(entity);
        when(entity.getBody()).thenReturn(expectedPolicy);

        //act
        Policy actualPolicy = classUnderTest.createPolicyForAccount(VALID_ACCOUNT, policyToCreate, SAMPLE_TOKEN);

        //assert
        assertEquals(expectedPolicy, actualPolicy);
        assertEquals(TEST_ID, actualPolicy.getId());
    }

    @Test(expected = NotAuthorizedException.class)
    public void testCreatePolicyForAccountNotAuthorizedException() {
        //arrange
        Policy policy = new Policy();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Policy.class))).thenThrow(new RestClientException("403 Forbidden."));

        //act
        classUnderTest.createPolicyForAccount(INVALID_ACCOUNT, policy, SAMPLE_TOKEN);
    }

    @Test
    public void testUpdatePolicyForAccount() {
        //arrange
        Policy policy = new Policy();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class))).thenReturn(stringEntity);

        //act
        classUnderTest.updatePolicyForAccount(VALID_ACCOUNT, TEST_ID, policy, SAMPLE_TOKEN);
    }

    @Test(expected = NotAuthorizedException.class)
    public void testUpdatePolicyForAccountNotAuthorizedException() {
        //arrange
        Policy policy = new Policy();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT),
                any(HttpEntity.class), eq(String.class))).thenThrow(new RestClientException("403 Forbidden."));

        //act
        classUnderTest.updatePolicyForAccount(INVALID_ACCOUNT, TEST_ID, policy, SAMPLE_TOKEN);
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testUpdatePolicyForAccountPolicyNotFoundException() {
        //arrange
        Policy policy = new Policy();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT),
                any(HttpEntity.class), eq(String.class))).thenThrow(new RestClientException("400 Bad Request."));

        //act
        classUnderTest.updatePolicyForAccount(INVALID_ACCOUNT, TEST_ID, policy, SAMPLE_TOKEN);
    }


    @Test
    public void testDeletePolicyForAccount() {
        //arrange
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class))).thenReturn(stringEntity);

        //act
        classUnderTest.deletePolicyForAccount(VALID_ACCOUNT, TEST_ID, SAMPLE_TOKEN);
    }

    @Test(expected = NotAuthorizedException.class)
    public void testDeletePolicyForAccountNotAuthorizedException() {
        //arrange
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE),
                any(HttpEntity.class), eq(String.class))).thenThrow(new RestClientException("403 Forbidden."));

        //act
        classUnderTest.deletePolicyForAccount(INVALID_ACCOUNT, TEST_ID, SAMPLE_TOKEN);
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testDeletePolicyForAccountPolicyNotFoundException() {
        //arrange
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE),
                any(HttpEntity.class), eq(String.class))).thenThrow(new RestClientException("400 Bad Request."));

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
