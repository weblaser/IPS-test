package com.ctl.security.dsm;

import manager.*;

public class DsmLogInClient {

    private final Manager manager;

    public DsmLogInClient(Manager manager) {
        this.manager = manager;
    }

    public String connectToDSMClient(String username, String password) throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthenticationException_Exception {
        return manager.authenticate(username, password);
    }

}
