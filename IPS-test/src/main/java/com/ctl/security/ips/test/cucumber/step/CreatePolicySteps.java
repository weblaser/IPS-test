package com.ctl.security.ips.test.cucumber.step;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by chad.middleton on 1/16/2015.
 */
@ContextConfiguration("classpath*:cucumber.xml")
public class CreatePolicySteps {

    private String sessionId;
    private String password;


    @Given("^I have a policy that I want to create in DSM$")
    public void i_have_a_policy_that_I_want_to_create_in_DSM() throws Throwable {
    }

    @When("^I execute the \"(.*?)\" operation against the DSM API$")
    public void i_execute_the_operation_against_the_DSM_API(String arg1) throws Throwable {
    }

    @Then("^I receive a new policy response$")
    public void i_receive_a_new_policy_response() throws Throwable {
    }


}
