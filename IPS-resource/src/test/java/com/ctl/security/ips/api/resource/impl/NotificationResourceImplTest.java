package com.ctl.security.ips.api.resource.impl;

import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import com.ctl.security.ips.api.jms.NotificationMessageSender;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NotificationResourceImplTest {

    @InjectMocks
    private NotificationResourceImpl classUnderTest;

    @Mock
    private NotificationMessageSender notificationMessageSender;

    String hostName;
    String accountId;

    @Test
    public void updateNotificationDestination_updatesNotificationDestination(){
        NotificationDestination notificationDestination = null;
        List<NotificationDestination> notificationDestinations = Arrays.asList(notificationDestination);

        classUnderTest.updateNotificationDestination(accountId,hostName,notificationDestinations);

        verify(notificationMessageSender).updateNotificationDestination(new NotificationDestinationBean(hostName, accountId, notificationDestinations));
    }

    @Test
    public void testDeleteNotificationDestination(){
        //arrange
        //act
        classUnderTest.deleteNotificationDestination(accountId, hostName);
        //assert
        verify(notificationMessageSender).deleteNotificationDestination(new NotificationDestinationBean(hostName, accountId, null));
    }

}