package com.ctl.security.ips.api.config;

import com.ctl.security.acegi.api.config.SecurityConfig;
import com.ctl.security.clc.client.core.config.ClcClientCoreAppConfig;
import com.wordnik.swagger.jaxrs.config.BeanConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {"com.ctl.security.ips"})
@Import({SecurityConfig.class, ClcClientCoreAppConfig.class})
public class ApiConfig {

    public static final String COM_CTL_SECURITY_IPS_API = "com.ctl.security.ips.api";
    public static final String IPS = "/ips/swagger";
    public static final String API_VERSION = "1.0.0";
    public static final String IPS_API = "IPS API";
    public static final String CENTURY_LINK_API_FOR_MANAGED_IPS_SERVICES = "CenturyLink API for managed IPS services";

    @Bean
    public BeanConfig swaggerConfig() {
        BeanConfig config = new BeanConfig();
        config.setResourcePackage(COM_CTL_SECURITY_IPS_API);
        config.setBasePath(IPS);
        config.setVersion(API_VERSION);
        config.setTitle(IPS_API);
        config.setDescription(CENTURY_LINK_API_FOR_MANAGED_IPS_SERVICES);
        config.setScan(true);
        return config;
    }

}
