package com.ctl.security.ips.client;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.domain.configurationitem.ConfigurationItemResource;
import com.ctl.security.data.common.domain.mongo.Account;
import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import com.ctl.security.ips.common.jms.bean.NotificationDestinationBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationClientTest {

    @InjectMocks
    private NotificationClient classUnderTest;

    @Mock
    private ConfigurationItemClient configurationItemClient;

    @Mock
    private ConfigurationItemResource configurationItemResource;

    @Mock
    private ConfigurationItem configurationItem;

    @Mock
    private Account account;

    @Mock
    private ClientComponent clientComponent;

    @Mock
    private RestTemplate restTemplate;

    private HttpHeaders httpHeaders = new HttpHeaders();

    @Test
    public void updateNotificationDestination_updatesNotificationDestination(){

        String hostName = null;
        String accountId = null;
        NotificationDestination notificationDestination = null;
        List<NotificationDestination> notificationDestinations = Arrays.asList(notificationDestination);
        NotificationDestinationBean notificationDestinationBean = new NotificationDestinationBean(hostName, accountId, notificationDestinations);

        when(configurationItem.getAccount()).thenReturn(account);
        when(configurationItemClient.getConfigurationItem(hostName, accountId)).thenReturn(configurationItemResource);
        when(configurationItemResource.getContent()).thenReturn(configurationItem);

        String hostUrl = "hostUrlValueForTest";
        ReflectionTestUtils.setField(classUnderTest, "hostUrl", hostUrl);
        String address = hostUrl + NotificationClient.NOTIFICATIONS + "/" + notificationDestinationBean.getAccountId() + "/" + notificationDestinationBean.getHostName();

        String bearerToken = null;
        httpHeaders.add("test", "test");
        when(clientComponent.createHeaders(bearerToken)).thenReturn(httpHeaders);


        classUnderTest.updateNotificationDestination(notificationDestinationBean, bearerToken);


        verify(restTemplate).exchange(address,
                HttpMethod.PUT, new HttpEntity<>(notificationDestinationBean.getNotificationDestinations(), httpHeaders), String.class);
    }

}