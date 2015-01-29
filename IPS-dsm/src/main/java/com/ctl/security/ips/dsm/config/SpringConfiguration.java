package com.ctl.security.ips.dsm.config;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@PropertySources({@PropertySource("classpath:/dsm.client.properties")})
@Configuration
@Import({DsmBeans.class})
public class SpringConfiguration {
    private static final Logger logger = Logger.getLogger(SpringConfiguration.class);

}