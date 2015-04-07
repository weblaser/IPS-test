package com.ctl.security.ips.informant;

import com.ctl.security.clc.client.core.config.ClcClientCoreAppConfig;
import com.ctl.security.ips.dsm.config.DsmConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.*;

/**
 * Created by Sean Robb on 3/24/2015.
 */

@Configuration
@EnableAsync
@EnableScheduling
@Import({DsmConfig.class, ClcClientCoreAppConfig.class})
@PropertySource("classpath:properties/informant.properties")
@ComponentScan(basePackages = {"com.ctl.security.ips.informant"})
public class InformantConfig {
    @Bean
    File file(){
        return new File("lastExecution.txt");
    }

}
