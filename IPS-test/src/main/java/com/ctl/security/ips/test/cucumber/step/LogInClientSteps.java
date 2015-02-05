package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.ips.dsm.DsmLogInClient;
import com.ctl.security.ips.dsm.config.BaseDsmBeans;
import com.ctl.security.ips.test.cucumber.config.CucumberConfiguration;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import manager.ManagerAuthenticationException_Exception;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.assertTrue;

/**
 * Created by Chad.Middleton on 1/13/2015.
 */

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = CucumberConfiguration.class)
public class LogInClientSteps {

    private String user;
    private String password;
    private String sessionID;
    private Boolean authenticationError;

    @Autowired
    private DsmLogInClient client;

    @Given("^I have user account credentials$")
    public void i_have_user_account_credentials() throws Throwable {
        this.user = BaseDsmBeans.APIUSER;
        this.password = BaseDsmBeans.PASSWORD_CORRECT;
    }

    @Given("^I have an incorrect user$")
    public void i_have_an_incorrect_user() throws Throwable {
        this.user = BaseDsmBeans.APIUSER_WRONG;
        this.password = BaseDsmBeans.PASSWORD_CORRECT;
    }

    @Given("^I have an incorrect password$")
    public void i_have_an_incorrect_password() throws Throwable {
        this.user = BaseDsmBeans.APIUSER;
        this.password = BaseDsmBeans.PASSWORD_WRONG;
    }

    @When("^I attempt to authenticate against the dsm api$")
    public void i_attempt_to_authenticate_against_the_dsm_api() throws Throwable {
        try {
            this.sessionID = client.connectToDSMClient(user, password);
        } catch (ManagerAuthenticationException_Exception maee) {
            authenticationError = true;
        }
    }

    @Then("^I receive a valid session id token$")
    public void i_receive_a_valid_session_id_token() throws Throwable {
        assertTrue(StringUtils.isNotEmpty(sessionID));
    }

    @Then("^I receive an authentication error$")
    public void i_receive_an_authentication_error() throws Throwable {
        assertTrue(authenticationError);
    }


}

