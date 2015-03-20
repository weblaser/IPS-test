package com.ctl.security.ips.client;

import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.common.domain.Policy.PolicyStatus;
import com.ctl.security.ips.common.exception.NotAuthorizedException;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Kevin.Weber on 10/29/2014.
 */
@RunWith(MockitoJUnitRunner.class)
public class PolicyClientTest {

    private static final String VALID_ACCOUNT = "TCCD";
    private static final String TEST_ID = "12345";
    private static final String INVALID_ACCOUNT = "TCCX";
    private static final String SAMPLE_TOKEN = "Bearer sampletoken";


    @InjectMocks
    private PolicyClient classUnderTest = new PolicyClient();

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ResponseEntity<Policy[]> listEntity;

    @Mock
    private ResponseEntity<Policy> entity;

    @Mock
    private ResponseEntity<String> stringEntity;

    @Mock
    private ClientComponent clientComponent;

    private HttpHeaders httpHeaders = new HttpHeaders();

    @Before
    public void setup(){
        String bearerToken = SAMPLE_TOKEN;
        httpHeaders.add("test", "test");
        when(clientComponent.createHeaders(bearerToken)).thenReturn(httpHeaders);
    }

    @Test
    public void testCreatePolicyForAccount() {
        //arrange
        Policy expectedPolicy = buildPolicy();
        Policy policyToCreate = new Policy().setVendorPolicyId(TEST_ID).setStatus(PolicyStatus.ACTIVE);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Policy.class))).thenReturn(entity);
        when(entity.getBody()).thenReturn(expectedPolicy);

        //act
        classUnderTest.createPolicyForAccount(VALID_ACCOUNT, policyToCreate, SAMPLE_TOKEN);

        //assert
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test(expected = NotAuthorizedException.class)
    public void testCreatePolicyForAccountNotAuthorizedException() {
        //arrange
        Policy policy = new Policy();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(String.class))).thenThrow(new RestClientException("403 Forbidden."));

        //act
        classUnderTest.createPolicyForAccount(INVALID_ACCOUNT, policy, SAMPLE_TOKEN);
    }
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
        String hostUrl = "hostUrl/";
        ReflectionTestUtils.setField(classUnderTest, "hostUrl", hostUrl);
        Policy policy = buildPolicy();
        HttpEntity policyHttpEntity = new HttpEntity<>(httpHeaders);

        //act
        String username = null;
        String serverDomainName = null;
        classUnderTest.deletePolicyForAccount(VALID_ACCOUNT, TEST_ID, username, serverDomainName, SAMPLE_TOKEN);


        verify(restTemplate).exchange(eq(hostUrl + PolicyClient.POLICIES + VALID_ACCOUNT + "/" + TEST_ID + "/" + serverDomainName + "?" +
                PolicyClient.USERNAME + "=" + username), eq(HttpMethod.DELETE), eq(policyHttpEntity), eq(String.class));
    }

    @Test(expected = NotAuthorizedException.class)
    public void testDeletePolicyForAccountNotAuthorizedException() {
        //arrange
        Policy policy = buildPolicy();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE),
                any(HttpEntity.class), eq(String.class))).thenThrow(new RestClientException("403 Forbidden."));

        //act
        String username = null;
        String serverDomainName = null;
        classUnderTest.deletePolicyForAccount(INVALID_ACCOUNT, TEST_ID, username, serverDomainName, SAMPLE_TOKEN);
    }

    @Test(expected = PolicyNotFoundException.class)
    public void testDeletePolicyForAccountPolicyNotFoundException() {
        //arrange
        Policy policy = buildPolicy();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE),
                any(HttpEntity.class), eq(String.class))).thenThrow(new RestClientException("400 Bad Request."));

        //act
        String username = null;
        String serverDomainName = null;
        classUnderTest.deletePolicyForAccount(INVALID_ACCOUNT, TEST_ID, username, serverDomainName, SAMPLE_TOKEN);
    }

    private List<Policy> buildPolicyList() {
        List<Policy> list = new ArrayList<Policy>();
        Policy policy = buildPolicy();
        list.add(policy);
        return list;
    }

    private Policy buildPolicy() {
        return new Policy().setVendorPolicyId(TEST_ID).setStatus(PolicyStatus.ACTIVE);
    }


}
