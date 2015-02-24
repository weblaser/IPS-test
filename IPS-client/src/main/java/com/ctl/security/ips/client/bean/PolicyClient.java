package com.ctl.security.ips.client.bean;

import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.exception.NotAuthorizedException;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Response.Status;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kevin.Weber on 10/29/2014.
 */
@Service
public class PolicyClient {

    public static final String POLICIES = "policies/";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String USERNAME = "username";

    @Value("${${spring.profiles.active:local}.ips.host}")
    private String hostUrl;

    @Autowired
    private RestTemplate restTemplate;

    public void createPolicyForAccount(String account, Policy policy, String token) {
        try {
            restTemplate.exchange(hostUrl + POLICIES + account,
                    HttpMethod.POST, new HttpEntity<>(policy, createHeaders(token)), String.class);
        } catch (RestClientException rce) {
            fail(rce);
        }
    }

    public List<Policy> getPoliciesForAccount(String account, String token) {
        List<Policy> response = null;
        try {
            response = Arrays.asList(restTemplate.exchange(hostUrl + POLICIES + account,
                    HttpMethod.GET, new HttpEntity<>(createHeaders(token)), Policy[].class).getBody());
        } catch (RestClientException rce) {
            fail(rce);
        }
        return response;
    }

    public Policy getPolicyForAccount(String account, String id, String token) {
        Policy response = null;
        try {
            response = restTemplate.exchange(hostUrl + POLICIES + account + "/" + id,
                    HttpMethod.GET, new HttpEntity<>(createHeaders(token)), Policy.class).getBody();
        } catch (RestClientException rce) {
            fail(rce);
        }
        return response;
    }

    public void updatePolicyForAccount(String account, String id, Policy policy, String token) {
        try {
            restTemplate.exchange(hostUrl + POLICIES + account,
                    HttpMethod.PUT, new HttpEntity<>(policy, createHeaders(token)), String.class);
        } catch (RestClientException rce) {
            fail(rce);
        }
    }

    public void deletePolicyForAccount(String account, String id, String username, String serverDomainName, String token) {
        String address = hostUrl + POLICIES + account + "/" + id + "/" + serverDomainName + "?" + USERNAME + "=" + username;
        try {
            restTemplate.exchange(address,
                    HttpMethod.DELETE, new HttpEntity<>(createHeaders(token)), String.class);
        } catch (RestClientException rce) {
            fail(rce);
        }
    }

    private HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, BEARER + token);
        return headers;
    }

    private void fail(RestClientException rce) {
        String message = rce.getMessage();
        int statusCode = Integer.parseInt(message.split("[\\s]")[0]);
        if (statusCode >= Status.FORBIDDEN.getStatusCode()) {
            throw new NotAuthorizedException(message);
        } else {
            throw new PolicyNotFoundException(message);
        }
    }

}
