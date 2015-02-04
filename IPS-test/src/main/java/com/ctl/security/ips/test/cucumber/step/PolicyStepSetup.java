package com.ctl.security.ips.test.cucumber.step;

import manager.*;

/**
 * Created by kevin.wilde on 2/3/2015.
 */
public interface PolicyStepSetup {

    public static final String APIUSER = "apiuser";
    public static final String PASSWORD_CORRECT = "trejachad32jUgEs";
    public static final String APIUSER_WRONG = "wrong";
    public static final String PASSWORD_WRONG = "wrong";

    String setupDsmAuthentication() throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerMaxSessionsException_Exception, ManagerAuthenticationException_Exception, ManagerCommunicationException_Exception, ManagerException_Exception;

    void setupCreatePolicyRetrievePolicy() throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerMaxSessionsException_Exception, ManagerAuthenticationException_Exception, ManagerCommunicationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception, ManagerTimeoutException_Exception, ManagerAuthorizationException_Exception;

    void setupDeletePolicyRetrievePolicy() throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthorizationException_Exception, ManagerTimeoutException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception;
}
