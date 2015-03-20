package com.ctl.security.ips.cli.config;

import com.ctl.security.ips.client.config.ClientConfig;
import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by kevin on 3/19/15.
 */

@Configuration
@ComponentScan(basePackages = "com.ctl.security.ips.cli")
@Import({ClientConfig.class})
public class IpsApiCliConfig {

    @Bean
    public Gson gson(){
        return new Gson();
    }
}
