package com.ctl.security.ips.dsm;

import com.ctl.security.ips.common.domain.SecurityTenant;
import com.ctl.security.ips.dsm.domain.CreateOptions;
import com.ctl.security.ips.dsm.domain.CreateTenantRequest;
import com.ctl.security.ips.dsm.domain.DsmTenant;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import com.ctl.security.library.common.httpclient.CtlSecurityClient;
import com.ctl.security.library.common.httpclient.CtlSecurityRequest;
import com.ctl.security.library.common.httpclient.CtlSecurityResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import manager.*;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.valueOf;

/**
 * Created by Chad.Middleton on 1/15/2015.
 */
@Component
@Scope("prototype")
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

    @Autowired
    private RestTemplate restTemplate;

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(DsmTenantClient.class);


    public SecurityTenant createDsmTenant(SecurityTenant securityTenant) throws DsmClientException {
        logger.info("Creating DSM Tenant...");
        String sessionId = null;
        SecurityTenant createdSecurityTenant = null;

            try {


                logger.info("username: " + username);
                logger.info("password: " + password);

                sessionId = dsmLogInClient.connectToDSMClient(username, password);

                logger.info("sessionId: " + sessionId);

                Gson gson = new Gson();
                String securityTenantJson = gson.toJson(securityTenant);

                logger.info("securityTenantJson: " + securityTenantJson);

                CreateTenantRequest createTenantRequest = createDsmCreateTenantRequest(securityTenant)
                        .setSessionId(sessionId);

                Map<String, CreateTenantRequest> createTenantRequestMap = new HashMap<>();
                createTenantRequestMap.put("createTenantRequest", createTenantRequest);

                String address = protocol + host + ":" + port + path + PATH_TENANTS;

                logger.info("Sending Create Request for DSM Tenant to: " + address);
                logger.info("Request Sent: " + createTenantRequestMap);
                ResponseEntity<String> responseEntity = null;
                try {

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add("Content-Type", "application/json");

                    responseEntity = restTemplate.exchange(address,
                        HttpMethod.POST, new HttpEntity<>(createTenantRequestMap, httpHeaders), String.class);

                } catch (Exception e) {
                    logger.error("Failed");
                    logger.error(e.getMessage(), e);
                    throw e;
                }

                String responseContent = responseEntity.getBody();
                logger.info(responseContent);
                logger.info("Marshalling Response...");

                JsonObject createTenantResponseJsonObject = gson.fromJson(responseContent, JsonObject.class);
                JsonElement createTenantResponseJsonElement = createTenantResponseJsonObject.get("createTenantResponse");
                JsonObject tenantIdJsonObject = createTenantResponseJsonElement.getAsJsonObject();
                JsonElement tenantIdJsonElement = tenantIdJsonObject.get("tenantID");
                Integer tenantId = tenantIdJsonElement.getAsInt();

                createdSecurityTenant = getSecurityTenant(tenantId, sessionId);
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

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
        HttpEntity<String> httpEntity = new HttpEntity<String>("parameters", httpHeaders);

        ResponseEntity<String> responseEntity = restTemplate.exchange(address, HttpMethod.GET, httpEntity, String.class);
        String dsmTenantXml = responseEntity.getBody();

        JAXBContext jc = JAXBContext.newInstance(DsmTenant.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        InputStream inputStream = new ByteArrayInputStream(dsmTenantXml.getBytes(StandardCharsets.UTF_8));

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