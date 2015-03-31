package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.ips.cli.IpsApiCli;
import com.ctl.security.ips.cli.IpsApiCliDelegator;
import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.test.cucumber.config.CucumberConfiguration;
import com.ctl.security.library.test.TestAppender;
import com.google.gson.Gson;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by kevin on 3/21/15.
 */

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {CucumberConfiguration.class})
public class IpsApiCliSteps {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(IpsApiCliSteps.class);

    final TestAppender testAppender = new TestAppender();
    final Logger rootLogger = Logger.getRootLogger();

    private String bearerToken;
    private String accountId;
    private String policyId;

    private String policyJson;
    private Policy policy;


    @Autowired
    private ClcAuthenticationComponent clcAuthenticationComponent;


    @Given("^there is a policy to retrieve$")
    public void there_is_a_policy_to_retrieve() throws Throwable {
        bearerToken = clcAuthenticationComponent.authenticate();
        accountId = ClcAuthenticationComponent.VALID_AA;
        policyId = PolicySteps.VALID_POLICY_ID;
    }

    @When("^the ips-cli is used to retrieve that policy$")
    public void the_ips_cli_is_used_to_retrieve_that_policy() throws Throwable {
        String[] args = {IpsApiCliDelegator.GET_POLICY_FOR_ACCOUNT,
                bearerToken,
                accountId,
                policyId};

        rootLogger.addAppender(testAppender);
        IpsApiCli.main(args);
        rootLogger.removeAppender(testAppender);
    }

    @Then("^the policy is retrieved$")
    public void the_policy_is_retrieved() throws Throwable {
        List<LoggingEvent> loggingEvents = testAppender.getLog();

        loggingEvents.forEach((loggingEvent) -> renderPolicyFromLog(loggingEvent));

        assertNotNull(policyJson);
        assertNotNull(policy);
        assertNotNull(policy.getVendorPolicyId());

    }

    private void renderPolicyFromLog(LoggingEvent loggingEvent) {
        System.out.print(loggingEvent.getRenderedMessage() + "\n");
        if(loggingEvent.getRenderedMessage().contains("vendorPolicyId")){
            policyJson = loggingEvent.getRenderedMessage();
            Gson gson = new Gson();
            policy = gson.fromJson(policyJson, Policy.class);
        }
    }

}
