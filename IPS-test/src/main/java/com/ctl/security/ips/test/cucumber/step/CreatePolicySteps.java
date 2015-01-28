package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.domain.CtlSecurityProfile;
import com.ctl.security.ips.test.cucumber.config.CucumberConfiguration;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;


/**
 * Created by chad.middleton on 1/16/2015.
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = CucumberConfiguration.class)
public class CreatePolicySteps {

    @Autowired
    private DsmPolicyClient policyClient;

    @Autowired
    private RestTemplate restTemplate;

    private CtlSecurityProfile ctlSecurityProfileToBeCreated;
    private CtlSecurityProfile newlyCreatedCtlSecurityProfile;
    private String sessionId;

    private String username = "apiuser";
    private String password = "trejachad32jUgEs";


    @Given("^I have a policy that I want to create in DSM$")
    public void i_have_a_policy_that_I_want_to_create_in_DSM() throws Throwable {
        ctlSecurityProfileToBeCreated = new CtlSecurityProfile();

        String name = "name" + System.currentTimeMillis();
        ctlSecurityProfileToBeCreated.setName(name);
    }

    @When("^I execute the \"(.*?)\" operation against the DSM API$")
    public void i_execute_the_operation_against_the_DSM_API(String arg1) throws Throwable {

        newlyCreatedCtlSecurityProfile = policyClient.createCtlSecurityProfile(ctlSecurityProfileToBeCreated);

    }



    @Then("^I receive a new policy response$")
    public void i_receive_a_new_policy_response() throws Throwable {

        assertNotNull(newlyCreatedCtlSecurityProfile);
    }

    @Then("^I handle the error correctly$")
    public void i_handle_the_error_correctly() throws Throwable {
    }


}
