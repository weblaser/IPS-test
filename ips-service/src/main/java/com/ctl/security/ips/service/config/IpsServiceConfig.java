package com.ctl.security.ips.service.config;

import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;


@Configuration
@ComponentScan(basePackages = "com.ctl.security.ips.service")
@PropertySources({
        @PropertySource("classpath:properties/security.data.client.properties"),
        @PropertySource("classpath:properties/ips.service.properties")
})
public class IpsServiceConfig {

    public static final String IPS_SERVICE_REST_TEMPLATE = "ipsServiceRestTemplate";

    @Bean(name = IPS_SERVICE_REST_TEMPLATE)
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
