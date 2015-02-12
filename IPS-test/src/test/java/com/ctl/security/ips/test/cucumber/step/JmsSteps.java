package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.ips.test.cucumber.config.CucumberConfiguration;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * Created by kevin.wilde on 2/12/2015.
 */

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {CucumberConfiguration.class})
public class JmsSteps {

    public static final String AT_QUEUE = "at-queue";
    private String message;

    @Autowired
    private JmsTemplate jmsTemplatePolicy;

    @Given("^a message is ready to be sent to the queue$")
    public void a_message_is_ready_to_be_sent_to_the_queue() throws Throwable {
        message = "message";
    }

    @When("^the message is sent to the queue$")
    public void the_message_is_sent_to_the_queue() throws Throwable {
        jmsTemplatePolicy.convertAndSend(AT_QUEUE, message);
    }

    @Then("^the message is accepted and placed on the queue$")
    public void the_message_is_accepted_and_placed_on_the_queue() throws Throwable {

    }
}
