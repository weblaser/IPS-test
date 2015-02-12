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
    public JmsTemplate jmsTemplate() {

        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        return null;
    }

    private ActiveMQConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("http://localhost:8161");
        return activeMQConnectionFactory;
    }


}
