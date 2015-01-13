package com.ctl.security.authenticate;

import manager.*;

public class LogInClient {

    private Manager manager;

    public LogInClient(Manager manager) {
        this.manager = manager;
    }

    public String connectToDSMClient(String username, String password) throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthenticationException_Exception {
        return manager.authenticate(username, password);
    }

}
