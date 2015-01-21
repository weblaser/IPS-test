package com.ctl.security.dsm;

import manager.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DsmLogInClient {

    @Autowired
    private Manager manager;

    public String connectToDSMClient(String username, String password) throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthenticationException_Exception {
        return manager.authenticate(username, password);
    }

}
