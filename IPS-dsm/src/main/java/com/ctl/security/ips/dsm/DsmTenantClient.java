package com.ctl.security.ips.dsm;

import com.ctl.security.ips.common.domain.SecurityTenant;
import com.ctl.security.ips.dsm.config.DsmConfig;
import com.ctl.security.ips.dsm.domain.*;
import com.ctl.security.library.common.httpclient.CtlSecurityClient;
import com.ctl.security.library.common.httpclient.CtlSecurityResponse;
import manager.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.*;
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

    @Qualifier(DsmConfig.DSM_REST_BEAN)
    @Autowired
    private RestTemplate restTemplate;

    @Value("${${spring.profiles.active:local}.dsm.restUrl}")
    private String url;

    @Value("${${spring.profiles.active:local}.dsm.username}")
    private String username;

    @Value("${${spring.profiles.active:local}.dsm.password}")
    private String password;

    @Autowired
    private DsmLogInClient dsmLogInClient;

    Map<Long, String> aMap = new HashMap() {{
        put(1l, "lane");
        put(2l, "chad");
        put(3l, "kevin");
    }};

    @Autowired
    private CtlSecurityClient ctlSecurityClient;

    private HttpHeaders setDsmHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private static final Logger logger = Logger.getLogger(DsmTenantClient.class);

    public SecurityTenant createDsmTenant(SecurityTenant securityTenant) throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception {
        String sessionId = dsmLogInClient.connectToDSMClient(username, password);
        CreateTenantRequest request = createDsmCreateTenantRequest(securityTenant).setSessionId(sessionId);

        Map<String, CreateTenantRequest> createTenantRequestMap = new HashMap<>();
        createTenantRequestMap.put("createTenantRequest", request);


        String address = url + "/tenants";
        CtlSecurityResponse ctlSecurityResponse = ctlSecurityClient.
                post(address).
                addHeader("Content-Type", "application/json").
                body(createTenantRequestMap).
                execute();

        String responseContent = ctlSecurityResponse.getResponseContent();
        logger.info(responseContent);

        SecurityTenant createdSecurityTenant = null;
        try {
            JAXBContext jc = JAXBContext.newInstance(CreateTenantResponse.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            InputStream inputStream = new ByteArrayInputStream(responseContent.getBytes("UTF-8"));

            CreateTenantResponse createTenantResponse = (CreateTenantResponse)unmarshaller.unmarshal(inputStream);

            createdSecurityTenant = getSecurityTenant(createTenantResponse.getTenantID(), sessionId);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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

    public SecurityTenant retrieveDsmTenant(Integer tenantId) throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception {
        String sessionId = dsmLogInClient.connectToDSMClient(username, password);

        SecurityTenant securityTenant = null;

        try {
            securityTenant = getSecurityTenant(tenantId, sessionId);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            dsmLogInClient.endSession(sessionId);
        }
        return securityTenant;
    }

    private SecurityTenant getSecurityTenant(Integer tenantId, String sessionId) throws JAXBException, UnsupportedEncodingException {
        String address = url + "/tenants/id/" + tenantId + "?sID=" + sessionId;

        String responseContent = ctlSecurityClient.get(address).execute().getResponseContent();

        JAXBContext jc = JAXBContext.newInstance(DsmTenant.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();

        InputStream inputStream = new ByteArrayInputStream(responseContent.getBytes("UTF-8"));

        DsmTenant dsmTenant = (DsmTenant)unmarshaller.unmarshal(inputStream);

        SecurityTenant securityTenant = new SecurityTenant()
                .setAgentInitiatedActivationPassword(dsmTenant.getAgentInitiatedActivationPassword())
                .setTenantId(dsmTenant.getTenantID());
        return securityTenant;
    }

}