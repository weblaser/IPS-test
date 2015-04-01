package com.ctl.security.ips.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ClientComponentTest {
    
    @InjectMocks
    private ClientComponent classUnderTest;
    
    @Test
    public void createHeaders_createsHeaders(){
        String bearerToken = null;

        HttpHeaders headers = classUnderTest.createHeaders(bearerToken);

        assertNotNull(headers);
        assertNotNull(headers.get(ClientComponent.AUTHORIZATION));
        assertEquals(headers.get(ClientComponent.AUTHORIZATION).get(0), ClientComponent.BEARER + bearerToken);
    }

}