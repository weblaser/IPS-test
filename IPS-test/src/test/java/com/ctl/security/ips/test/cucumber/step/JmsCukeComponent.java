package com.ctl.security.ips.test.cucumber.step;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Created by kevin.wilde on 2/12/2015.
 */

@Component
public class JmsCukeComponent {

    public static final String AT_QUEUE = "at-queue";

    private String receivedMessage;

    @JmsListener(destination = AT_QUEUE)
    public void consumeAtQueueMessage(String message){
        receivedMessage = message;
    }

    public String getReceivedMessage(){
        return receivedMessage;
    }
}
