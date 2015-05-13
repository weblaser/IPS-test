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
    private static final String BEARER_TOKEN = "token";
    private static final String HOSTNAME = "hostName";
    private static final String ACCOUNT_ID = "accountId";
    private static final String URL = "exampleUrl";
    private static final NotificationDestination NOTIFICATION_DESTINATION = new NotificationDestination().setUrl(URL);
    private static final List<NotificationDestination> NOTIFICATION_DESTINATIONS = Arrays.asList(NOTIFICATION_DESTINATION);
    private NotificationDestinationBean notificationDestinationBean;

    @Test
    public void updateNotificationDestination_updatesNotificationDestination(){
        notificationDestinationBean = new NotificationDestinationBean(HOSTNAME, ACCOUNT_ID, NOTIFICATION_DESTINATIONS);

        String hostUrl = "hostUrlValueForTest";
        ReflectionTestUtils.setField(classUnderTest, "hostUrl", hostUrl);
        String address = hostUrl + NotificationClient.NOTIFICATIONS + "/" + notificationDestinationBean.getAccountId() + "/" + notificationDestinationBean.getHostName();

        httpHeaders.add("test", "test");
        when(clientComponent.createHeaders(BEARER_TOKEN)).thenReturn(httpHeaders);


        classUnderTest.updateNotificationDestination(notificationDestinationBean, BEARER_TOKEN);


        verify(restTemplate).exchange(address,
                HttpMethod.PUT, new HttpEntity<>(notificationDestinationBean.getNotificationDestinations(), httpHeaders), String.class);
    }

    @Test
    public void testDeleteNotificationDestination(){
        //arrange
        notificationDestinationBean = new NotificationDestinationBean(HOSTNAME, ACCOUNT_ID, NOTIFICATION_DESTINATIONS);
        String hostUrl = "hostUrlValueForTest";
        ReflectionTestUtils.setField(classUnderTest, "hostUrl", hostUrl);
        String address = hostUrl + NotificationClient.NOTIFICATIONS + "/" + notificationDestinationBean.getAccountId() + "/" + notificationDestinationBean.getHostName();

        when(clientComponent.createHeaders(BEARER_TOKEN)).thenReturn(httpHeaders);
        //act
        classUnderTest.deleteNotificationDestination(notificationDestinationBean, BEARER_TOKEN);
        //assert
        verify(restTemplate).exchange(address, HttpMethod.DELETE, new HttpEntity<>(httpHeaders), String.class);
    }
}