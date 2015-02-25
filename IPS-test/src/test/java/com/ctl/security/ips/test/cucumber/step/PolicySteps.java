package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.clc.client.common.domain.ClcAuthenticationRequest;
import com.ctl.security.clc.client.common.domain.ClcAuthenticationResponse;
import com.ctl.security.clc.client.core.bean.AuthenticationClient;
import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.cmdb.ProductUserActivityClient;
import com.ctl.security.data.client.cmdb.UserClient;
import com.ctl.security.data.client.domain.configurationitem.ConfigurationItemResource;
import com.ctl.security.data.client.domain.productuseractivity.ProductUserActivityResources;
import com.ctl.security.data.client.domain.user.UserResource;
import com.ctl.security.data.common.domain.mongo.Product;
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
import java.util.Set;

import static org.junit.Assert.*;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {CucumberConfiguration.class})
public class PolicySteps {

    private static final String VALID_POLICY_ID = "12345";
    private static final String VALID = "valid";
    private static final String VALID_AA = "TCCD";
    private static final String INVALID_AA = "TCCX";
    private static final String INVALID_POLICY_ID = "45678";

    private static final String VALID_USERNAME = "kweber.tccd";
    private static final String VALID_PASSWORD = "1qaz@WSX";
    private static final String INVALID_TOKEN = "Bearer SomeinvalidToken";

    private Exception exception;
    private List<Policy> policyList;
    private Policy policy;

    private String accountId;
    private String bearerToken;
    private String serverDomainName;
    private String username;

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

    @When("^I POST a policy$")
    public void I_POST_a_policy() {
        try {
            policy = new Policy();
            String name = "name" + System.currentTimeMillis();
            serverDomainName = "server.domain.name." + System.currentTimeMillis();
            String userName = "userName" + System.currentTimeMillis();
            policy.setName(name).
                    setServerDomainName(serverDomainName).
                    setUsername(userName);

            policyClient.createPolicyForAccount(accountId, policy, bearerToken);
        } catch (Exception e) {
            exception = e;
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

                String policyName = policy.getName();
                Policy retrievedPolicy = getPolicyWithWait(policyName);
                id = retrievedPolicy.getVendorPolicyId();
                username = policy.getUsername();

                policyClient.deletePolicyForAccount(accountId, id, username, serverDomainName, bearerToken);

            }
        } catch (Exception e) {
            exception = e;
        }
    }

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

    @Then("^I receive a response that contains the expected policy$")
    public void I_receive_a_response_that_contains_the_expected_policy() {
        Policy expected = buildPolicy();
        assertEquals(expected, policy);
    }

    @Then("^I receive a response that contains a uuid for the created policy$")
    public void I_receive_a_response_that_contains_a_uuid_for_the_created_policy() throws DsmPolicyClientException, InterruptedException {
        dsmClientComponent.verifyDsmPolicyCreation(dsmPolicyClient, policy);
        verifyCmdbCreation();
    }



    @Then("^I receive a response that does not contain an error message$")
    public void I_receive_a_response_that_does_not_contain_an_error_message() {
    }

    @Then("^I see that the policy has been deleted$")
    public void i_see_that_the_policy_has_been_deleted() throws Throwable {

        assertNull(exception);

        ConfigurationItemResource configurationItemResource = configurationItemClient.getConfigurationItem(serverDomainName, accountId);
        Set<Product> products = configurationItemResource.getContent().getProducts();
        assert products.toString().contains("status=INACTIVE");

        //TODO: We need the GET operation to work before we can test it in this way.
//        boolean isDeleted = false;
//
//        int i = 0;
//        int maxTries = 10;
//        while(i < maxTries && !isDeleted){
//            Policy retrievedPolicy = dsmPolicyClient.retrieveSecurityProfileByName(policy.getName());
//            if(retrievedPolicy == null || retrievedPolicy.getName() == null){
//                isDeleted = true;
//            }
//            Thread.sleep(1000);
//            i++;
//        }
//        assertTrue(isDeleted);
    }

    @Then("^I receive a response with error message (.*)$")
    public void I_receive_a_response_with_error_message(String message) throws Throwable {
        if (exception instanceof PolicyNotFoundException) {
            assertNotNull(message, exception.getMessage());
        } else if (exception instanceof NotAuthorizedException) {
            assertNotNull(message, exception.getMessage());
        } else {
            if(exception != null){
                exception.printStackTrace();
            }
            fail();
        }
    }

    private Policy getPolicyWithWait(String policyName) throws DsmPolicyClientException, InterruptedException {
        Policy retrievedPolicy = null;
        int i = 0;
        int maxTries = 10;
        while(i < maxTries && retrievedPolicy == null){
            retrievedPolicy = dsmPolicyClient.retrieveSecurityProfileByName(policyName);
            Thread.sleep(1000);
            i++;
        }
        return retrievedPolicy;
    }

    private Policy buildPolicy() {
        return new Policy().setVendorPolicyId(VALID_POLICY_ID).setStatus(PolicyStatus.ACTIVE);
    }

    private void verifyCmdbCreation() throws InterruptedException {

        UserResource user = null;

        int i = 0;
        int maxTries = 10;
        while(i < maxTries && (user == null || user.getId() == null)){
            user = userClient.getUser(policy.getUsername(), accountId);
            Thread.sleep(1000);
            i++;
        }

        assertNotNull(user.getId());
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
}

