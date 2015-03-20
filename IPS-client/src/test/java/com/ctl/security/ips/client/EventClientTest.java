package com.ctl.security.ips.client;

import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.ctl.security.ips.common.exception.IpsException;
import com.ctl.security.ips.common.jms.bean.EventBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventClientTest {

    @InjectMocks
    private EventClient classUnderTest;

    @Mock
    private ClientComponent clientComponent;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ResponseEntity<String> responseEntity;

    private HttpHeaders httpHeaders = new HttpHeaders();

    @Test
    public void notify_notifiesAnEventOccurred() throws Exception {
        String hostName = null;
        String accountId = null;
        FirewallEvent event = new FirewallEvent();
        event.setReason("This is My Reason");
        event.setHostName("This is My Host Name");
        EventBean eventBean = new EventBean(hostName,accountId,event);

        String hostUrl = "hostUrlValueForTest";
        ReflectionTestUtils.setField(classUnderTest, "hostUrl", hostUrl);
        String address = hostUrl + EventClient.EVENT +"/"+ eventBean.getAccountId()+"/"+eventBean.getHostName();

        String bearerToken = null;
        httpHeaders.add("test", "test");
        when(clientComponent.createHeaders(bearerToken))
                .thenReturn(httpHeaders);

        //arrange
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);

        classUnderTest.notify(eventBean,bearerToken);

        verify(restTemplate).exchange(address,
                HttpMethod.POST, new HttpEntity<>(eventBean.getEvent(), httpHeaders), String.class);
    }


    @Test(expected = IpsException.class)
    public void notify_failsToPutOnNotificationDestination() {
        //arrange
        String hostName = null;
        String accountId = null;
        FirewallEvent event = new FirewallEvent();
        event.setReason("This is My Reason");
        event.setHostName("This is My Host Name");
        EventBean eventBean = new EventBean(hostName,accountId,event);

        String hostUrl = "hostUrlValueForTest";
        ReflectionTestUtils.setField(classUnderTest, "hostUrl", hostUrl);

        String bearerToken = null;
        httpHeaders.add("test", "test");
        when(clientComponent.createHeaders(bearerToken))
                .thenReturn(httpHeaders);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        when(responseEntity.getStatusCode())
                .thenReturn(HttpStatus.I_AM_A_TEAPOT);

        //act
        classUnderTest.notify(eventBean, bearerToken);
    }
}