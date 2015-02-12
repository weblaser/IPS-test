package com.ctl.security.ips.common.jms;

import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

/**
 * Created by kevin.wilde on 2/11/2015.
 */

@Configuration
public class JmsConfig {


    @Bean
    public JmsTemplate jmsTemplatePolicy() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("tcp://localhost:61616");
        JmsTemplate jmsTemplate = new JmsTemplate(activeMQConnectionFactory);
//        Destination destination = new ActiveMQQueue("policy-queue");
//        jmsTemplate.setDefaultDestination(destination);
        return jmsTemplate;
    }



}
