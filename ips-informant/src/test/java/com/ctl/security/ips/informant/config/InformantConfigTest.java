package com.ctl.security.ips.informant.config;

import com.ctl.security.ips.informant.InformantConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.impl.StdSchedulerFactory;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class InformantConfigTest {

    @InjectMocks
    private InformantConfig classUnderTest;

    @Test
    public void stdSchedulerFactory_instantiatesStdSchedulerFactory(){
        StdSchedulerFactory stdSchedulerFactory = classUnderTest.stdSchedulerFactory();

        assertNotNull(stdSchedulerFactory);
    }
}