package com.ctl.security.ips.api.resource.impl;

import com.ctl.security.ips.api.jms.EventMessageSender;
import com.ctl.security.ips.common.domain.Event.DpiEvent;
import com.ctl.security.ips.common.jms.bean.EventBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EventResourceImplTest {

    @InjectMocks
    EventResourceImpl classUnderTest;

    @Mock
    EventMessageSender eventMessageSender;

    @Test
    public void testNotify() throws Exception {
        String hostName = null;
        String accountId = null;
        DpiEvent event=null;

        classUnderTest.notify(hostName,accountId,event);

        verify(eventMessageSender).notify(new EventBean(hostName,accountId,event));
    }
}