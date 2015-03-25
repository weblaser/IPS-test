package com.ctl.security.ips.informant;

import com.ctl.security.ips.informant.cofig.InformantConfig;
import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.config.java.annotation.Import;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by Sean Robb on 3/24/2015.
 *
 */
@EnableAutoConfiguration
@Import(InformantConfig.class)
public class InformantApplication {
//    private static Logger logger = LogManager.getLogger(InformantApplication.class);

    public static void main(String[] args) throws SchedulerException {
        ConfigurableApplicationContext context = SpringApplication.run(InformantApplication.class, args);

        InformantScheduler informantScheduler = context.getBean(InformantScheduler.class);
        informantScheduler.run();

//        SpringApplication.run(InformantApplication.class, args);
    }

}
