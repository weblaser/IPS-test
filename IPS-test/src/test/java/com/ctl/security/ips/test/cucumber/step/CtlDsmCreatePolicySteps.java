package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.test.cucumber.config.CucumberConfiguration;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import manager.Manager;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.junit.Assert.*;


/**
 * Created by chad.middleton on 1/16/2015.
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = CucumberConfiguration.class)
public class CtlDsmCreatePolicySteps {



    @Autowired
    private DsmPolicyClient dsmPolicyClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Manager manager;

    private Policy policy;
    private Policy newlyCreatedCtlPolicy;


    private String username = "apiuser";
    private String password = "trejachad32jUgEs";


    @Given("^I have a policy that I want to create in DSM$")
    public void i_have_a_policy_that_I_want_to_create_in_DSM() throws Throwable {

        policy = new Policy();

        String name = "name" + System.currentTimeMillis();
        policy.setName(name);

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

        Policy deletedPolicy = dsmPolicyClient.retrieveSecurityProfileById(NumberUtils.createInteger(retrievedPolicy.getId()));
        assertTrue(deletedPolicy.getId() == null);
    }

    @Then("^I handle the error correctly$")
    public void i_handle_the_error_correctly() throws Throwable {
    }



}
