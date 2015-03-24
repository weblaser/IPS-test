package com.ctl.security.ips.informant.cofig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.impl.StdSchedulerFactory;
import static org.junit.Assert.*;

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