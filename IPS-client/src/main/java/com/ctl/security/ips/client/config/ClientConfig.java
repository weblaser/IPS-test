package com.ctl.security.ips.client.config;

import com.ctl.security.library.config.SecurityLibraryCommonAppConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by Kevin.Weber on 10/29/2014.
 */
@Configuration
@ComponentScan(basePackages = "com.ctl.security.ips.client")
@PropertySource("classpath:properties/ips.client.properties")
@Import({SecurityLibraryCommonAppConfig.class})
public class ClientConfig {

}
