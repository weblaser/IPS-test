package com.ctl.security.ips.maestro.config;

import com.ctl.security.data.client.config.SecurityDataClientAppConfig;
import com.ctl.security.ips.common.jms.config.JmsConfig;
import com.ctl.security.ips.dsm.config.DsmConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {"com.ctl.security.ips.maestro"})
@Import({DsmConfig.class, SecurityDataClientAppConfig.class, JmsConfig.class})
@PropertySource("classpath:properties/security.data.client.properties")
public class MaestroConfig {

}
