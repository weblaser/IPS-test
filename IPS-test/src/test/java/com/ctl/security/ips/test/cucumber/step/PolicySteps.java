package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.clc.client.common.domain.ClcAuthenticationResponse;
import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.cmdb.ProductUserActivityClient;
import com.ctl.security.data.client.cmdb.UserClient;
import com.ctl.security.data.client.domain.configurationitem.ConfigurationItemResource;
import com.ctl.security.data.client.domain.productuseractivity.ProductUserActivityResources;
import com.ctl.security.data.client.domain.user.UserResource;
import com.ctl.security.data.common.domain.mongo.Product;
import com.ctl.security.data.common.domain.mongo.ProductUserActivity;
import com.ctl.security.ips.client.PolicyClient;
import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.common.domain.Policy.PolicyStatus;
import com.ctl.security.ips.common.exception.NotAuthorizedException;
import com.ctl.security.ips.common.exception.PolicyNotFoundException;
import com.ctl.security.ips.dsm.DsmAgentClient;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.DsmTenantClient;
import com.ctl.security.ips.dsm.domain.DsmTenant;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import com.ctl.security.ips.test.cucumber.config.CucumberConfiguration;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import manager.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {CucumberConfiguration.class})
public class PolicySteps {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(PolicySteps.class);

    private static final String VALID = "valid";
    public static final int MAX_WAIT_TIME = 300;
    private static final String INVALID_AA = "TCCX";
    public static final String VALID_POLICY_ID = "12345";
    private static final String INVALID_POLICY_ID = "45678";
    private static final String INVALID_TOKEN = "Bearer SomeinvalidToken";

    private Policy policy;
    private String hostName;
    private String username;
    private String accountId;
    private String bearerToken;
    private Exception exception;
    private List<Policy> policyList;


    @Autowired
    private PolicyClient policyClient;

    @Autowired
    private DsmAgentClient dsmAgentClient;

    @Autowired
    private DsmClientComponent dsmClientComponent;

    @Autowired
    private DsmPolicyClient dsmPolicyClient;

    @Autowired
    private DsmTenantClient dsmTenantClient;

    @Autowired
    private ConfigurationItemClient configurationItemClient;

    @Autowired
    private ProductUserActivityClient productUserActivityClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ClcAuthenticationComponent clcAuthenticationComponent;

    @Autowired
    private Environment environment;

    @Value("${clc.client.test.package.linux.server}")
    private String clcLinuxServerName;

    @Value("${clc.client.test.package.windows.server}")
    private String clcWindowsServerName;


    @Given("^I have an? (.*) account$")
    public void I_have_validity_account(String validity) throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthorizationException_Exception, ManagerTimeoutException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception {
        if (VALID.equalsIgnoreCase(validity)) {
            accountId = ClcAuthenticationComponent.VALID_AA;

            ClcAuthenticationResponse authenticationResponse = clcAuthenticationComponent.authenticate();
            bearerToken = authenticationResponse.getBearerToken();
        } else {
            accountId = INVALID_AA;
            bearerToken = INVALID_TOKEN;
        }
    }

