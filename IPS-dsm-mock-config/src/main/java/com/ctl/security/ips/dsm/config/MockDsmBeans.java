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

import static org.mockito.Mockito.when;

/**
 * Created by kevin.wilde on 1/21/2015.
 */
@Configuration
@Profile({"local", "dev"})
@ComponentScan("com.ctl.security.dsm")
public class MockDsmBeans {

    private static final Logger logger = Logger.getLogger(MockDsmBeans.class);



    @Mock
    private Manager manager;

    public MockDsmBeans() {
        MockitoAnnotations.initMocks(this);
    }

    @Bean
    public Manager manager() throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthorizationException_Exception, ManagerTimeoutException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception {

        return manager;

    }



}
