package com.ctl.security.ips.client.config;

import org.junit.Test;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Kevin.Weber on 10/29/2014.
 */
public class ClientConfigTest {

    ClientConfig underTest = new ClientConfig();

    @Test
    public void testRestTemplate() {
        //act
        RestTemplate actual = underTest.restTemplate();
        //assert
        assertNotNull(actual);
    }
}
