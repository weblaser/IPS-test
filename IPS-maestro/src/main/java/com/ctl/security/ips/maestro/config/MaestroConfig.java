package com.ctl.security.ips.maestro.config;

import com.ctl.security.clc.client.core.config.ClcClientCoreAppConfig;
import com.ctl.security.data.client.config.SecurityDataClientAppConfig;
import com.ctl.security.ips.common.jms.config.JmsConfig;
import com.ctl.security.ips.dsm.config.DsmConfig;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan(basePackages = {"com.ctl.security.ips.maestro"})
@Import({DsmConfig.class, SecurityDataClientAppConfig.class, JmsConfig.class, ClcClientCoreAppConfig.class})
@PropertySources({
        @PropertySource("classpath:properties/security.data.client.properties"),
        @PropertySource("classpath:properties/ips.service.properties"),
        @PropertySource("classpath:properties/maestro.properties")
})
public class MaestroConfig {

    public static final String IPS_MAESTRO_REST_TEMPLATE = "ipsMaestroRestTemplate";

    @Bean(name = IPS_MAESTRO_REST_TEMPLATE)
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
