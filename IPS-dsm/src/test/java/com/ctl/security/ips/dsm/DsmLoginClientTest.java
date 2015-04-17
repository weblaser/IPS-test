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

    private final String validUsername = "username";
    private final String validPassword = "password";
    private final String validSessionId = "123";
    private final String tenantName = "tenantName";
    private final String invalidUsername = "wrongUsername";
    private final String invalidPassword = "wrongPassword";

    @Mock
    private Manager manager;

    @InjectMocks
    private DsmLogInClient underTest;

    @Test
    public void loginSuccess() throws Exception {
        // Arrange
        when(manager.authenticate(eq(validUsername), eq(validPassword))).thenReturn(validSessionId);
        // Act
        String sessionId = underTest.connectToDSMClient(validUsername, validPassword);
        // Assert
        assertEquals(validSessionId, sessionId);
    }

    @Test(expected = ManagerAuthenticationException_Exception.class)
    public void loginFail() throws Exception{
        //Arrange
        when(manager.authenticate(eq(invalidUsername), eq(invalidPassword))).thenThrow(ManagerAuthenticationException_Exception.class);
        //Act
        underTest.connectToDSMClient(invalidUsername, invalidPassword);
    }

    @Test
    public void endSession_endsSession(){
        underTest.endSession(validSessionId);

        verify(manager).endSession(validSessionId);
    }

    @Test
    public void connectTenantToDSMClient_connectsToDSM() throws Exception {
        // Arrange
        when(manager.authenticateTenant(eq(tenantName), eq(validUsername), eq(validPassword)))
                .thenReturn(validSessionId);
        // Act
        String sessionId = underTest.connectTenantToDSMClient(tenantName, validUsername, validPassword);
        // Assert
        assertEquals(validSessionId, sessionId);
    }
}
