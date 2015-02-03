package com.ctl.security.ips.dsm;

import manager.Manager;
import manager.ManagerAuthenticationException_Exception;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DsmLoginClientTest {

    @Mock
    private Manager manager;

    @InjectMocks
    private DsmLogInClient underTest;

    @Test
    public void loginSuccess() throws Exception {
        // Arrange
        when(manager.authenticate(eq("username"), eq("password"))).thenReturn("123");
        // Act
        String sessionId = underTest.connectToDSMClient("username", "password");
        // Assert
        assertEquals("123", sessionId);
    }

    @Test(expected = ManagerAuthenticationException_Exception.class)
    public void loginFail() throws Exception{
        //Arrange
        when(manager.authenticate(eq("wrongUsername"), eq("wrongPassword"))).thenThrow(ManagerAuthenticationException_Exception.class);
        //Act
        underTest.connectToDSMClient("wrongUsername","wrongPassword");
    }

    @Test
    public void endSession_endsSession(){
        String sessionId = "sessionId";

        underTest.endSession(sessionId);

        verify(manager).endSession(sessionId);
    }
}
