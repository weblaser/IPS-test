package com.ctl.security.ips.test.cucumber.config;

import com.ctl.security.data.client.config.SecurityDataClientAppConfig;
import org.springframework.context.annotation.*;

/**
 * @author lane.maxwell
 */
@Configuration
@ComponentScan("com.ctl.security")
@Import({SecurityDataClientAppConfig.class})
@PropertySources({
        @PropertySource("classpath:properties/security.data.client.properties"),
        @PropertySource("classpath:properties/ips.service.properties"),
        @PropertySource("classpath:properties/ips.test.properties")
})
public class CucumberConfiguration {


}
