package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.clc.client.common.domain.ClcAuthenticationRequest;
import com.ctl.security.clc.client.common.domain.ClcAuthenticationResponse;
import com.ctl.security.clc.client.core.bean.AuthenticationClient;
import com.ctl.security.clc.client.core.bean.ServerClient;
import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.common.domain.SecurityTenant;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.DsmTenantClient;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import com.ctl.security.ips.test.cucumber.config.CucumberConfiguration;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by chad.middleton on 1/16/2015.
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = CucumberConfiguration.class)
public class DsmClientSteps {

    @Autowired
    private DsmPolicyClient dsmPolicyClient;
    @Autowired
    private DsmTenantClient dsmTenantClient;
    @Autowired
    private ClcAuthenticationComponent clcAuthenticationComponent;

    @Autowired
    private DsmClientComponent dsmClientComponent;

    private Policy policy;
    private PolicyBean policyBean;

    private Policy newlyCreatedCtlPolicy;

    private SecurityTenant securityTenant;
    private SecurityTenant newlyCreateSecurityTenant;

    private SecurityTenant retrievedNewlyCreateSecurityTenant;
    private String username = "apiuser";
    private String password = "trejachad32jUgEs";

    private Integer tenantId;



    @Given("^I have a policy that I want to create in DSM$")
    public void i_have_a_policy_that_I_want_to_create_in_DSM() throws Throwable {
        ClcAuthenticationResponse clcAuthenticationResponse = clcAuthenticationComponent.authenticate();
        String bearerToken = clcAuthenticationResponse.getBearerToken();
        String accountAlias = clcAuthenticationResponse.getAccountAlias();

        policy = new Policy();

        String name = "name" + System.currentTimeMillis();
        policy.setName(name);
        policy.setHostName("VA1SCDVV2TEST01");
        policyBean = new PolicyBean(accountAlias, policy, bearerToken);

    }

    @Given("^a customer tenant is ready to be created$")
    public void a_customer_tenant_is_ready_to_be_created() throws Throwable {
        setupTenantToBeCreated();
    }

    @Given("^a tenant already exists in the DSM$")
    public void a_tenant_already_exists_in_the_DSM() throws DsmClientException {
        setupTenantToBeCreated();
        newlyCreateSecurityTenant = dsmTenantClient.createDsmTenant(securityTenant);
    }


    @When("^the dsm rest client is used to (create|delete) the tenant$")
    public void the_dsm_rest_client_is_used_to_create_the_tenant(String crud) throws Throwable {
        final String create = "create";
        final String delete = "delete";

        if (crud.equalsIgnoreCase(create)) {
            newlyCreateSecurityTenant = dsmTenantClient.createDsmTenant(securityTenant);
        } else if (crud.equalsIgnoreCase(delete)) {
            dsmTenantClient.deleteDsmTenant(newlyCreateSecurityTenant.getTenantId().toString());
        }
    }

    @When("^the dsm rest client is used to retrieve the tenant$")
    public void the_dsm_rest_client_is_used_to_retrieve_the_tenant() throws DsmClientException {
        retrievedNewlyCreateSecurityTenant = dsmTenantClient.retrieveDsmTenant(newlyCreateSecurityTenant.getTenantId());
    }

    @When("^I execute the \"(.*?)\" operation against the DSM API$")
    public void i_execute_the_operation_against_the_DSM_API(String arg1) throws Throwable {
        newlyCreatedCtlPolicy = dsmPolicyClient.createCtlSecurityProfile(policyBean);
    }


    @Then("^I receive a new policy response$")
    public void i_receive_a_new_policy_response() throws Throwable {

        assertNotNull(newlyCreatedCtlPolicy);
    }

    @Then("^I am able to retrieve the newly created policy$")
    public void i_am_able_to_retrieve_the_newly_created_policy() throws Throwable {
        dsmClientComponent.verifyDsmPolicyCreation(dsmPolicyClient, newlyCreatedCtlPolicy, true);
    }

    @Then("^the tenant has been created in DSM$")
    public void the_tenant_has_been_created_in_DSM() throws Throwable {
        assertNotNull(newlyCreateSecurityTenant);
        assertNotNull(newlyCreateSecurityTenant.getTenantId());
        assertNotNull(newlyCreateSecurityTenant.getAgentInitiatedActivationPassword());
    }

    @Then("^the correct tenant is returned$")
    public void the_correct_tenant_is_returned() {
        assertNotNull(retrievedNewlyCreateSecurityTenant);
    }

    @Then("^the tenant is pending deletion$")
    public void the_tenant_is_no_longer_available() throws DsmClientException {
        SecurityTenant securityTenant = dsmTenantClient.retrieveDsmTenant(newlyCreateSecurityTenant.getTenantId());

        //after deletion attempt to the DSM the tenant goes to a PENDING_DELETION state
        // for 7 days then is deleted from the DSM
        assertEquals("PENDING_DELETION", securityTenant.getState());
    }

    private void setupTenantToBeCreated() {
        String testTenant = "TestTenant" + System.currentTimeMillis();
        securityTenant = new SecurityTenant().setTenantName(testTenant).setAdminEmail("test@test.com")
                .setAdminPassword("secretpassword").setAdminAccount("TestAdmin");
    }
}
