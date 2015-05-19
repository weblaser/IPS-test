package com.ctl.security.ips.dsm;

import com.ctl.security.clc.client.core.bean.ServerClient;
import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.domain.SecurityProfileTransportMarshaller;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import manager.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by Chad.Middleton on 1/15/2015.
 */
@Component
public class DsmPolicyClient {

    Logger logger = LoggerFactory.getLogger(DsmPolicyClient.class);

    @Autowired
    private Manager manager;

    @Value("${${spring.profiles.active:local}.dsm.username}")
    private String username;

    @Value("${${spring.profiles.active:local}.dsm.password}")
    private String password;

    @Value("#{SecurityLibraryPropertySplitter.map('${ips.clc.osType.to.dsm.policy.name}')}")
    private Map<String,String> osTypeMap;

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
        logger.info("creating new policy " + policyBean.getPolicy().getName() + ", hostName: " + policyBean.getPolicy().getHostName() + " accountAlias: " + policyBean.getAccountAlias());
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
        String clcOs = serverClient.getServerDetails(accountAlias, policy.getHostName(), bearerToken).getOs();
        String osType = osTypeMap.get(clcOs);

        if(osType != null) {
            Policy parentPolicy = retrieveSecurityProfileByName(osType);
            policy.setParentPolicyId(parentPolicy.getVendorPolicyId());
            logger.info("parent policy set for " + policy.getName() + " with parent policy id: " +  policy.getParentPolicyId());
        } else {
            String msg = "OS type for " + clcOs + " not found";
            logger.error(msg);
            throw new DsmClientException(new RuntimeException(msg));
        }
    }

    public Policy retrieveSecurityProfileById(int id) throws DsmClientException {
        try {
            logger.info("retrieving profile " + id);
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
            logger.info("retrieving profile " + name);
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
        String sessionId=null;
        try {
            sessionId = dsmLogInClient.connectToDSMClient(username, password);
            logger.info("deleting security policies with ids: " + ids.toString());
            manager.securityProfileDelete(ids, sessionId);
            dsmLogInClient.endSession(sessionId);
        } catch (ManagerSecurityException_Exception | ManagerLockoutException_Exception | ManagerMaxSessionsException_Exception | ManagerCommunicationException_Exception | ManagerAuthenticationException_Exception | ManagerException_Exception | ManagerTimeoutException_Exception | ManagerAuthorizationException_Exception e) {
            throw new DsmClientException(e);
        }
        finally {
            dsmLogInClient.endSession(sessionId);
        }
    }

}
