package com.ctl.security.ips.api.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

/**
 * Created by kevin.wilde on 2/12/2015.
 */
public class MessageSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendMessage(final Object message) {
        jmsTemplate.convertAndSend(message);
    }
}
