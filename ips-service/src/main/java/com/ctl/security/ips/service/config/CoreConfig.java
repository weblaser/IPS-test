package com.ctl.security.ips.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@ComponentScan(basePackages = "com.ctl.security.ips.crud")
@PropertySource("classpath:properties/security.data.client.properties")
public class CoreConfig {

}
