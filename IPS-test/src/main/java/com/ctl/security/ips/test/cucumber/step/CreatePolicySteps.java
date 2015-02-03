package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.test.cucumber.config.CucumberConfiguration;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import manager.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


/**
 * Created by chad.middleton on 1/16/2015.
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = CucumberConfiguration.class)
public class CreatePolicySteps {

    public static final String APIUSER = "apiuser";
    public static final String PASSWORD_CORRECT = "trejachad32jUgEs";
    public static final String APIUSER_WRONG = "wrong";
    public static final String PASSWORD_WRONG = "wrong";

    @Autowired
    private DsmPolicyClient dsmPolicyClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Manager manager;

    private Policy policy;
    private Policy newlyCreatedCtlPolicy;

    private String sessionId;

    private String username = "apiuser";
    private String password = "trejachad32jUgEs";


    @Given("^I have a policy that I want to create in DSM$")
    public void i_have_a_policy_that_I_want_to_create_in_DSM() throws Throwable {
        policy = new Policy();

        String name = "name" + System.currentTimeMillis();
        policy.setName(name);

        sessionId = setupDsmAuthentication();
        setupCreatePolicyRetrievePolicy();
    }

    @When("^I execute the \"(.*?)\" operation against the DSM API$")
    public void i_execute_the_operation_against_the_DSM_API(String arg1) throws Throwable {

        newlyCreatedCtlPolicy = dsmPolicyClient.createCtlSecurityProfile(policy);

    }


    @Then("^I receive a new policy response$")
    public void i_receive_a_new_policy_response() throws Throwable {

        assertNotNull(newlyCreatedCtlPolicy);
    }

    @Then("^I am able to retrieve the newly created policy$")
    public void i_am_able_to_retrieve_the_newly_created_policy() throws Throwable {
        Policy retrievedPolicy = dsmPolicyClient.retrieveSecurityProfileById(new Integer(newlyCreatedCtlPolicy.getId()).intValue());
        assertNotNull(retrievedPolicy);
        assertEquals(newlyCreatedCtlPolicy.getName(), retrievedPolicy.getName());

        dsmPolicyClient.securityProfileDelete(Arrays.asList(NumberUtils.createInteger(retrievedPolicy.getId())));

        setupDeletePolicyRetrievePolicy();
        Policy deletedPolicy = dsmPolicyClient.retrieveSecurityProfileById(NumberUtils.createInteger(retrievedPolicy.getId()));
        assertTrue(deletedPolicy.getId() == null);
    }

    @Then("^I handle the error correctly$")
    public void i_handle_the_error_correctly() throws Throwable {
    }

    private void setupCreatePolicyRetrievePolicy() throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerMaxSessionsException_Exception, ManagerAuthenticationException_Exception, ManagerCommunicationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception, ManagerTimeoutException_Exception, ManagerAuthorizationException_Exception {

        SecurityProfileTransport expectedSecurityProfileTransport = new SecurityProfileTransport();
        int id = 0;
        expectedSecurityProfileTransport.setID(id);
        when(manager.securityProfileSave(Matchers.any(SecurityProfileTransport.class), Matchers.eq(sessionId))).thenReturn(expectedSecurityProfileTransport);

        when(manager.securityProfileRetrieve(id, sessionId)).thenReturn(expectedSecurityProfileTransport);
    }

    private String setupDsmAuthentication() throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerMaxSessionsException_Exception, ManagerAuthenticationException_Exception, ManagerCommunicationException_Exception, ManagerException_Exception {
        String sessionId = "123";
        when(manager.authenticate(APIUSER, PASSWORD_CORRECT)).thenReturn(sessionId);

        when(manager.authenticate(APIUSER, PASSWORD_WRONG)).thenThrow(ManagerAuthenticationException_Exception.class);
        when(manager.authenticate(APIUSER_WRONG, PASSWORD_CORRECT)).thenThrow(ManagerAuthenticationException_Exception.class);
        return sessionId;
    }


    private void setupDeletePolicyRetrievePolicy() throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthorizationException_Exception, ManagerTimeoutException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception {
        SecurityProfileTransport expectedSecurityProfileTransport = new SecurityProfileTransport();
        int id = 0;
        when(manager.securityProfileRetrieve(id, sessionId)).thenReturn(expectedSecurityProfileTransport);
    }
}
