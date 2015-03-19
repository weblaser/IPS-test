package com.ctl.security.ips.maestro.service;

import com.ctl.security.data.client.cmdb.ConfigurationItemClient;
import com.ctl.security.data.client.domain.configurationitem.ConfigurationItemResource;
import com.ctl.security.data.common.domain.mongo.*;
import com.ctl.security.ips.common.domain.Event;
import com.ctl.security.ips.common.jms.bean.EventBean;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
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
    private RestTemplate restTemplate;

    @Mock
    private Logger logger;

    @Mock
    private ResponseEntity<String> responseEntity;

    private HttpStatus httpStatus;

    String hostName = null;
    String accountId = null;

    private Integer maxRetryAttempts=2;

    @Before
    public void setup(){
        ReflectionTestUtils.setField(classUnderTest, "maxRetryAttempts", maxRetryAttempts);
        ReflectionTestUtils.setField(classUnderTest, "retryWaitTime", 10);
    }

    @Test
    public void notify_notifiesToNotificationDestination(){
        final TestAppender appender = new TestAppender();
        final Logger logger = Logger.getRootLogger();

        EventBean eventBean = createEventBean(hostName, accountId);

        List<NotificationDestination> notificationDestinations = createNotificationDestinations();

        basicMockitoSetup(notificationDestinations);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);
        when(responseEntity.getStatusCode())
                .thenReturn(HttpStatus.ACCEPTED);

        logger.addAppender(appender);
        classUnderTest.notify(eventBean);
        logger.removeAppender(appender);

        basicMockitoVerification();

        verifyRestTemplateExchange(1, eventBean,notificationDestinations);

         assertEquals(true,responseEntity.getStatusCode().is2xxSuccessful());

        final List<LoggingEvent> log = appender.getLog();
        assertEquals((Integer)0, (Integer) log.size());
    }

    @Test
    public void notify_logsFailedNotificationCausedByStatusCode(){
        final TestAppender appender = new TestAppender();
        final Logger logger = Logger.getRootLogger();

        EventBean eventBean = createEventBean(hostName, accountId);

        List<NotificationDestination> notificationDestinations = createNotificationDestinations();

        basicMockitoSetup(notificationDestinations);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);
        when(responseEntity.getStatusCode())
                .thenReturn(HttpStatus.I_AM_A_TEAPOT);

        logger.addAppender(appender);
        classUnderTest.notify(eventBean);
        logger.removeAppender(appender);

        basicMockitoVerification();

        assertEquals(false,responseEntity.getStatusCode().is2xxSuccessful());

        verifyRestTemplateExchange(maxRetryAttempts, eventBean, notificationDestinations);

        final List<LoggingEvent> log = appender.getLog();

        for(int i=1; i < maxRetryAttempts - 1; i++) {
            assertEquals(Level.INFO,log.get(i).getLevel());
        }
        assertEquals(Level.ERROR,log.get(maxRetryAttempts).getLevel());
        assertEquals((Integer)(maxRetryAttempts + 1), (Integer) log.size());
    }

    @Test
    public void notify_logsExceptionNotificationCausedByException(){
        final TestAppender appender = new TestAppender();
        final Logger logger = Logger.getRootLogger();

        EventBean eventBean = createEventBean(hostName, accountId);

        List<NotificationDestination> notificationDestinations = createNotificationDestinations();

        basicMockitoSetup(notificationDestinations);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("test"));

        logger.addAppender(appender);
        classUnderTest.notify(eventBean);
        logger.removeAppender(appender);

        basicMockitoVerification();

        verifyRestTemplateExchange(maxRetryAttempts, eventBean, notificationDestinations);

        assertNotNull(responseEntity);
        assertNull(responseEntity.getStatusCode());

        final List<LoggingEvent> log = appender.getLog();

        for(int i=1; i < maxRetryAttempts - 1; i++) {
            assertEquals(Level.INFO,log.get(i).getLevel());
        }
        assertEquals(Level.ERROR,log.get(maxRetryAttempts).getLevel());
        assertEquals((Integer)(maxRetryAttempts + 1),(Integer)log.size());
    }

    private void verifyRestTemplateExchange(Integer amountOfTimes, EventBean eventBean, List<NotificationDestination> notificationDestinations) {
        for (NotificationDestination notification : notificationDestinations)
        {
            verify(restTemplate, times(amountOfTimes)).exchange(eq(notification.getUrl()),
                    eq(HttpMethod.POST),
                    eq(new HttpEntity<>(eventBean.getEvent())),
                    eq(String.class));
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
        Event event=new Event();
        event.setMessage("New Message");
        return new EventBean(hostName,accountId,event);
    }

    private List<NotificationDestination> createNotificationDestinations() {
        NotificationDestination notificationDestination=new NotificationDestination();
        notificationDestination.setUrl("http://localhost:8080/test");
        notificationDestination.setTypeCode(NotificationDestinationType.WEBHOOK);
        notificationDestination.setIntervalCode(NotificationDestinationInterval.DAILY);

        return Arrays.asList(notificationDestination);
    }

    class TestAppender extends AppenderSkeleton {
        private final List<LoggingEvent> log = new ArrayList<LoggingEvent>();

        @Override
        public boolean requiresLayout() {
            return false;
        }

        @Override
        protected void append(final LoggingEvent loggingEvent) {
            log.add(loggingEvent);
        }

        @Override
        public void close() {
        }

        public List<LoggingEvent> getLog() {
            return new ArrayList<LoggingEvent>(log);
        }
    }

}