package com.ctl.security.ips.informant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@RunWith(MockitoJUnitRunner.class)
public class InformantSchedulerTest {

    @InjectMocks
    private InformantScheduler classUnderTest;

    @Mock
    private StdSchedulerFactory stdSchedulerFactory;

    @Mock
    private Scheduler scheduler;

    @Test
    public void start_schedulesInformant() throws Exception {
        JobDetail jobDetail = newJob(Informant.class)
                .withIdentity("Informant Job", "Informant")
                .build();
        Trigger trigger = newTrigger()
                .withIdentity("Informant Time Trigger", "Informant")
                .withSchedule(simpleSchedule().withIntervalInMinutes(5))
                .forJob(jobDetail)
                .build();

        when(stdSchedulerFactory.getScheduler())
                .thenReturn(scheduler);

        classUnderTest.run();

        verify(stdSchedulerFactory).getScheduler();
        verify(scheduler).scheduleJob(jobDetail, trigger);
    }
}