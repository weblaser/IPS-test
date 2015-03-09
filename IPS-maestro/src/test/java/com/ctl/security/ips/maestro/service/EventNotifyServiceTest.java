package com.ctl.security.ips.maestro.service;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.domain.configurationitem.ConfigurationItemResource;
import com.ctl.security.data.common.domain.mongo.Account;
import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import com.ctl.security.ips.common.jms.bean.EventBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

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

    private HttpHeaders httpHeaders = new HttpHeaders();

    @Test
    public void notify_notifiesToNotificationDestination() throws Exception {
        String hostName = null;
        String accountId = null;
        EventBean eventBean=new EventBean(hostName,accountId);

        when(configurationItemClient.getConfigurationItem(hostName,accountId)).thenReturn(configurationItemResource);
        when(configurationItemResource.getContent()).thenReturn(configurationItem);
        when(configurationItem.getAccount()).thenReturn(account);
        when(account.getNotificationDestinations()).thenReturn(notificationDestinations);

        String bearerToken = null;
        httpHeaders.add("test", "test");
        when(clientComponent.createHeaders(bearerToken)).thenReturn(httpHeaders);

        classUnderTest.notify(eventBean);

        verify(configurationItemClient).getConfigurationItem(hostName, accountId);
        verify(configurationItemResource).getContent();
        verify(configurationItem).getAccount();
        verify(account).getNotificationDestinations();


        for (int notificationDestinationIndex=0; notificationDestinationIndex<account.getNotificationDestinations().size();notificationDestinationIndex++)
        {
            verify(restTemplate).exchange(account.getNotificationDestinations().get(notificationDestinationIndex).getUrl(),
                HttpMethod.PUT, new HttpEntity<>(eventBean.getEvent().getMessage(), httpHeaders), String.class);
        }
    }
}