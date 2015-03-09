package com.ctl.security.ips.maestro.service;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.domain.configurationitem.ConfigurationItemResource;
import com.ctl.security.data.common.domain.mongo.*;
import com.ctl.security.ips.common.domain.Event;
import com.ctl.security.ips.common.jms.bean.EventBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventNotifyServiceTest {

    @InjectMocks
    EventNotifyService classUnderTest;

    @Mock
    private ConfigurationItemClient configurationItemClient;

    @Mock
    private ConfigurationItemResource configurationItemResource;

    @Mock
    private ConfigurationItem configurationItem;

    @Mock
    private Account account;

    @Mock
    private List<NotificationDestination> notificationDestinations;

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void notify_notifiesToNotificationDestination() throws Exception {
        String hostName = null;
        String accountId = null;
        Event event=new Event("New Message");
        EventBean eventBean=new EventBean(hostName,accountId,event);

        NotificationDestination notificationDestination=new NotificationDestination();
        notificationDestination.setUrl("http://localhost:8080/test");
        notificationDestination.setTypeCode(NotificationDestinationType.WEBHOOK);
        notificationDestination.setIntervalCode(NotificationDestinationInterval.DAILY);

        notificationDestinations = Arrays.asList(notificationDestination);

        when(configurationItemClient.getConfigurationItem(hostName, accountId)).thenReturn(configurationItemResource);
        when(configurationItemResource.getContent()).thenReturn(configurationItem);
        when(configurationItem.getAccount()).thenReturn(account);
        when(account.getNotificationDestinations()).thenReturn(notificationDestinations);

        classUnderTest.notify(eventBean);

        verify(configurationItemClient).getConfigurationItem(hostName, accountId);
        verify(configurationItemResource).getContent();
        verify(configurationItem).getAccount();
        verify(account).getNotificationDestinations();

        for (int notificationDestinationIndex = 0;
             notificationDestinationIndex < notificationDestinations.size();
             notificationDestinationIndex++)
        {
            verify(restTemplate).exchange(notificationDestinations.get(notificationDestinationIndex).getUrl(),
                HttpMethod.POST, new HttpEntity<>(eventBean.getEvent().getMessage()), String.class);
        }
    }
}