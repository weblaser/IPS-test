package com.ctl.security.ips.dsm;

import manager.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Created by admin on 5/15/2015.
 */
@Component
public class DsmAgentClient {

    private final String DPI_STATUS = "Intrusion Prevention: On";
    private final String STATUS = "Managed (Online)";

    @Autowired
    private Manager manager;

    @Value("${${spring.profiles.active:local}.dsm.username}")
    private String username;

    @Value("${${spring.profiles.active:local}.dsm.password}")
    private String password;

    @Autowired
    private DsmLogInClient dsmLogInClient;

    public Boolean verifyAgentInstall(String accountAlias, String hostName) {
        String sessionId = null;
        String tenantSessionId = null;
        try {
            sessionId = dsmLogInClient.connectToDSMClient(username, password);
            tenantSessionId = dsmLogInClient.connectTenantToDSMClient(accountAlias, sessionId);

            Integer hostId = getHostId(hostName, tenantSessionId);

            if (hostId == null) {
                return false;
            }

            List<ProtectionStatusTransport> protectionStatusTransportsList = manager
                    .hostGetStatus(hostId, tenantSessionId)
                    .getProtectionStatusTransports()
                    .getItem();

            Optional<ProtectionStatusTransport> agentStatusOpt = protectionStatusTransportsList.stream()
                    .filter(currentAgentStatus -> currentAgentStatus.getProtectionType().equals(EnumProtectionType.AGENT))
                    .findFirst();

            if (agentStatusOpt.isPresent()) {
                ProtectionStatusTransport agentStatus = agentStatusOpt.get();
                if (checkAgentStatus(agentStatus)) {
                    return true;
                }
            }

        } catch (ManagerSecurityException_Exception | ManagerCommunicationException_Exception |
                ManagerLockoutException_Exception | ManagerException_Exception |
                ManagerMaxSessionsException_Exception | ManagerAuthenticationException_Exception |
                ManagerTimeoutException_Exception ex) {
            ex.printStackTrace();
        } finally {
            dsmLogInClient.endSession(tenantSessionId);
            dsmLogInClient.endSession(sessionId);
        }
        return false;
    }

    private Integer getHostId(String hostName, String tenantSessionId) throws ManagerAuthenticationException_Exception, ManagerTimeoutException_Exception, ManagerException_Exception {
        Integer hostId = null;

        HostTransport hostTransport = manager.hostRetrieveByName(hostName, tenantSessionId);

        if (hostTransport != null) {
            hostId = hostTransport.getID();
        }

        return hostId;
    }

    private boolean checkAgentStatus(ProtectionStatusTransport agentStatus) {
        return agentStatus.getDpiStatus().contains(DPI_STATUS) && agentStatus.getStatus().contains(STATUS);
    }

}
