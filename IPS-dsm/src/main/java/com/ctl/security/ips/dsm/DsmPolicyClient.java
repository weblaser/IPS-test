package com.ctl.security.ips.dsm;

import com.ctl.security.dsm.domain.SecurityProfileTransportMarshaller;
import com.ctl.security.dsm.exception.DsmPolicyClientException;
import com.ctl.security.ips.common.domain.Policy;
import manager.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Chad.Middleton on 1/15/2015.
 */
@Component
public class DsmPolicyClient {

    @Autowired
    private Manager manager;

    @Value("${${spring.profiles.active:local}.dsm.username}")
    private String username;

    @Value("${${spring.profiles.active:local}.dsm.password}")
    private String password;

    @Autowired
    private DsmLogInClient dsmLogInClient;

    @Autowired
    private SecurityProfileTransportMarshaller securityProfileTransportMarshaller;

    private SecurityProfileTransport createPolicyOnDSMClient(SecurityProfileTransport securityProfileTransport) throws DsmPolicyClientException{
        try {
            String sessionID = dsmLogInClient.connectToDSMClient(username, password);
            return manager.securityProfileSave(securityProfileTransport, sessionID);
        } catch (ManagerSecurityException_Exception | ManagerLockoutException_Exception | ManagerCommunicationException_Exception | ManagerMaxSessionsException_Exception | ManagerException_Exception | ManagerAuthenticationException_Exception | ManagerAuthorizationException_Exception | ManagerIntegrityConstraintException_Exception | ManagerTimeoutException_Exception | ManagerValidationException_Exception e) {
            throw new DsmPolicyClientException(e);
        }
    }

    public Policy createCtlSecurityProfile(Policy policy) throws DsmPolicyClientException {

        try {
          SecurityProfileTransport securityProfileTransport = createPolicyOnDSMClient(securityProfileTransportMarshaller.convert(policy));
            return securityProfileTransportMarshaller.convert(securityProfileTransport);
        } catch (DsmPolicyClientException e) {
            throw new DsmPolicyClientException(e);
        }
    }
}
