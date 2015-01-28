package com.ctl.security.ips.dsm.config;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.*;

@PropertySources({@PropertySource("classpath:/dsm.client.properties")})
@Configuration
@Import({ProdDsmBeans.class})
public class SpringConfiguration {
    private static final Logger logger = Logger.getLogger(SpringConfiguration.class);

}