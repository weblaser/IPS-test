package com.ctl.security.ips.api.jms;

import com.ctl.security.ips.common.jms.NotificationOperation;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NotificationMessageSenderTest {

    @InjectMocks
    private NotificationMessageSender classUnderTest;

    @Mock
    private JmsTemplate jmsTemplate;

    @Test
    public void testUpdateNotificationDestination() throws Exception {
            NotificationDestinationBean notificationDestinationBean = null;

            classUnderTest.updateNotificationDestination(notificationDestinationBean);

            verify(jmsTemplate).convertAndSend(NotificationOperation.UPDATE_NOTIFICATION_DESTINATION_FOR_SERVER, notificationDestinationBean);
    }
}
