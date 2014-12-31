package com.ctl.security.ips.client.config;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Kevin.Weber on 10/29/2014.
 */
@Configuration
@ComponentScan(basePackages = "com.ctl.security.ips.client")
@PropertySource("classpath:properties/ips.client.properties")
public class ClientConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
