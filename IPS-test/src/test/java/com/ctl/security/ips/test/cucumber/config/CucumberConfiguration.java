package com.ctl.security.ips.test.cucumber.config;

import com.ctl.security.data.client.config.SecurityDataClientAppConfig;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * @author lane.maxwell
 */
@Configuration
//TODO fix this component scan
@ComponentScan("com.ctl.security")
@Import({SecurityDataClientAppConfig.class,MockConfig.class,RealConfig.class})
@PropertySources({
        @PropertySource("classpath:properties/security.data.client.properties"),
        @PropertySource("classpath:properties/ips.service.properties"),
        @PropertySource("classpath:properties/ips.test.properties")
})
public class CucumberConfiguration {

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
