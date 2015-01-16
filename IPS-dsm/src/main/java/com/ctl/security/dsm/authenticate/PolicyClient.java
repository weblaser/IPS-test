package com.ctl.security.dsm.authenticate;

import manager.*;

/**
 * Created by Chad.Middleton on 1/15/2015.
 */
public class PolicyClient {

    private final Manager manager;
    private final LogInClient logInClient;

    public PolicyClient(Manager manager, LogInClient logInClient) {
        this.manager = manager;
        this.logInClient = logInClient;
    }

    public SecurityProfileTransport createPolicyOnDSMClient(String username, String password, SecurityProfileTransport securityProfileTransport) throws ManagerValidationException_Exception, ManagerAuthenticationException_Exception, ManagerTimeoutException_Exception, ManagerAuthorizationException_Exception, ManagerIntegrityConstraintException_Exception, ManagerException_Exception, ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerMaxSessionsException_Exception, ManagerCommunicationException_Exception {
        String sessionID = logInClient.connectToDSMClient(username, password);
        return manager.securityProfileSave(securityProfileTransport, sessionID);
    }
}
