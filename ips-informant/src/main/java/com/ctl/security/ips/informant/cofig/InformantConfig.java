package com.ctl.security.ips.informant.cofig;

import com.ctl.security.ips.dsm.config.DsmConfig;
import com.ctl.security.ips.informant.InformantScheduler;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.config.java.annotation.Import;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Created by Sean Robb on 3/24/2015.
 */

@Configuration
@Import(DsmConfig.class)
@ComponentScan(basePackages = {"com.ctl.security.ips.informant"})
public class InformantConfig {


    @Bean
    public StdSchedulerFactory stdSchedulerFactory() {
        return new StdSchedulerFactory();
    }

}
