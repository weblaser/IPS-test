package com.ctl.security.dsm.authenticate;

import com.ctl.security.dsm.authenticate.LogInClient;
import manager.Manager;
import manager.ManagerAuthenticationException_Exception;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginClientTest {

    @Mock
    private Manager manager;

    @InjectMocks
    private LogInClient client;

    @Test
    public void loginSuccess() throws Exception {
        // Arrange
        when(manager.authenticate(eq("username"), eq("password"))).thenReturn("123");

        // Act
        String sessionId = client.connectToDSMClient("username", "password");

        // Assert
        assertEquals("123", sessionId);
    }

    @Test(expected = ManagerAuthenticationException_Exception.class)
    public void loginFail() throws Exception{
        //Arrange
        when(manager.authenticate(eq("wrongUsername"), eq("wrongPassword"))).thenThrow(ManagerAuthenticationException_Exception.class);

        //Act
        String sessionId = client.connectToDSMClient("wrongUsername","wrongPassword");
    }
}
