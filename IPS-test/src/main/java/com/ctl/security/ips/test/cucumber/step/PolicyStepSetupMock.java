package com.ctl.security.ips.test.cucumber.step;

import manager.*;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static org.mockito.Mockito.when;

/**
 * Created by kevin.wilde on 2/3/2015.
 */
@Component
@Profile({"local", "dev"})
public class PolicyStepSetupMock implements PolicyStepSetup {



    private String sessionId = "123";

    @Autowired
    private Manager manager;

    @Override
    public String setupDsmAuthentication() throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerMaxSessionsException_Exception, ManagerAuthenticationException_Exception, ManagerCommunicationException_Exception, ManagerException_Exception {

        when(manager.authenticate(APIUSER, PASSWORD_CORRECT)).thenReturn(sessionId);

        when(manager.authenticate(APIUSER, PASSWORD_WRONG)).thenThrow(ManagerAuthenticationException_Exception.class);
        when(manager.authenticate(APIUSER_WRONG, PASSWORD_CORRECT)).thenThrow(ManagerAuthenticationException_Exception.class);
        return sessionId;
    }

    @Override
    public void setupCreatePolicyRetrievePolicy() throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerMaxSessionsException_Exception, ManagerAuthenticationException_Exception, ManagerCommunicationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception, ManagerTimeoutException_Exception, ManagerAuthorizationException_Exception {

        SecurityProfileTransport expectedSecurityProfileTransport = new SecurityProfileTransport();
        int id = 0;
        expectedSecurityProfileTransport.setID(id);
        when(manager.securityProfileSave(Matchers.any(SecurityProfileTransport.class), Matchers.eq(sessionId))).thenReturn(expectedSecurityProfileTransport);

        when(manager.securityProfileRetrieve(id, sessionId)).thenReturn(expectedSecurityProfileTransport);
    }


    @Override
    public void setupDeletePolicyRetrievePolicy() throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthorizationException_Exception, ManagerTimeoutException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception {
        SecurityProfileTransport expectedSecurityProfileTransport = new SecurityProfileTransport();
        int id = 0;
        when(manager.securityProfileRetrieve(id, sessionId)).thenReturn(expectedSecurityProfileTransport);
    }
}
