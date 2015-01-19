package com.ctl.security.dsm.authenticate;

import manager.Manager;
import manager.SecurityProfileTransport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by Chad.Middleton on 1/15/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class DsmPolicyClientTest {

    @Mock
    private Manager manager;

    @Mock
    private LogInClient logInClient;

    @InjectMocks
    private DsmPolicyClient classUnderTest;

    @Test
    public void createPolicyOnDSMClientTestSuccess() throws Exception {
        //arrange
        SecurityProfileTransport transport = new SecurityProfileTransport();
        when(logInClient.connectToDSMClient(eq("joe"), eq("password"))).thenReturn("12345");
        when(manager.securityProfileSave(any(SecurityProfileTransport.class), eq("12345"))).thenReturn(transport);
        //act
        SecurityProfileTransport actual = classUnderTest.createPolicyOnDSMClient("joe", "password", new SecurityProfileTransport());
        //assert
        assertEquals(transport, actual);
    }
//
//    @Test (expected = Exception.class)
//    public void createPolicyOnDSMClientTestFail()throws Exception{
//        //arrange
//        SecurityProfileTransport transport = new SecurityProfileTransport();
//        when(logInClient.connectToDSMClient(eq("joe"), eq("password"))).thenReturn("12345");
//        when(manager.securityProfileSave(any(SecurityProfileTransport.class), eq("12345"))).thenThrow(Exception.class);
//        //act
//        SecurityProfileTransport actual = underTest.createPolicyOnDSMClient("joe", "12345", new SecurityProfileTransport());
//        //assert
//    }

}
