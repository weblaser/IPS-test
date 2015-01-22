package com.ctl.security.dsm.config;

import manager.Manager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Chad.Middleton on 1/14/2015.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProdWebConfigTest {

    @InjectMocks
    Prod classUnderTest;

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
