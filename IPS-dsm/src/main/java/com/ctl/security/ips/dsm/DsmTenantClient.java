package com.ctl.security.ips.dsm;

import com.ctl.security.ips.common.domain.SecurityTenant;
import com.ctl.security.ips.dsm.domain.CreateOptions;
import com.ctl.security.ips.dsm.domain.CreateTenantRequest;
import com.ctl.security.ips.dsm.domain.CreateTenantResponse;
import com.ctl.security.ips.dsm.domain.DsmTenant;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import com.ctl.security.library.common.httpclient.CtlSecurityClient;
import com.ctl.security.library.common.httpclient.CtlSecurityResponse;
import manager.*;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chad.Middleton on 1/15/2015.
 *
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


    public SecurityTenant createDsmTenant(SecurityTenant securityTenant) throws DsmClientException{
        String sessionId = null;
        CreateTenantResponse createTenantResponse = null;
        SecurityTenant createdSecurityTenant = null;
        try {

            sessionId = dsmLogInClient.connectToDSMClient(username, password);

            CreateTenantRequest createTenantRequest = createDsmCreateTenantRequest(securityTenant).setSessionId(sessionId);

            Map<String, CreateTenantRequest> createTenantRequestMap = new HashMap<>();
            createTenantRequestMap.put("createTenantRequest", createTenantRequest);

            String address = protocol + host + ":" + port + path + PATH_TENANTS;

            CtlSecurityResponse ctlSecurityResponse = ctlSecurityClient
                    .post(address)
                    .addHeader("Content-Type", "application/json")
                .body(createTenantRequestMap)
                .execute();

            String responseContent = ctlSecurityResponse.getResponseContent();
            logger.info(responseContent);

            JAXBContext jc = JAXBContext.newInstance(CreateTenantResponse.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            InputStream inputStream = new ByteArrayInputStream(responseContent.getBytes("UTF-8"));

            createTenantResponse = (CreateTenantResponse)unmarshaller.unmarshal(inputStream);

            createdSecurityTenant = getSecurityTenant(createTenantResponse.getTenantID(), sessionId);

        } catch (JAXBException | UnsupportedEncodingException e) {
            logger.error(e);
            return null;
        }
        catch (ManagerSecurityException_Exception | ManagerAuthenticationException_Exception | ManagerLockoutException_Exception | ManagerCommunicationException_Exception | ManagerMaxSessionsException_Exception | ManagerException_Exception e ) {
            logger.error(e);
            throw new DsmClientException(e);
        }
        finally {
            dsmLogInClient.endSession(sessionId);
        }

        return createdSecurityTenant;
    }

    private CreateTenantRequest createDsmCreateTenantRequest(SecurityTenant securityTenant) {
        return new CreateTenantRequest()
                .setCreateOptions(createDsmCreateTenantOptions(securityTenant))
                .setTenantElement(createDsmTenantElement(securityTenant));
    }

    private CreateOptions createDsmCreateTenantOptions(SecurityTenant securityTenant){
        return new CreateOptions()
                .setAdminAccount(securityTenant.getAdminAccount())
                .setAdminPassword(securityTenant.getAdminPassword())
                .setAdminEmail(securityTenant.getAdminEmail());
    }
    private DsmTenant createDsmTenantElement(SecurityTenant securityTenant){
        return new DsmTenant()
                .setName(securityTenant.getTenantName());
    }

    public SecurityTenant retrieveDsmTenant(Integer tenantId) throws DsmClientException {
        String sessionId = null;
        SecurityTenant securityTenant = null;

        try {
            sessionId = dsmLogInClient.connectToDSMClient(username, password);
            securityTenant = getSecurityTenant(tenantId, sessionId);
        } catch (JAXBException | UnsupportedEncodingException e){
            logger.error(e);
            return null;
        } catch (ManagerSecurityException_Exception | ManagerLockoutException_Exception | ManagerMaxSessionsException_Exception | ManagerCommunicationException_Exception | ManagerAuthenticationException_Exception | ManagerException_Exception e) {
            logger.error(e);
            throw new DsmClientException(e);
        }
        finally {
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
                .setTenantId(dsmTenant.getTenantID());


    }

}