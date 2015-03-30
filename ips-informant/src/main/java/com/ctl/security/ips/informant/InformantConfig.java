package com.ctl.security.ips.informant;

import com.ctl.security.clc.client.core.config.ClcClientCoreAppConfig;
import com.ctl.security.ips.dsm.config.DsmConfig;
import com.ctl.security.library.common.config.SecurityLibraryCommonAppConfig;
import org.quartz.impl.StdSchedulerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Sean Robb on 3/24/2015.
 */

@Configuration
@EnableAsync
@EnableScheduling
@Import({DsmConfig.class, ClcClientCoreAppConfig.class})
@ComponentScan(basePackages = {"com.ctl.security.ips.informant"})
public class InformantConfig {


    @Bean
    public StdSchedulerFactory stdSchedulerFactory() {
        return new StdSchedulerFactory();
    }

}
