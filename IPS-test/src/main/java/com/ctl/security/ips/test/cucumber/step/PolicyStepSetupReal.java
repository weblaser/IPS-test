package com.ctl.security.ips.test.cucumber.step;

import manager.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Created by kevin.wilde on 2/3/2015.
 */
@Component
@Profile({"ts", "qa", "prod"})
public class PolicyStepSetupReal implements PolicyStepSetup {
    @Override
    public String setupDsmAuthentication() throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerMaxSessionsException_Exception, ManagerAuthenticationException_Exception, ManagerCommunicationException_Exception, ManagerException_Exception {
        return null;
    }

    @Override
    public void setupCreatePolicyRetrievePolicy() throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerMaxSessionsException_Exception, ManagerAuthenticationException_Exception, ManagerCommunicationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception, ManagerTimeoutException_Exception, ManagerAuthorizationException_Exception {

    }

    @Override
    public void setupDeletePolicyRetrievePolicy() throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthorizationException_Exception, ManagerTimeoutException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception {

    }
}
