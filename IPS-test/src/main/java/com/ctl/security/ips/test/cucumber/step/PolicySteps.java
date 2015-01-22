package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.clc.client.common.domain.ClcAuthenticationRequest;
import com.ctl.security.clc.client.common.domain.ClcAuthenticationResponse;
import com.ctl.security.clc.client.core.bean.AuthenticationClient;
import com.ctl.security.ips.client.bean.PolicyClient;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.common.exception.NotAuthorizedException;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import com.ctl.security.ips.test.cucumber.config.CucumberConfiguration;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = CucumberConfiguration.class)
@ActiveProfiles("dev")
public class PolicySteps {

    private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
    private static final String VALID_POLICY_ID = "test-id";
    private static final String VALID = "valid";
    private static final String VALID_AA = "TCCD";
    private static final String INVALID_AA = "TCCX";
    private static final String INVALID_POLICY_ID = "test-id-2";

    private static final String VALID_USERNAME = "kweber.tccd";
    private static final String VALID_PASSWORD = "1qaz@WSX";
    private static final String INVALID_TOKEN = "Bearer SomeinvalidToken";

    private Exception exception;
    private List<Policy> policyList;
    private Policy policy;
    private String policyId;

    private String aa;
    private String bearerToken;

    @Autowired
    private AuthenticationClient authenticationClient;

    @Autowired
    private PolicyClient policyClient;

    @When("^I GET the policies$")
    public void i_GET_the_policies() {
        try {
            policyList = policyClient.getPoliciesForAccount(aa, bearerToken);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("^I receive a response that contains the expected list of policies$")
    public void i_receive_a_response_that_contains_the_expected_list_of_policies() {
        Policy expected = buildPolicy();
        assertNotNull(policyList);
        assertTrue(!policyList.isEmpty());
        for (Policy actual : policyList) {
            assertEquals(expected, actual);
        }
    }

    @Given("^I have an? (.*) account$")
    public void I_have_validity_account(String validity) {
        if (VALID.equalsIgnoreCase(validity)) {
            aa = VALID_AA;
            ClcAuthenticationResponse clcAuthenticationResponse = authenticationClient.authenticateV2Api(new ClcAuthenticationRequest(VALID_USERNAME, VALID_PASSWORD));
            bearerToken = clcAuthenticationResponse.getBearerToken();
        }
        else {
            aa = INVALID_AA;
            bearerToken = INVALID_TOKEN;
        }
    }

    @When("^I (GET|PUT|DELETE) an? (valid|invalid) policy$")
    public void I_METHOD_validity_policy(String method, String validity) {
        String id;
        if (VALID.equalsIgnoreCase(validity)) {
            id = VALID_POLICY_ID;
        }
        else {
            id = INVALID_POLICY_ID;
        }
        try {
            if ("GET".equals(method)) {
                policy = policyClient.getPolicyForAccount(aa, id, bearerToken);
            }
            else if ("PUT".equals(method)) {
                policyClient.updatePolicyForAccount(aa, id, new Policy(), bearerToken);
            }
            else {
                policyClient.deletePolicyForAccount(aa, id, bearerToken);
            }
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("^I receive a response that contains the expected policy$")
    public void I_receive_a_response_that_contains_the_expected_policy() {
        Policy expected = buildPolicy();
        assertEquals(expected, policy);
    }

    @When("^I POST a policy$")
    public void I_POST_a_policy() {
        try {
            policyId = policyClient.createPolicyForAccount(aa, new Policy(), bearerToken);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("^I receive a response that contains a uuid for the created policy$")
    public void I_receive_a_response_that_contains_a_uuid_for_the_created_policy() {
        assertTrue(policyId.matches(UUID_REGEX));
    }

    @Then("^I receive a response that does not contain an error message$")
    public void I_receive_a_response_that_does_not_contain_an_error_message() {
    }

    private Policy buildPolicy() {
        return new Policy().setId(VALID_POLICY_ID).setStatus(PolicyStatus.ACTIVE);
    }

    @Then("^I receive a response with error message (.*)$")
    public void I_receive_a_response_with_error_message(String message) throws Throwable {
        if (exception instanceof PolicyNotFoundException) {
            assertNotNull(message, exception.getMessage());
        } else if (exception instanceof NotAuthorizedException) {
            assertNotNull(message, exception.getMessage());
        } else {
            fail();
        }
    }
}

