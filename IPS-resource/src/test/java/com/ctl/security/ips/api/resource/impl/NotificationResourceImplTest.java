package com.ctl.security.ips.api.resource.impl;

import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import com.ctl.security.ips.service.NotificationServiceWrite;
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
    private NotificationServiceWrite notificationServiceWrite;

    @Test
    public void updateNotificationDestination_updatesNotificationDestination(){
        String hostName = null;
        String accountId = null;
        NotificationDestination notificationDestination = null;
        List<NotificationDestination> notificationDestinations = Arrays.asList(notificationDestination);

        classUnderTest.updateNotificationDestination(accountId,hostName,notificationDestinations);

        verify(notificationServiceWrite).updateNotificationDestination(new NotificationDestinationBean(hostName, accountId, notificationDestinations));
    }

}