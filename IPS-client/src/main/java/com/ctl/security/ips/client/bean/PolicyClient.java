package com.ctl.security.ips.client.bean;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.exception.NotAuthorizedException;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import com.ctl.security.library.common.http.CtlSecurityClient;
import com.ctl.security.library.common.http.CtlSecurityRequest;
import com.ctl.security.library.common.http.CtlSecurityResponse;
import com.ctl.security.library.common.http.CtlSecurityResponseHandler;

/**
 * Created by Kevin.Weber on 10/29/2014.
 */
@Service
public class PolicyClient {

    private static final String POLICIES = "policies/";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    
    @Value("${${ENV_SERVER:local}.ips.host}")
    private String hostUrl;

    @Autowired
    private CtlSecurityClient clientBuilder;
    
    @Autowired
    private CtlSecurityResponseHandler ctlSecurityResponseHandler;

    public List<Policy> getPoliciesForAccount(String account, String token) {
        CtlSecurityResponse ctlSecurityResponse = clientBuilder.get(hostUrl + POLICIES + account)
                .addHeader(AUTHORIZATION, BEARER + token)
                .execute();

        checkForFailure(ctlSecurityResponse);

        return Arrays.asList(ctlSecurityResponseHandler.getResponseObject(ctlSecurityResponse, Policy[].class));
    }

    public Policy getPolicyForAccount(String account, String id, String token) {
        CtlSecurityResponse ctlSecurityResponse = clientBuilder.get(hostUrl + POLICIES + account + "/" + id)
                .addHeader(AUTHORIZATION, BEARER + token)
                .execute();

        checkForFailure(ctlSecurityResponse);

        return ctlSecurityResponseHandler.getResponseObject(ctlSecurityResponse, Policy.class);
    }

    public String createPolicyForAccount(String account, Policy policy, String token) {
        CtlSecurityRequest request = clientBuilder.post(hostUrl + POLICIES + account)
                .addHeader(AUTHORIZATION, BEARER + token)
                .body(policy);
        CtlSecurityResponse ctlSecurityResponse = request.execute();

        checkForFailure(ctlSecurityResponse);

        return ctlSecurityResponse.getResponseContent();
    }

    public void updatePolicyForAccount(String account, String id, Policy policy, String token) {
        CtlSecurityResponse ctlSecurityResponse = clientBuilder.put(hostUrl + POLICIES + account + "/" + id)
                .addHeader(AUTHORIZATION, BEARER + token)
                .body(policy)
                .execute();

        checkForFailure(ctlSecurityResponse);
    }

    public void deletePolicyForAccount(String account, String id, String token) {
        CtlSecurityResponse response = clientBuilder.delete(hostUrl + POLICIES + account + "/" + id)
                .addHeader(AUTHORIZATION, BEARER + token)
                .execute();

        checkForFailure(response);
    }

    private void checkForFailure(CtlSecurityResponse response) {
        if (response.getStatusCode() >= Status.FORBIDDEN.getStatusCode()) {
            throw new NotAuthorizedException(response.getResponseContent());
        } else if (response.getStatusCode() >= Status.BAD_REQUEST.getStatusCode()) {
            throw new PolicyNotFoundException(response.getResponseContent());
        }
    }

}
