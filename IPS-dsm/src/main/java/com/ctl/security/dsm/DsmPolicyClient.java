package com.ctl.security.dsm;

import com.ctl.security.dsm.domain.CtlSecurityProfile;
import com.ctl.security.dsm.domain.SecurityProfileTransportMarshaller;
import manager.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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


    public SecurityProfileTransport createPolicyOnDSMClient(SecurityProfileTransport securityProfileTransport) throws ManagerValidationException_Exception, ManagerAuthenticationException_Exception, ManagerTimeoutException_Exception, ManagerAuthorizationException_Exception, ManagerIntegrityConstraintException_Exception, ManagerException_Exception, ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerMaxSessionsException_Exception, ManagerCommunicationException_Exception {
        String sessionID = dsmLogInClient.connectToDSMClient(username, password);
        return manager.securityProfileSave(securityProfileTransport, sessionID);
    }

    public CtlSecurityProfile createCtlSecurityProfile(CtlSecurityProfile ctlSecurityProfileToBeCreated) throws ManagerLockoutException_Exception, ManagerAuthenticationException_Exception, ManagerException_Exception, ManagerIntegrityConstraintException_Exception, ManagerSecurityException_Exception, ManagerValidationException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerAuthorizationException_Exception, ManagerTimeoutException_Exception {
        securityProfileTransportMarshaller.convert(ctlSecurityProfileToBeCreated);

        SecurityProfileTransport securityProfileTransport = null;

        createPolicyOnDSMClient(securityProfileTransport);

        return new CtlSecurityProfile();
    }
}
