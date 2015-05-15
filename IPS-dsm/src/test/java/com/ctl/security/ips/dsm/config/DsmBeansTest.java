package com.ctl.security.ips.dsm.config;

import manager.Manager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Chad.Middleton on 1/14/2015.
 */

@RunWith(MockitoJUnitRunner.class)
public class DsmBeansTest {

    @InjectMocks
    private DsmBeans classUnderTest;

    @Mock
    private Manager manager;

    private final String protocol = "http://";
    private final String host = "localhost";
    private final String port = "8080";


    @Before
    public void before() {

        ReflectionTestUtils.setField(classUnderTest, "protocol", protocol);
        ReflectionTestUtils.setField(classUnderTest, "host", host);
        ReflectionTestUtils.setField(classUnderTest, "port", port);

    }

    @Test
    public void managerTestPass(){
        //arrange
        //act
        Manager actual = classUnderTest.manager();
        //assert
        assertNotNull(actual);
    }

}
