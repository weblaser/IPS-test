package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.ips.test.cucumber.config.CucumberConfiguration;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.fail;

/**
 * Created by kevin.wilde on 2/12/2015.
 */

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {CucumberConfiguration.class})
public class JmsSteps {


    private String message;


    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private JmsCukeComponent jmsCukeComponent;

    @Given("^a message is ready to be sent to the queue$")
    public void a_message_is_ready_to_be_sent_to_the_queue() throws Throwable {
        message = "message";
    }

    @When("^the message is sent to the queue$")
    public void the_message_is_sent_to_the_queue() throws Throwable {
        sendMessageToQueue();
    }


    @Then("^the message is produced on the queue$")
    public void the_message_is_produced_on_the_queue() throws Throwable {

    }



    @Given("^a listener is listening for messages on the queue$")
    public void a_listener_is_listening_for_messages_on_the_queue() throws Throwable {

    }

    @When("^a message is sent to the queue$")
    public void a_message_is_sent_to_the_queue() throws Throwable {
        message = "message";
        sendMessageToQueue();
    }

    @Then("^the message is consumed from the queue$")
    public void the_message_is_consumed_from_the_queue() throws Throwable {
        boolean success = false;
        int i = 0;
        int maxTries = 30;
        while(i < maxTries && !success){
            if(jmsCukeComponent.getReceivedMessage() != null){
                success = true;
            }
            Thread.sleep(1000);
            i++;
        }
        if(!success){
            fail("The message was never consumed.");
        }
    }


    private void sendMessageToQueue() {
        jmsTemplate.convertAndSend(JmsCukeComponent.AT_QUEUE, message);
    }


}
