package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.clc.client.common.domain.ClcAuthenticationRequest;
import com.ctl.security.clc.client.common.domain.ClcAuthenticationResponse;
import com.ctl.security.clc.client.core.bean.AuthenticationClient;
import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.cmdb.ProductUserActivityClient;
import com.ctl.security.data.client.cmdb.UserClient;
import com.ctl.security.data.client.domain.configurationitem.ConfigurationItemResource;
import com.ctl.security.data.client.domain.productuseractivity.ProductUserActivityResource;
import com.ctl.security.data.client.domain.productuseractivity.ProductUserActivityResources;
import com.ctl.security.data.client.domain.user.UserResource;
import com.ctl.security.data.common.domain.mongo.ProductUserActivity;
import com.ctl.security.ips.client.bean.PolicyClient;
import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.common.domain.PolicyStatus;
import com.ctl.security.ips.common.exception.NotAuthorizedException;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import com.ctl.security.ips.test.cucumber.config.CucumberConfiguration;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import manager.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {CucumberConfiguration.class})
public class PolicySteps {

    private static final String VALID_POLICY_ID = "test-vendorPolicyId";
    private static final String VALID = "valid";
    private static final String VALID_AA = "TCCD";
    private static final String INVALID_AA = "TCCX";
    private static final String INVALID_POLICY_ID = "test-vendorPolicyId-2";

    private static final String VALID_USERNAME = "kweber.tccd";
    private static final String VALID_PASSWORD = "1qaz@WSX";
    private static final String INVALID_TOKEN = "Bearer SomeinvalidToken";

    private Exception exception;
    private List<Policy> policyList;
    private Policy policy;

    private String accountId;
    private String bearerToken;

    @Autowired
    private AuthenticationClient authenticationClient;

    @Autowired
    private PolicyClient policyClient;

    @Autowired
    private DsmClientComponent dsmClientComponent;

    @Autowired
    private DsmPolicyClient dsmPolicyClient;

    @Autowired
    private ConfigurationItemClient configurationItemClient;

    @Autowired
    private ProductUserActivityClient productUserActivityClient;

    @Autowired
    private UserClient userClient;

    @When("^I GET the policies$")
    public void i_GET_the_policies() {
        try {
            policyList = policyClient.getPoliciesForAccount(accountId, bearerToken);
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
    public void I_have_validity_account(String validity) throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthorizationException_Exception, ManagerTimeoutException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception {

        if (VALID.equalsIgnoreCase(validity)) {
            accountId = VALID_AA;
            ClcAuthenticationResponse clcAuthenticationResponse = authenticationClient.authenticateV2Api(new ClcAuthenticationRequest(VALID_USERNAME, VALID_PASSWORD));
            bearerToken = clcAuthenticationResponse.getBearerToken();
        } else {
            accountId = INVALID_AA;
            bearerToken = INVALID_TOKEN;
        }
    }

    @Given("^I (GET|PUT|DELETE) an? (valid|invalid) policy$")
    public void I_METHOD_validity_policy(String method, String validity) {
        String id;
        if (VALID.equalsIgnoreCase(validity)) {
            id = VALID_POLICY_ID;
        } else {
            id = INVALID_POLICY_ID;
        }
        try {
            if ("GET".equals(method)) {
                policy = policyClient.getPolicyForAccount(accountId, id, bearerToken);
            } else if ("PUT".equals(method)) {
                policyClient.updatePolicyForAccount(accountId, id, new Policy(), bearerToken);
            } else {
                policyClient.deletePolicyForAccount(accountId, id, bearerToken);
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
            policy = new Policy();
            String name = "name" + System.currentTimeMillis();
            String serverDomainName = "server.domain.name." + System.currentTimeMillis();
            String userName = "userName" + System.currentTimeMillis();
            policy.setName(name).
                    setServerDomainName(serverDomainName).
                    setUsername(userName);

            policyClient.createPolicyForAccount(accountId, policy, bearerToken);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("^I receive a response that contains a uuid for the created policy$")
    public void I_receive_a_response_that_contains_a_uuid_for_the_created_policy() throws DsmPolicyClientException {
        dsmClientComponent.verifyDsmPolicyCreation(dsmPolicyClient, policy);
        verifyCmdbCreation();
    }

    private void verifyCmdbCreation() {

        UserResource user = userClient.getUser(policy.getUsername(), accountId);
        assertNotNull(user.getContent());
        assertNotNull(user.getContent().getId());
        assertNotNull(user.getContent().getProductUserActivities());

        ProductUserActivityResources productUserActivityResources = userClient.getProductUserActivities(user);
        assertNotNull(productUserActivityResources);
        assertNotNull(productUserActivityResources.getContent());
        List<ProductUserActivity> productUserActivities = productUserActivityResources.unwrap();
        assertTrue(productUserActivities.size() > 0);

        ConfigurationItemResource configurationItemResource = configurationItemClient.getConfigurationItem(policy.getServerDomainName(), accountId);
        assertNotNull(configurationItemResource);
        assertNotNull(configurationItemResource.getContent().getId());

        userClient.deleteUser(user.getContent().getId());
        productUserActivities.stream().forEach(x -> productUserActivityClient.deleteProductUserActivity(x.getId()));
        configurationItemClient.deleteConfigurationItem(configurationItemResource.getContent().getId());
    }

    @Then("^I receive a response that does not contain an error message$")
    public void I_receive_a_response_that_does_not_contain_an_error_message() {
    }

    private Policy buildPolicy() {
        return new Policy().setVendorPolicyId(VALID_POLICY_ID).setStatus(PolicyStatus.ACTIVE);
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
