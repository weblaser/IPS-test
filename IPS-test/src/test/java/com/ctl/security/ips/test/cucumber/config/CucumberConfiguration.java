package com.ctl.security.ips.test.cucumber.config;

import com.ctl.security.data.client.config.SecurityDataClientAppConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * @author lane.maxwell
 */
@Configuration
@ComponentScan("com.ctl.security")
@Import({SecurityDataClientAppConfig.class})
@PropertySource("classpath:properties/security.data.client.properties")
public class CucumberConfiguration {


}
