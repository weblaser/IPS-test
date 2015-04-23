package com.ctl.security.ips.maestro.jms;

import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.maestro.service.EventNotifyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EventListenerTest {

    @InjectMocks
    private EventListener classUnderTest;

    @Mock
    private EventNotifyService eventNotifyService;

    @Test
    public void testNotify() throws Exception {
        EventBean eventBean = null;
        classUnderTest.notify(eventBean);
        verify(eventNotifyService).notify(eventBean);
    }
}