package com.ctl.security.dsm.config;

import manager.*;
import org.apache.log4j.Logger;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by kevin.wilde on 1/21/2015.
 */
@Configuration
@Profile({"local", "dev"})
@ComponentScan("com.ctl.security.dsm")
public class LocalDsmBeans {

    private static final Logger logger = Logger.getLogger(ProdDsmBeans.class);

    @Mock
    private Manager manager;

    public LocalDsmBeans(){
        MockitoAnnotations.initMocks(this);
    }

    @Bean
    public Manager manager() throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthorizationException_Exception, ManagerTimeoutException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception {

        String sessionId = "123";
        when(manager.authenticate(anyString(), anyString())).thenReturn(sessionId);
        SecurityProfileTransport expectedSecurityProfileTransport = new SecurityProfileTransport();
        when(manager.securityProfileSave(any(SecurityProfileTransport.class), eq(sessionId))).thenReturn(expectedSecurityProfileTransport);

        return manager;

    }

}
