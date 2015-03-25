package com.ctl.security.ips.informant;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by Sean Robb on 3/24/2015.
 */

@Component
public class InformantScheduler implements Runnable {

    @Autowired
    private StdSchedulerFactory stdSchedulerFactory;

    @Override
    public void run() {
        try {
            Scheduler scheduler = stdSchedulerFactory.getScheduler();

            JobDetail jobDetail = newJob(Informant.class)
                    .withIdentity("Informant Job", "Informant")
                    .build();
            Trigger trigger = newTrigger()
                    .withIdentity("Informant Time Trigger", "Informant")
                    .withSchedule(simpleSchedule().withIntervalInMinutes(5))
                    .forJob(jobDetail)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
