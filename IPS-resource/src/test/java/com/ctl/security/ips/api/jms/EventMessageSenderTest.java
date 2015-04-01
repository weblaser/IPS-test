package com.ctl.security.ips.api.jms;

import com.ctl.security.ips.common.jms.EventOperation;
import com.ctl.security.ips.common.jms.bean.EventBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EventMessageSenderTest {

    @InjectMocks
    EventMessageSender classUnderTest;

    @Mock
    private JmsTemplate jmsTemplate;

    @Test
    public void testNotify() throws Exception {
        EventBean eventBean = null;

        classUnderTest.notify(eventBean);

        verify(jmsTemplate).convertAndSend(EventOperation.EVENT_OCCURRED,eventBean);
    }
}