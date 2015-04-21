package com.ctl.security.ips.client;

import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.common.exception.NotAuthorizedException;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Response.Status;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Kevin.Weber on 10/29/2014.
 *
 */
@Service
public class PolicyClient {

    private static final Logger logger = LogManager.getLogger();

    public static final String POLICIES = "policies/";
    public static final String USERNAME = "username";

    @Value("${${spring.profiles.active:local}.ips.host}")
    private String hostUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ClientComponent clientComponent;

    public void createPolicyForAccount(String accountId, Policy policy, String bearerToken) {
        try {
            String address = hostUrl + POLICIES + accountId;
            logger.info("createPolicyForAccount: " + address);

            ResponseEntity<String> responseEntity = restTemplate.exchange(address,
                    HttpMethod.POST, new HttpEntity<>(policy, clientComponent.createHeaders(bearerToken)), String.class);

            logger.info("Create Policy Status Code: " + responseEntity.getStatusCode().value());
        } catch (RestClientException rce) {
            fail(rce);
        }
    }

    public List<Policy> getPoliciesForAccount(String account, String bearerToken) {
        List<Policy> response = null;
        try {
            String address = hostUrl + POLICIES + account;
            logger.info("getPoliciesForAccount: " + address);

            response = Arrays.asList(restTemplate.exchange(address,
                    HttpMethod.GET, new HttpEntity<>(clientComponent.createHeaders(bearerToken)), Policy[].class).getBody());
        } catch (RestClientException rce) {
            fail(rce);
        }
        return response;
    }

    public Policy getPolicyForAccount(String account, String id, String bearerToken) {
        Policy response = null;
        try {
            String address = hostUrl + POLICIES + account + "/" + id;
            logger.info("getPolicyForAccount: " + address);

            response = restTemplate.exchange(address,
                    HttpMethod.GET, new HttpEntity<>(clientComponent.createHeaders(bearerToken)), Policy.class).getBody();
        } catch (RestClientException rce) {
            fail(rce);
        }
        return response;
    }

    public void updatePolicyForAccount(String account, String id, Policy policy, String bearerToken) {
        try {
            String address = hostUrl + POLICIES + account;
            logger.info("updatePolicyForAccount: " + address);

            restTemplate.exchange(address,
                    HttpMethod.PUT, new HttpEntity<>(policy, clientComponent.createHeaders(bearerToken)), String.class);
        } catch (RestClientException rce) {
            fail(rce);
        }
    }

    public void deletePolicyForAccount(String account, String id, String username, String serverDomainName, String bearerToken) {
        String address = hostUrl + POLICIES + account + "/" + id + "/" + serverDomainName + "?" + USERNAME + "=" + username;
        logger.info("deletePolicyForAccount: " + address);

        try {
            HttpHeaders httpHeaders = clientComponent.createHeaders(bearerToken);
            restTemplate.exchange(address,
                    HttpMethod.DELETE, new HttpEntity<>(httpHeaders), String.class);
        } catch (RestClientException rce) {
            fail(rce);
        }
    }


    private void fail(RestClientException rce) {
        logger.error(rce.getMessage(), rce);
        String message = rce.getMessage();
        int statusCode = Integer.parseInt(message.split("[\\s]")[0]);
        if (statusCode >= Status.FORBIDDEN.getStatusCode()) {
            throw new NotAuthorizedException(message);
        } else {
            throw new PolicyNotFoundException(message);
        }
    }

}
