package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import com.ctl.security.ips.maestro.service.PolicyServiceWrite;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by chad.middleton on 3/27/2015
 */
public class InstallProductIpsSteps {

    @Autowired
    private PolicyServiceWrite policyServiceWrite;

    private PolicyBean policyBean;

    @Given("^a customer wants to install IPS on a VM$")
    public void a_customer_wants_to_install_IPS_on_a_VM() {
        policyBean = new PolicyBean("SCDV", new Policy().setName("Foo").setHostName("blah"), "bearerToken");
    }

    @When("^the blueprint finishes in CLC$")
    public void the_blueprint_finishes_in_CLC() throws DsmClientException {
        policyServiceWrite.createPolicyForAccount(policyBean);
    }

    @Then("^the new policy is persisted in all correct areas$")
    public void the_new_policy_is_persisted_in_all_correct_areas() {
    }

}


