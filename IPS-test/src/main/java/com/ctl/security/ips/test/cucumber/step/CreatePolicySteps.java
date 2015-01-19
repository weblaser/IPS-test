package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.dsm.DsmPolicyClient;
import cucumber.api.java.en.Given;


import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import manager.SecurityProfileTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertNotNull;

/**
 * Created by chad.middleton on 1/16/2015.
 */
@ContextConfiguration("classpath*:cucumber.xml")
public class CreatePolicySteps {

    @Autowired
    private DsmPolicyClient policyClient;

    private SecurityProfileTransport securityProfileTransportToBeCreated;
    private SecurityProfileTransport newlyCreatedSecurityProfileTransport;
    private String sessionId;

    private String username = "apiuser";
    private String password = "trejachad32jUgEs";


    @Given("^I have a policy that I want to create in DSM$")
    public void i_have_a_policy_that_I_want_to_create_in_DSM() throws Throwable {
        securityProfileTransportToBeCreated = new SecurityProfileTransport();

        String name = "name" + System.currentTimeMillis();
        securityProfileTransportToBeCreated.setName(name);
    }

    @When("^I execute the \"(.*?)\" operation against the DSM API$")
    public void i_execute_the_operation_against_the_DSM_API(String arg1) throws Throwable {
        newlyCreatedSecurityProfileTransport = policyClient.createPolicyOnDSMClient(username, password, securityProfileTransportToBeCreated);
    }

    @Then("^I receive a new policy response$")
    public void i_receive_a_new_policy_response() throws Throwable {
        assertNotNull(newlyCreatedSecurityProfileTransport);
    }


}
