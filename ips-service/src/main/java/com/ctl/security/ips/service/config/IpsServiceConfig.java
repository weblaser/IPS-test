package com.ctl.security.ips.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;


@Configuration
@ComponentScan(basePackages = "com.ctl.security.ips.service")
@PropertySource("classpath:properties/security.data.client.properties")
public class IpsServiceConfig {

    public static final String IPS_SERVICE_REST_TEMPLATE = "ipsServiceRestTemplate";

    @Bean(name = IPS_SERVICE_REST_TEMPLATE)
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
