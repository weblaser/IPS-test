package com.ctl.security.ips.maestro.service;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.cmdb.ProductUserActivityClient;
import com.ctl.security.data.client.domain.configurationitem.ConfigurationItemResource;
import com.ctl.security.data.common.domain.mongo.*;
import com.ctl.security.ips.common.domain.Event.DpiEvent;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.library.common.httpclient.CtlSecurityResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventNotifyServiceTest {

    @InjectMocks
    private EventNotifyService classUnderTest;

    @Mock
    private ConfigurationItemClient configurationItemClient;

    @Mock
    private ConfigurationItemResource configurationItemResource;

    @Mock
    private ConfigurationItem configurationItem;

    @Mock
    private Account account;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ResponseEntity responseEntity;

    @Mock
    private CtlSecurityResponse ctlSecurityResponse;

    @Mock
    private ProductUserActivityClient productUserActivityClient;

    @Mock
    private ProductUserActivityService productUserActivityService;

    String hostName = null;
    String accountId = null;

    private Integer maxRetryAttempts = 2;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(classUnderTest, "maxRetryAttempts", maxRetryAttempts);
        ReflectionTestUtils.setField(classUnderTest, "retryWaitTime", 10);
    }

    @Test
    public void notify_notifiesToNotificationDestination() {

        EventBean eventBean = createEventBean(hostName, accountId);

        List<NotificationDestination> notificationDestinations = createNotificationDestinations();

        basicMockitoSetup(notificationDestinations);

        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(responseEntity);

        classUnderTest.notify(eventBean);

        basicMockitoVerification();

        verifyRestTemplateExchange(1, eventBean, notificationDestinations);

        verify(productUserActivityService, times(1)).persistNotification(true,
                configurationItem,
                notificationDestinations.get(0));

    }

    @Test
    public void notify_logsFailedNotificationCausedByStatusCode() {

        EventBean eventBean = createEventBean(hostName, accountId);

        List<NotificationDestination> notificationDestinations = createNotificationDestinations();

        basicMockitoSetup(notificationDestinations);

        classUnderTest.notify(eventBean);

        basicMockitoVerification();

        verifyRestTemplateExchange(maxRetryAttempts, eventBean, notificationDestinations);

        verify(productUserActivityService, times(maxRetryAttempts - 1)).persistNotification(false,
                configurationItem,
                notificationDestinations.get(0));
    }

    @Test
    public void notify_logsExceptionNotificationCausedByException(){

        EventBean eventBean = createEventBean(hostName, accountId);

        List<NotificationDestination> notificationDestinations = createNotificationDestinations();

        basicMockitoSetup(notificationDestinations);

        classUnderTest.notify(eventBean);

        basicMockitoVerification();

        verify(productUserActivityService, times(maxRetryAttempts - 1)).persistNotification(false,
                configurationItem,
                notificationDestinations.get(0));

    }



    private void verifyRestTemplateExchange(Integer amountOfTimes, EventBean eventBean, List<NotificationDestination> notificationDestinations) {
        for (NotificationDestination notificationDestination : notificationDestinations) {
//            verify(ctlSecurityClient.post(anyString()).body(anyString()), times(amountOfTimes)).execute();
            verify(restTemplate, times(amountOfTimes)).exchange(notificationDestination.getUrl(),
                HttpMethod.POST, new HttpEntity<DpiEvent>(eventBean.getEvent()), String.class);
        }
    }

    private void basicMockitoVerification() {
        verify(configurationItemClient).getConfigurationItem(hostName, accountId);
        verify(configurationItemResource).getContent();
        verify(configurationItem).getAccount();
        verify(account).getNotificationDestinations();
    }

    private void basicMockitoSetup(List<NotificationDestination> notificationDestinations) {
        when(configurationItemClient.getConfigurationItem(hostName, accountId))
                .thenReturn(configurationItemResource);
        when(configurationItemResource.getContent())
                .thenReturn(configurationItem);
        when(configurationItem.getAccount())
                .thenReturn(account);
        when(account.getNotificationDestinations())
                .thenReturn(notificationDestinations);
    }

    private EventBean createEventBean(String hostName, String accountId) {
        DpiEvent event = new DpiEvent();
        event.setReason("New Reason");
        event.setHostName("New HostName");
        return new EventBean(hostName, accountId, event);
    }

    private List<NotificationDestination> createNotificationDestinations() {
        NotificationDestination notificationDestination = new NotificationDestination();
        notificationDestination.setUrl("http://localhost:8080/test");
        notificationDestination.setTypeCode(NotificationDestinationType.WEBHOOK);
        notificationDestination.setIntervalCode(NotificationDestinationInterval.DAILY);

        return Arrays.asList(notificationDestination);
    }


}