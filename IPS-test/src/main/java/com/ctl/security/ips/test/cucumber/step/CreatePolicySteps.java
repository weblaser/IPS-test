package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.dsm.DsmPolicyClient;
import com.ctl.security.dsm.authenticate.DsmPolicyClient;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import cucumber.api.java.en.Given;


import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import manager.SecurityProfileTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
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

        WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(8089)); //No-args constructor will start on port 8080, no HTTPS
        wireMockServer.start();

        WireMock.configureFor("localhost", 8089);

        try{
        stubFor(get(urlEqualTo("/some/thing"))
//            stubFor(get(urlEqualTo("/"))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "text/plain")
                            .withBody("Hello world!")));

        newlyCreatedSecurityProfileTransport = policyClient.createPolicyOnDSMClient(username, password, securityProfileTransportToBeCreated);
        }
        finally{
            wireMockServer.stop();
        }
    }

    @Then("^I receive a new policy response$")
    public void i_receive_a_new_policy_response() throws Throwable {
        assertNotNull(newlyCreatedSecurityProfileTransport);
    }


}
