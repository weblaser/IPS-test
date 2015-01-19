package com.ctl.security.dsm;

import manager.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Chad.Middleton on 1/15/2015.
 */
@Component
public class DsmPolicyClient {

    @Autowired
    private Manager manager;

    @Autowired
    private DsmLogInClient dsmLogInClient;


    public SecurityProfileTransport createPolicyOnDSMClient(String username, String password, SecurityProfileTransport securityProfileTransport) throws ManagerValidationException_Exception, ManagerAuthenticationException_Exception, ManagerTimeoutException_Exception, ManagerAuthorizationException_Exception, ManagerIntegrityConstraintException_Exception, ManagerException_Exception, ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerMaxSessionsException_Exception, ManagerCommunicationException_Exception {
        String sessionID = dsmLogInClient.connectToDSMClient(username, password);
        return manager.securityProfileSave(securityProfileTransport, sessionID);
    }
}
