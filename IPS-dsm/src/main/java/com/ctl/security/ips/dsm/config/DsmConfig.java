package com.ctl.security.ips.dsm.config;

import com.ctl.security.ips.client.config.ClientConfig;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.*;

@PropertySources({@PropertySource("classpath:/dsm.client.properties")})
@Configuration
@ComponentScan(basePackages = {"com.ctl.security.ips.dsm"})
@Import({DsmBeans.class, ClientConfig.class})
public class DsmConfig {
    private static final Logger logger = Logger.getLogger(DsmConfig.class);

}