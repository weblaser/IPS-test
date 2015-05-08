package com.ctl.security.ips.dsm;

import manager.*;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DsmLogInClient {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(DsmLogInClient.class);

    @Autowired
    public DsmLogInClient(Manager manager) {
        this.manager = manager;
    }

    private Manager manager;

    public String connectToDSMClient(String username, String password) throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthenticationException_Exception {
        logger.info("Connecting to DSM with username: " + username);
        String sessionId = manager.authenticate(username, password);
        logger.info("Session ID: " + sessionId);
        return sessionId;
    }

    public void endSession(String sessionId) {
        logger.info("Disconnecting from DSM. Session ID: " + sessionId);
        manager.endSession(sessionId);
    }

    public String connectTenantToDSMClient(String tenantName, String username, String password) throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception {
        logger.info("Connecting to DSM with Tenant " + tenantName);
        logger.info("Using Username: "+ username);
        String sessionId = manager.authenticateTenant(tenantName, username, password);
        logger.info("Session ID: " + sessionId);
        return sessionId;
    }
}
