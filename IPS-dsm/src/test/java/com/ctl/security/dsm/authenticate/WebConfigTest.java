package com.ctl.security.dsm.authenticate;

import com.ctl.security.dsm.LogInClient;
import com.ctl.security.dsm.WebConfig;
import manager.Manager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.MalformedURLException;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Chad.Middleton on 1/14/2015.
 */

@RunWith(MockitoJUnitRunner.class)
public class WebConfigTest {

    @InjectMocks
    WebConfig classUnderTest;

    @Mock
    private Manager manager;

    @Test
    public void clientTest() throws MalformedURLException {
        //arrange
        //act
        LogInClient actual = classUnderTest.logInClient(manager);
        //assert
        assertNotNull(actual);
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
