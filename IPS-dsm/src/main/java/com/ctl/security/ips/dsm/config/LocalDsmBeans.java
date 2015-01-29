package com.ctl.security.ips.dsm.config;

import manager.*;
import org.apache.log4j.Logger;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by kevin.wilde on 1/21/2015.
 */
@Configuration
@Profile({"local", "dev"})
@ComponentScan("com.ctl.security.dsm")
public class LocalDsmBeans {

    private static final Logger logger = Logger.getLogger(ProdDsmBeans.class);

    public static final String APIUSER = "apiuser";
    public static final String PASSWORD_CORRECT = "trejachad32jUgEs";
    public static final String APIUSER_WRONG = "wrong";
    public static final String PASSWORD_WRONG = "wrong";

    @Mock
    private Manager manager;

    public LocalDsmBeans() {
        MockitoAnnotations.initMocks(this);
    }

    @Bean
    public Manager manager() throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthorizationException_Exception, ManagerTimeoutException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception {

        String sessionId = "123";
        Mockito.when(manager.authenticate(APIUSER, PASSWORD_CORRECT)).thenReturn(sessionId);

        Mockito.when(manager.authenticate(APIUSER, PASSWORD_WRONG)).thenThrow(ManagerAuthenticationException_Exception.class);
        Mockito.when(manager.authenticate(APIUSER_WRONG, PASSWORD_CORRECT)).thenThrow(ManagerAuthenticationException_Exception.class);

        SecurityProfileTransport expectedSecurityProfileTransport = new SecurityProfileTransport();
        Mockito.when(manager.securityProfileSave(Matchers.any(SecurityProfileTransport.class), Matchers.eq(sessionId))).thenReturn(expectedSecurityProfileTransport);

        return manager;

    }

}
