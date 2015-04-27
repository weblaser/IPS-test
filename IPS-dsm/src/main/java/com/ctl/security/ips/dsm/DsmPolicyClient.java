package com.ctl.security.ips.dsm;

import com.ctl.security.clc.client.core.bean.ServerClient;
import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.domain.SecurityProfileTransportMarshaller;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import com.ctl.security.ips.dsm.util.OsType;
import manager.*;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Chad.Middleton on 1/15/2015.
 */
@Component
public class DsmPolicyClient {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(DsmPolicyClient.class);

    @Autowired
    private Manager manager;

    @Value("${${spring.profiles.active:local}.dsm.username}")
    private String username;

    @Value("${${spring.profiles.active:local}.dsm.password}")
    private String password;

    @Autowired
    private DsmLogInClient dsmLogInClient;

    @Autowired
    private ServerClient serverClient;

    @Autowired
    private SecurityProfileTransportMarshaller securityProfileTransportMarshaller;

    private SecurityProfileTransport createPolicyOnDSMClient(SecurityProfileTransport securityProfileTransport) throws DsmClientException {
        try {
            String sessionId = dsmLogInClient.connectToDSMClient(username, password);
            SecurityProfileTransport newlyCreatedSecurityProfileTransport = manager.securityProfileSave(securityProfileTransport, sessionId);
            dsmLogInClient.endSession(sessionId);
            return newlyCreatedSecurityProfileTransport;
        } catch (ManagerSecurityException_Exception | ManagerLockoutException_Exception | ManagerCommunicationException_Exception | ManagerMaxSessionsException_Exception | ManagerException_Exception | ManagerAuthenticationException_Exception | ManagerAuthorizationException_Exception | ManagerIntegrityConstraintException_Exception | ManagerTimeoutException_Exception | ManagerValidationException_Exception e) {
            throw new DsmClientException(e);
        }
    }


    public PolicyBean createPolicyWithParentPolicy(PolicyBean policyBean) throws DsmClientException {
        setParentPolicy(policyBean.getPolicy(), policyBean.getAccountAlias(), policyBean.getBearerToken());

        try {
            SecurityProfileTransport securityProfileTransport = createPolicyOnDSMClient(
                    securityProfileTransportMarshaller
                            .convert(policyBean.getPolicy()));
            return new PolicyBean(policyBean.getAccountAlias(),
                    securityProfileTransportMarshaller
                            .convert(securityProfileTransport)
                            .setHostName(policyBean.getPolicy().getHostName())
                            .setUsername(policyBean.getPolicy().getUsername()),
                    policyBean.getBearerToken());
        } catch (DsmClientException e) {
            throw new DsmClientException(e);
        }
    }

    private void setParentPolicy(Policy policy, String accountAlias, String bearerToken) throws DsmClientException {
        String clcOs = serverClient.getOS(accountAlias, policy.getHostName(), bearerToken);
        Policy parentPolicy;
        logger.info("Selecting the parent policy for "+clcOs);
        if (clcOs.toLowerCase().contains("win")) {
            parentPolicy = retrieveSecurityProfileByName(OsType.CLC_WINDOWS.getValue());
        } else {
            parentPolicy = retrieveSecurityProfileByName(OsType.CLC_LINUX.getValue());
        }
        policy.setParentPolicyId(parentPolicy.getVendorPolicyId());
        logger.info("Parent policy set...");
    }

    public Policy retrieveSecurityProfileById(int id) throws DsmClientException {
        try {
            String sessionId = dsmLogInClient.connectToDSMClient(username, password);
            SecurityProfileTransport securityProfileTransport = manager.securityProfileRetrieve(id, sessionId);
            dsmLogInClient.endSession(sessionId);
            return securityProfileTransportMarshaller.convert(securityProfileTransport);
        } catch (ManagerSecurityException_Exception | ManagerLockoutException_Exception | ManagerCommunicationException_Exception | ManagerMaxSessionsException_Exception | ManagerException_Exception | ManagerAuthenticationException_Exception | ManagerTimeoutException_Exception e) {
            throw new DsmClientException(e);
        }
    }

    public Policy retrieveSecurityProfileByName(String name) throws DsmClientException {
        try {
            String sessionId = dsmLogInClient.connectToDSMClient(username, password);
            SecurityProfileTransport securityProfileTransport = manager.securityProfileRetrieveByName(name, sessionId);
            dsmLogInClient.endSession(sessionId);
            Policy policy = securityProfileTransportMarshaller.convert(securityProfileTransport);
            return policy;
        } catch (ManagerSecurityException_Exception | ManagerLockoutException_Exception | ManagerCommunicationException_Exception | ManagerMaxSessionsException_Exception | ManagerException_Exception | ManagerAuthenticationException_Exception | ManagerTimeoutException_Exception e) {
            throw new DsmClientException(e);
        }
    }

    public void securityProfileDelete(List<Integer> ids) throws DsmClientException {
        try {
            String sessionId = dsmLogInClient.connectToDSMClient(username, password);
            manager.securityProfileDelete(ids, sessionId);
            dsmLogInClient.endSession(sessionId);
        } catch (ManagerSecurityException_Exception | ManagerLockoutException_Exception | ManagerMaxSessionsException_Exception | ManagerCommunicationException_Exception | ManagerAuthenticationException_Exception | ManagerException_Exception | ManagerTimeoutException_Exception | ManagerAuthorizationException_Exception e) {
            throw new DsmClientException(e);
        }
    }

}
