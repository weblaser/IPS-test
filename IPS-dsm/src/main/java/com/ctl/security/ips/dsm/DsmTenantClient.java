package com.ctl.security.ips.dsm;

import com.ctl.security.ips.common.domain.Tenant;
import com.ctl.security.ips.dsm.config.DsmConfig;
import com.ctl.security.ips.dsm.domain.*;
import manager.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private TenantElementMarshaller tenantElementMarshaller;


    public Tenant createDsmTenant(Tenant tenant) throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception {
        String sessionId = dsmLogInClient.connectToDSMClient(username, password);
        CreateTenantRequest request = createDsmCreateTenantRequest(tenant).setSessionId(sessionId);
//        Map<String, CreateTenantRequest> map = new HashMap<>();
//        map.put("createTenantRequest", request);
//        CreateTenantResponse createTenantResponse = restTemplate.postForObject(url + "/tenants", map, CreateTenantResponse.class);
        CreateTenantResponse createTenantResponse = restTemplate.postForObject(url + "/tenants", request, CreateTenantResponse.class);
        TenantElement tenantElement = restTemplate.getForObject(url + "/tenants/id/" + createTenantResponse.getTenantID() + "?sID=" + sessionId, TenantElement.class);
        return new Tenant()
                .setTenantId(tenantElement.getTenantID())
                .setAgentInitiatedActivationPassword(tenantElement.getAgentInitiatedActivationPassword());
    }

    private CreateTenantRequest createDsmCreateTenantRequest(Tenant tenant) {
        return new CreateTenantRequest()
                .setCreateOptions(createDsmCreateTenantOptions(tenant))
                .setTenantElement(createDsmTenantElement(tenant));
    }

    private CreateOptions createDsmCreateTenantOptions(Tenant tenant){
        return new CreateOptions()
                .setAdminAccount(tenant.getAdminAccount())
                .setAdminPassword(tenant.getAdminPassword())
                .setAdminEmail(tenant.getAdminEmail());
    }
    private TenantElement createDsmTenantElement(Tenant tenant){
        return new TenantElement()
                .setName(tenant.getTenantName());
    }
}