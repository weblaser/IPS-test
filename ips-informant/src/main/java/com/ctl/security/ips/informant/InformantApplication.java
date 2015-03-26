package com.ctl.security.ips.informant;

import com.ctl.security.ips.informant.service.InformantScheduler;
import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * Created by Sean Robb on 3/24/2015.
 *
 */

@SpringBootApplication
@Import(InformantConfig.class)
public class InformantApplication {
//    private static Logger logger = LogManager.getLogger(InformantApplication.class);

    public static void main(String[] args) throws SchedulerException {

        ApplicationContext applicationContext = SpringApplication.run(InformantApplication.class, args);

        InformantScheduler informantScheduler = applicationContext.getBean(InformantScheduler.class);
        informantScheduler.run();

    }

}
