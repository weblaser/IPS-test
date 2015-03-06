package com.ctl.security.ips.maestro.jms;

import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import com.ctl.security.ips.maestro.service.NotificationServiceWrite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NotificationListenerTest {

    @InjectMocks
    private NotificationListener classUnderTest;

    @Mock
    private NotificationServiceWrite notificationServiceWrite;

    @Test
    public void testUpdateNotificationDestination() throws Exception {
        NotificationDestinationBean notificationDestinationBean = null;

        classUnderTest.updateNotificationDestination(notificationDestinationBean);

        verify(notificationServiceWrite).updateNotificationDestination(notificationDestinationBean);
    }
}