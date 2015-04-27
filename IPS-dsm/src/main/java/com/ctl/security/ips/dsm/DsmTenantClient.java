package com.ctl.security.ips.dsm;

import com.ctl.security.ips.common.domain.SecurityTenant;
import com.ctl.security.ips.dsm.domain.*;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import com.ctl.security.library.common.httpclient.CtlSecurityClient;
import com.ctl.security.library.common.httpclient.CtlSecurityRequest;
import com.ctl.security.library.common.httpclient.CtlSecurityResponse;
import manager.*;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

/**
 * Created by Chad.Middleton on 1/15/2015.
 */
@Component
public class DsmTenantClient {

    public static final String PATH_TENANTS = "/tenants";
    public static final String PATH_TENANTS_ID = "/tenants/id/";
    public static final String QUERY_PARAM_SESSION_ID = "sID=";

    @Value("${${spring.profiles.active:local}.dsm.rest.protocol}")
    private String protocol;
    @Value("${${spring.profiles.active:local}.dsm.rest.host}")
    private String host;
    @Value("${${spring.profiles.active:local}.dsm.rest.port}")
    private String port;
    @Value("${${spring.profiles.active:local}.dsm.rest.path}")
    private String path;

    @Value("${${spring.profiles.active:local}.dsm.username}")
    private String username;

    @Value("${${spring.profiles.active:local}.dsm.password}")
    private String password;

    @Autowired
    private DsmLogInClient dsmLogInClient;

    @Autowired
    private CtlSecurityClient ctlSecurityClient;

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(DsmTenantClient.class);


    public SecurityTenant createDsmTenant(SecurityTenant securityTenant) throws DsmClientException {
        logger.info("Creating DSM Tenant...");
        String sessionId = null;
        CreateTenantResponse createTenantResponse = null;
        SecurityTenant createdSecurityTenant = null;
        try {

            sessionId = dsmLogInClient.connectToDSMClient(username, password);

            CreateTenantRequest createTenantRequest = createDsmCreateTenantRequest(securityTenant)
                    .setSessionId(sessionId);

            Map<String, CreateTenantRequest> createTenantRequestMap = new HashMap<>();
            createTenantRequestMap.put("createTenantRequest", createTenantRequest);

            String address = protocol + host + ":" + port + path + PATH_TENANTS;

            logger.info("Sending Create Request for DSM Tenant to: " + address);
            logger.info("Request Sent: " + createTenantRequestMap);
            CtlSecurityResponse ctlSecurityResponse = ctlSecurityClient
                    .post(address)
                    .addHeader("Content-Type", "application/json")
                    .body(createTenantRequestMap)
                    .execute();

            logger.info(ctlSecurityResponse);
            logger.info(ctlSecurityResponse.getStatusCode());
            logger.info(ctlSecurityResponse.getResponseContent());

            String responseContent = ctlSecurityResponse.getResponseContent();
            logger.info(responseContent);

            logger.info("Marshalling Response...");
            JAXBContext jc = JAXBContext.newInstance(CreateTenantResponse.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            InputStream inputStream = new ByteArrayInputStream(responseContent.getBytes("UTF-8"));

            createTenantResponse = (CreateTenantResponse) unmarshaller.unmarshal(inputStream);

            createdSecurityTenant = getSecurityTenant(createTenantResponse.getTenantID(), sessionId);
            logger.info("Successfully created Tenant...");
        } catch (JAXBException | UnsupportedEncodingException e) {
            logger.error(e);
            return null;
        } catch (ManagerSecurityException_Exception | ManagerAuthenticationException_Exception |
                ManagerLockoutException_Exception | ManagerCommunicationException_Exception |
                ManagerMaxSessionsException_Exception | ManagerException_Exception e) {
            logger.error(e);
            throw new DsmClientException(e);
        } finally {
            dsmLogInClient.endSession(sessionId);
        }

        return createdSecurityTenant;
    }

    private CreateTenantRequest createDsmCreateTenantRequest(SecurityTenant securityTenant) {
        return new CreateTenantRequest()
                .setCreateOptions(createDsmCreateTenantOptions(securityTenant))
                .setTenantElement(createDsmTenantElement(securityTenant));
    }

    private CreateOptions createDsmCreateTenantOptions(SecurityTenant securityTenant) {
        return new CreateOptions()
                .setAdminAccount(securityTenant.getAdminAccount())
                .setAdminPassword(securityTenant.getAdminPassword())
                .setAdminEmail(securityTenant.getAdminEmail());
    }

    private DsmTenant createDsmTenantElement(SecurityTenant securityTenant) {
        return new DsmTenant()
                .setName(securityTenant.getTenantName());
    }

    public SecurityTenant retrieveDsmTenant(Integer tenantId) throws DsmClientException {
        String sessionId = null;
        SecurityTenant securityTenant = null;

        try {
            sessionId = dsmLogInClient.connectToDSMClient(username, password);
            securityTenant = getSecurityTenant(tenantId, sessionId);
        } catch (JAXBException | UnsupportedEncodingException e) {
            logger.error(e);
            return null;
        } catch (ManagerSecurityException_Exception | ManagerLockoutException_Exception |
                ManagerMaxSessionsException_Exception | ManagerCommunicationException_Exception |
                ManagerAuthenticationException_Exception | ManagerException_Exception e) {
            logger.error(e);
            throw new DsmClientException(e);
        } finally {
            dsmLogInClient.endSession(sessionId);
        }
        return securityTenant;
    }

    private SecurityTenant getSecurityTenant(Integer tenantId, String sessionId) throws JAXBException, UnsupportedEncodingException {
        String address = protocol + host + ":" + port + path + PATH_TENANTS_ID + tenantId + "?" + QUERY_PARAM_SESSION_ID + sessionId;

        String responseContent = ctlSecurityClient.get(address).execute().getResponseContent();

        JAXBContext jc = JAXBContext.newInstance(DsmTenant.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        InputStream inputStream = new ByteArrayInputStream(responseContent.getBytes("UTF-8"));

        DsmTenant dsmTenant = (DsmTenant) unmarshaller.unmarshal(inputStream);

        return new SecurityTenant()
                .setAgentInitiatedActivationPassword(dsmTenant.getAgentInitiatedActivationPassword())
                .setTenantId(dsmTenant.getTenantID())
                .setGuid(dsmTenant.getGuid())
                .setState(dsmTenant.getState());

    }

    public void deleteDsmTenant(String tenantId) throws DsmClientException {
        String sessionId = null;
        try {
            sessionId = dsmLogInClient.connectToDSMClient(username, password);
            String address = protocol + host + ":" + port + path + PATH_TENANTS_ID + tenantId + "?" +
                    QUERY_PARAM_SESSION_ID + sessionId;

            CtlSecurityRequest ctlSecurityRequest = ctlSecurityClient.delete(address);

            CtlSecurityResponse ctlSecurityResponse = ctlSecurityRequest.execute();

            HttpStatus httpStatus = valueOf(ctlSecurityResponse.getStatusCode());

            if (httpStatus.is2xxSuccessful() == false) {
                throw new DsmClientException(new Exception("Could not delete Tenant"));
            }

        } catch (ManagerSecurityException_Exception | ManagerLockoutException_Exception |
                ManagerMaxSessionsException_Exception | ManagerCommunicationException_Exception |
                ManagerAuthenticationException_Exception | ManagerException_Exception e) {
            throw new DsmClientException(e);
        } finally {
            dsmLogInClient.endSession(sessionId);
        }
    }
}