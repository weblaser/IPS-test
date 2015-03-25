package com.ctl.security.ips.informant;

import com.ctl.security.ips.informant.config.InformantConfig;
import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.config.java.annotation.Import;
import org.springframework.context.ApplicationContext;

/**
 * Created by Sean Robb on 3/24/2015.
 *
 */

@SpringBootApplication
@Import(InformantConfig.class)
public class InformantApplication {
//    private static Logger logger = LogManager.getLogger(InformantApplication.class);

    public static void main(String[] args) throws SchedulerException {
//        ConfigurableApplicationContext context = SpringApplication.run(InformantApplication.class, args);
//

//        SpringApplication.run(InformantApplication.class, args);

        ApplicationContext applicationContext = SpringApplication.run(InformantApplication.class, args);

        InformantScheduler informantScheduler = applicationContext.getBean(InformantScheduler.class);
        informantScheduler.run();

    }

}
