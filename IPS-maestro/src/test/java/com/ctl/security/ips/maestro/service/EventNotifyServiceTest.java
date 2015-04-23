package com.ctl.security.ips.maestro.service;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.cmdb.ProductUserActivityClient;
import com.ctl.security.data.client.domain.configurationitem.ConfigurationItemResource;
import com.ctl.security.data.common.domain.mongo.*;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.library.common.httpclient.CtlSecurityClient;
import com.ctl.security.library.common.httpclient.CtlSecurityResponse;
import com.ctl.security.library.test.TestAppender;
import org.apache.http.HttpStatus;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CtlSecurityClient ctlSecurityClient;

    @Mock
    private CtlSecurityResponse ctlSecurityResponse;

    @Mock
    private ProductUserActivityClient productUserActivityClient;

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
        final TestAppender appender = new TestAppender();
        final Logger logger = Logger.getRootLogger();

        EventBean eventBean = createEventBean(hostName, accountId);

        List<NotificationDestination> notificationDestinations = createNotificationDestinations();

        basicMockitoSetup(notificationDestinations);

        when(ctlSecurityClient.post(anyString()).body(anyString()).execute())
                .thenReturn(ctlSecurityResponse);
        when(ctlSecurityResponse.isSuccessful())
                .thenReturn(true);

        logger.addAppender(appender);
        classUnderTest.notify(eventBean);
        logger.removeAppender(appender);

        basicMockitoVerification();

        verifyRestTemplateExchange(1, eventBean, notificationDestinations);

        final List<LoggingEvent> log = appender.getLog();
        assertEquals(Level.INFO, log.get(0).getLevel());
        assertEquals((Integer) 1, (Integer) log.size());
    }

    @Test
    public void notify_logsFailedNotificationCausedByStatusCode() {
        final TestAppender appender = new TestAppender();
        final Logger logger = Logger.getRootLogger();

        EventBean eventBean = createEventBean(hostName, accountId);

        List<NotificationDestination> notificationDestinations = createNotificationDestinations();

        basicMockitoSetup(notificationDestinations);

        when(ctlSecurityClient.post(anyString()).body(anyString()).execute())
                .thenReturn(ctlSecurityResponse);
        when(ctlSecurityResponse.isSuccessful())
                .thenReturn(false);

        logger.addAppender(appender);
        classUnderTest.notify(eventBean);
        logger.removeAppender(appender);

        basicMockitoVerification();

        verifyRestTemplateExchange(maxRetryAttempts, eventBean, notificationDestinations);

        final List<LoggingEvent> log = appender.getLog();

        assertEquals(Level.ERROR, log.get(0).getLevel());
        assertEquals(1, log.size());
    }

    @Test
    public void notify_logsExceptionNotificationCausedByException(){
        final TestAppender appender = new TestAppender();
        final Logger logger = Logger.getRootLogger();

        EventBean eventBean = createEventBean(hostName, accountId);

        List<NotificationDestination> notificationDestinations = createNotificationDestinations();

        basicMockitoSetup(notificationDestinations);

        when(ctlSecurityClient.post(anyString())).thenThrow(new RestClientException("test"));

        logger.addAppender(appender);
        classUnderTest.notify(eventBean);
        logger.removeAppender(appender);

        basicMockitoVerification();

        final List<LoggingEvent> log = appender.getLog();

        for(int i=1; i < maxRetryAttempts - 1; i++) {
            assertEquals(Level.INFO,log.get(i).getLevel());
        }
        assertEquals(Level.ERROR,log.get(maxRetryAttempts).getLevel());
        assertEquals((Integer)(maxRetryAttempts + 1),(Integer)log.size());
    }

    @Test
    public void notify_persistsSuccessfulNotification(){
        EventBean eventBean = createEventBean(hostName, accountId);
        List<NotificationDestination> notificationDestinations = createNotificationDestinations();

        basicMockitoSetup(notificationDestinations);

        when(ctlSecurityClient.post(anyString()).body(anyString()).execute()).thenReturn(ctlSecurityResponse);
        when(ctlSecurityResponse.isSuccessful())
                .thenReturn(true);

        classUnderTest.notify(eventBean);

        ProductUserActivity productUserActivity = new ProductUserActivity();

        productUserActivity.setConfigurationItem(configurationItem);
        productUserActivity.setDescription(EventNotifyService.SUCCESSFULLY_SENT_NOTIFICATION_TO + notificationDestinations.get(0).getUrl());

        verify(productUserActivityClient).createProductUserActivity(productUserActivity);
    }

    @Test
    public void notify_persistsFailedNotification(){
        EventBean eventBean = createEventBean(hostName, accountId);
        List<NotificationDestination> notificationDestinations = createNotificationDestinations();

        basicMockitoSetup(notificationDestinations);

        when(ctlSecurityClient.post(anyString()).body(anyString()).execute()).thenReturn(ctlSecurityResponse);
        when(ctlSecurityResponse.isSuccessful())
                .thenReturn(false);

        classUnderTest.notify(eventBean);

        ProductUserActivity productUserActivity = new ProductUserActivity();

        productUserActivity.setConfigurationItem(configurationItem);
        productUserActivity.setDescription(EventNotifyService.FAILED_TO_SEND_NOTIFICATION_TO + notificationDestinations.get(0).getUrl());

        verify(productUserActivityClient).createProductUserActivity(productUserActivity);
    }

    private void verifyRestTemplateExchange(Integer amountOfTimes, EventBean eventBean, List<NotificationDestination> notificationDestinations) {
        for (NotificationDestination notification : notificationDestinations) {
            verify(ctlSecurityClient.post(anyString()).body(anyString()), times(amountOfTimes)).execute();
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
        FirewallEvent event = new FirewallEvent();
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