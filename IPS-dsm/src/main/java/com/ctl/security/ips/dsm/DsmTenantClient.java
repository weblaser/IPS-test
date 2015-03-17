package com.ctl.security.ips.dsm;

import com.ctl.security.ips.common.domain.SecurityTenant;
import com.ctl.security.ips.dsm.config.DsmConfig;
import com.ctl.security.ips.dsm.domain.CreateOptions;
import com.ctl.security.ips.dsm.domain.CreateTenantRequest;
import com.ctl.security.ips.dsm.domain.CreateTenantResponse;
import com.ctl.security.ips.dsm.domain.Tenant;
import manager.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    private HttpHeaders setDsmHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private static final Logger logger = Logger.getLogger(DsmTenantClient.class);

    public SecurityTenant createDsmTenant(SecurityTenant securityTenant) throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception {
        String sessionId = dsmLogInClient.connectToDSMClient(username, password);
        CreateTenantRequest request = createDsmCreateTenantRequest(securityTenant).setSessionId(sessionId);

//        Map<String, CreateTenantRequest> map = new HashMap<>();
//        map.put("createTenantRequest", request);
        HttpEntity<CreateTenantRequest> httpEntity = new HttpEntity<>(request, setDsmHeaders());

//        CreateTenantResponse createTenantResponse = restTemplate.postForObject(url + "/tenants", map, CreateTenantResponse.class);

        CreateTenantResponse createTenantResponse = restTemplate.postForObject(url + "/tenants", httpEntity, CreateTenantResponse.class);
        Tenant createdTenant = restTemplate.getForObject(url + "/tenants/id/" + createTenantResponse.getTenantID() + "?sID=" + sessionId, Tenant.class);
        return new SecurityTenant().setAgentInitiatedActivationPassword(createdTenant.getAgentInitiatedActivationPassword()).setTenantId(createdTenant.getTenantID());
    }

    private CreateTenantRequest createDsmCreateTenantRequest(SecurityTenant securityTenant) {
        return new CreateTenantRequest()
                .setCreateOptions(createDsmCreateTenantOptions(securityTenant))
                .setTenant(createDsmTenantElement(securityTenant));
    }

    private CreateOptions createDsmCreateTenantOptions(SecurityTenant securityTenant){
        return new CreateOptions()
                .setAdminAccount(securityTenant.getAdminAccount())
                .setAdminPassword(securityTenant.getAdminPassword())
                .setAdminEmail(securityTenant.getAdminEmail());
    }
    private Tenant createDsmTenantElement(SecurityTenant securityTenant){
        return new Tenant()
                .setName(securityTenant.getTenantName());
    }

    public SecurityTenant retrieveDsmTenant(Integer tenantId) throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception {
        String sessionId = dsmLogInClient.connectToDSMClient(username, password);

        try {
            Tenant retrievedTenant = restTemplate.getForObject(url + "/tenants/id/" + tenantId + "?sID=" + sessionId, Tenant.class, setDsmHeaders());

            SecurityTenant securityTenant = new SecurityTenant()
                    .setAgentInitiatedActivationPassword(retrievedTenant.getAgentInitiatedActivationPassword())
                    .setTenantId(retrievedTenant.getTenantID());

            return securityTenant;
        }
        finally {
            dsmLogInClient.endSession(sessionId);
        }
    }

}