package com.ctl.security.ips.common.jms;

import com.ctl.security.ips.common.jms.config.JmsConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import javax.jms.ConnectionFactory;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class JmsConfigTest {

    @InjectMocks
    private JmsConfig classUnderTest;

    @Before
    public void setup(){
        ReflectionTestUtils.setField(classUnderTest, "brokerUrl", "vm://localhost?broker.persistent=false");
    }

    @Test
    public void jmsTemplate_createsJmsTemplate() throws Exception {
        JmsTemplate jmsTemplate = classUnderTest.jmsTemplate();

        assertNotNull(jmsTemplate);
    }

    @Test
    public void jmsListenerContainerFactory_createsJmsListenerContainerFactory() throws Exception {
        DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory = classUnderTest.jmsListenerContainerFactory();

        assertNotNull(defaultJmsListenerContainerFactory);
    }

    @Test
    public void connectionFactory_createsConnectionFactory() throws Exception {
        ConnectionFactory connectionFactory = classUnderTest.connectionFactory();

        assertNotNull(connectionFactory);
    }
}