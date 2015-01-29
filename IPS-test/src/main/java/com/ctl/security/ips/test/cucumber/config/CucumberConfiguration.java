package com.ctl.security.ips.test.cucumber.config;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kevin.wilde on 1/21/2015.
 */
@Configuration
@ComponentScan("com.ctl.security")
public class CucumberConfiguration {

    private static final Logger logger = Logger.getLogger(CucumberConfiguration.class);

}
