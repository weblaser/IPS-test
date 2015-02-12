package com.ctl.security.ips.api.jms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MessageSenderTest {

    @InjectMocks
    private MessageSender classUnderTest;

    @Mock
    private JmsTemplate jmsTemplate;

    @Test
    public void sendMessage_sendsMessage() {
        Object message = null;

        classUnderTest.sendMessage(message);

        verify(jmsTemplate).convertAndSend(message);
    }

}