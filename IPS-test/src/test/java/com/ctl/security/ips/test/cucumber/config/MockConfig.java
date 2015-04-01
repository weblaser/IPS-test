package com.ctl.security.ips.test.cucumber.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

import com.ctl.security.ips.test.cucumber.adapter.EventAdapter;
import com.ctl.security.ips.test.cucumber.adapter.MockEventAdapterImpl;
import org.springframework.context.annotation.*;

/**
 * Created by Sean Robb on 3/30/2015.
 *
 */
@Configuration
@PropertySource("classpath:properties/ips.dsm.mock.test.properties")
@Profile({"local", "dev"})
public class MockConfig {

    @Autowired
    private MockDsmRest mockDsmRest;
    
    @Bean
    EventAdapter eventAdapter(){
        return new MockEventAdapterImpl();
    }

    @PostConstruct
    public void mockDsmRest(){
        mockDsmRest.init();
    }

}