    @When("^I POST a (.*) policy$")
    public void I_POST_a_policy(String osType) {
        try {
            policy = new Policy();

            String activeProfiles = environment.getActiveProfiles()[0];

            if (activeProfiles.equalsIgnoreCase("local") || activeProfiles.equalsIgnoreCase("dev")) {
                hostName = "server.host.name." + System.currentTimeMillis();
            }
            else{
                if (osType.contains("windows")){
                    hostName = clcWindowsServerName;
                }else {
                    hostName = clcLinuxServerName;
                }
            }

            String userName = "userName" + System.currentTimeMillis();

            policy.setName("name" + System.currentTimeMillis()).
                    setHostName(hostName).
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

                if (VALID.equalsIgnoreCase(validity)) {
                    dsmClientComponent.verifyDsmPolicyCreation(dsmPolicyClient, policy, false);
                    verifyCmdbCreation(false);
                }

                username = policy.getUsername();

                policyClient.deletePolicyForAccount(accountId, id, username, hostName, bearerToken);

            }
        } catch (Exception e) {
            logger.log(Level.ERROR, e.getMessage(), e);
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
    public void I_receive_a_response_that_contains_a_uuid_for_the_created_policy() throws DsmClientException, InterruptedException {
        dsmClientComponent.verifyDsmPolicyCreation(dsmPolicyClient, policy, true);
        verifyCmdbCreation(true);
        //Normally Account Alias is equal to Tenant Name which is derived from the policy user name
        //assertTrue(dsmAgentClient.verifyAgentInstall(policy.getUsername(), hostName));
    }

    @Then("^a security profile is created in the DSM$")
    public void a_security_profile_is_created_in_the_DSM() throws DsmClientException, InterruptedException {
        dsmClientComponent.verifyDsmPolicyCreation(dsmPolicyClient, policy, true);
    }

    @And("^a Tenant is created in the REST DSM$")
    public void a_Tenant_is_created_in_the_REST_DSM() throws DsmClientException {
        dsmClientComponent.verifyDsmTenantCreation(dsmTenantClient, policy.getTenantId(), true);
    }

    @And("^a policy is created in our CMDB$")
    public void a_policy_is_created_in_our_CMDB() throws InterruptedException {
        verifyCmdbCreation(true);
    }

    @And("^the agent is activated on the server$")
    public void the_agent_is_activated_on_the_server() {
        assertTrue(dsmAgentClient.verifyAgentInstall(policy.getUsername(), policy.getHostName()));
    }

    @Then("^I receive a response that does not contain an error message$")
    public void I_receive_a_response_that_does_not_contain_an_error_message() {
    }

    @Then("^I see that the policy has been deleted$")
    public void i_see_that_the_policy_has_been_deleted() throws Throwable {

        ConfigurationItemResource configurationItemResource = null;
        Set<Product> products = new HashSet<>();

        int i = 0;
        int maxTries = MAX_WAIT_TIME;
        while(i < maxTries && !products.toString().contains("status=INACTIVE")){
            configurationItemResource = configurationItemClient.getConfigurationItem(hostName, accountId);
            products = configurationItemResource.getContent().getProducts();
            Thread.sleep(1000);
            i++;
        }

        assert products.toString().contains("status=INACTIVE");

        verifyCmdbCreation(true);
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

    private Policy buildPolicy() {
        return new Policy().setVendorPolicyId(VALID_POLICY_ID).setStatus(PolicyStatus.ACTIVE);
    }

    private void verifyCmdbCreation(boolean cleanup) throws InterruptedException {

        UserResource user = null;

        int i = 0;
        int maxTries = MAX_WAIT_TIME;
        while(i < maxTries && (user == null || user.getId() == null)){
            user = userClient.getUser(policy.getUsername(), accountId);
            Thread.sleep(1000);
            i++;
        }

        String message = "Failure in getting user with name: " + policy.getName() + " username: " + policy.getUsername();
        assertNotNull(message, user.getId());
        assertNotNull(message, user.getContent());
        assertNotNull(message, user.getContent().getId());
        assertNotNull(message, user.getContent().getProductUserActivities());

        ProductUserActivityResources productUserActivityResources = userClient.getProductUserActivities(user);
        assertNotNull(productUserActivityResources);
        assertNotNull(productUserActivityResources.getContent());
        List<ProductUserActivity> productUserActivities = productUserActivityResources.unwrap();
        assertTrue(productUserActivities.size() > 0);

        ConfigurationItemResource configurationItemResource = configurationItemClient.getConfigurationItem(policy.getHostName(), accountId);
        assertNotNull(configurationItemResource);
        assertNotNull(configurationItemResource.getContent().getId());

        if(cleanup){
            cleanUpCmdb(user, productUserActivities, configurationItemResource);
        }
    }

    private void cleanUpCmdb(UserResource user, List<ProductUserActivity> productUserActivities, ConfigurationItemResource configurationItemResource) {
        userClient.deleteUser(user.getContent().getId());
        productUserActivities.stream().forEach(x -> productUserActivityClient.deleteProductUserActivity(x.getId()));
        configurationItemClient.deleteConfigurationItem(configurationItemResource.getContent().getId());
    }
}

