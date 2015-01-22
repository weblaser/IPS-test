package com.ctl.security.ips.test.cucumber.config;

import manager.Manager;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by kevin.wilde on 1/21/2015.
 */
@Configuration
@Profile("test")
public class TestConfig {

    private static final Logger logger = Logger.getLogger(TestConfig.class);

    @Bean
    public Manager manager() {
        return null;
    }

}
