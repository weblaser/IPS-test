package com.ctl.security.ips.dsm.config;

import manager.Manager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Chad.Middleton on 1/14/2015.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProdWebConfigTest {

    @InjectMocks
    DsmBeans classUnderTest;

    @Mock
    private Manager manager;


    @Test
    public void managerTestPass(){
        //arrange
        //act
        Manager actual = classUnderTest.manager();
        //assert
        assertNotNull(actual);
    }

}
