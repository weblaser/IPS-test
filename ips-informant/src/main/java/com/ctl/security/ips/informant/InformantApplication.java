package com.ctl.security.ips.informant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * Created by Sean Robb on 3/24/2015.
 *
 */

@EnableAutoConfiguration
@Import(InformantConfig.class)
public class InformantApplication {
    private static Logger logger = LogManager.getLogger(InformantApplication.class);

    public static void main(String[] args) throws SchedulerException {

        ApplicationContext applicationContext = SpringApplication.run(InformantApplication.class, args);

        logger.info("Informant Started...");
    }

}
