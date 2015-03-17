package com.ctl.security.ips.dsm;

import com.ctl.security.ips.common.domain.SecurityTenant;
import com.ctl.security.ips.dsm.domain.CreateTenantRequest;
import com.ctl.security.ips.dsm.domain.CreateTenantResponse;
import com.ctl.security.ips.dsm.domain.TenantElement;
import manager.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Chad.Middleton on 1/15/2015.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DsmTenantElementClientTest {

    @InjectMocks
    private DsmTenantClient classUnderTest;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private DsmLogInClient dsmLogInClient;

    @Before
    public void before() {
        ReflectionTestUtils.setField(classUnderTest, "url", "/fakePath/rest");
        ReflectionTestUtils.setField(classUnderTest, "username", "userName");
        ReflectionTestUtils.setField(classUnderTest, "password", "password");
    }



    @Test
    public void testCreateDsmTenant_createdTenant() throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception {
        //arrange
        SecurityTenant securityTenant = new SecurityTenant();
        Integer tenantId = 1;
        String sessionId = "12345";
        String username = "userName";
        String password = "password";
        String agentInitiatedActivationPassword = "superSecretPassword";
        SecurityTenant expected = new SecurityTenant().setTenantId(tenantId).setAgentInitiatedActivationPassword(agentInitiatedActivationPassword);
        when(dsmLogInClient.connectToDSMClient(username, password)).thenReturn(sessionId);
        when(restTemplate.postForObject(anyString(), any(CreateTenantRequest.class), eq(CreateTenantResponse.class))).thenReturn(new CreateTenantResponse().setTenantID(tenantId));
        when(restTemplate.getForObject(anyString(), eq(TenantElement.class))).thenReturn(new TenantElement()
                .setTenantID(tenantId)
                .setAgentInitiatedActivationPassword(agentInitiatedActivationPassword));

        //act
        SecurityTenant result = classUnderTest.createDsmTenant(securityTenant);

        //assert
        assertNotNull(result);
        assertNotNull(result.getTenantId());
        assertEquals(expected.getTenantId(), result.getTenantId());
        assertEquals(expected.getAgentInitiatedActivationPassword(), result.getAgentInitiatedActivationPassword());

        verify(dsmLogInClient).connectToDSMClient(username, password);
        verify(restTemplate).postForObject(eq("/fakePath/rest/tenants"), any(CreateTenantRequest.class), eq(CreateTenantResponse.class));
        verify(restTemplate).getForObject("/fakePath/rest/tenants/id/" + result.getTenantId() + "?sID=" + sessionId,  TenantElement.class);
    }



//    @Test
//    public void testCreateDsmTenant_NoTenantInDsmDatabaseAlready() throws Exception {
//        //arrange
//
//
//        Tenant tenant = new Tenant();
//        Integer tenantID = 1;
//        String agentInitiatedActivationPassword = "SuperSecretPassword";
//        Tenant expectedTenant = new Tenant().setTenantID(tenantID).setAgentInitiatedActivationPassword(agentInitiatedActivationPassword);
//        String sessionId = "12345";
//
//        when(dsmLogInClient.connectToDSMClient(anyString(), anyString())).thenReturn(sessionId);//1
//
//        CreateTenantResponse tenantResponse = new CreateTenantResponse();
//        tenantResponse.setTenantID(tenantID);
//
//
//        TenantElement tenantElement = createTenantElement();
//        when(tenantElementMarshaller.convert(tenant)).thenReturn(tenantElement);
//        when(tenantAPI.addTenant(any(CreateTenantRequest.class))).thenReturn(tenantResponse);
//
//        TenantElement expectedTenantElement = createTenantElement();
//        when(tenantAPI.getTenantById(tenantResponse.getTenantID(), sessionId)).thenReturn(expectedTenantElement);
//        when(tenantElementMarshaller.convert(expectedTenantElement)).thenReturn(expectedTenant);
//
//        //act
//        Tenant result = classUnderTest.createDsmTenant(tenant);
//
//        //assert
//        assertNotNull(result);
//        assertEquals(expectedTenant, result);
//        assertEquals(expectedTenant.getTenantID(), result.getTenantID());
//        assertNotNull(result.getAgentInitiatedActivationPassword());
//        assertEquals(expectedTenant.getAgentInitiatedActivationPassword(), result.getAgentInitiatedActivationPassword());
//
//    }

//    @Test
//    public void testCreateDsmTenant_TenantInDsmDatabaseAlready() throws Exception{
//        //arrange
//        String sessionId = "12345";
//        Integer tenantID = 1;
//        String agentInitiatedActivationPassword = "SuperSecretPassword";
//        Tenant tenant = new Tenant().setTenantID(tenantID).setAgentInitiatedActivationPassword(agentInitiatedActivationPassword);
//        when(dsmLogInClient.connectToDSMClient(anyString(), anyString())).thenReturn(sessionId);
//        when(tenantAPI.getTenantById(tenantID, sessionId)).thenReturn(new TenantElement());
//        when(tenantElementMarshaller.convert(any(TenantElement.class))).thenReturn(tenant);
//        //act
//        Tenant result = classUnderTest.createDsmTenant();
//        //assert
//        assertNotNull(result);
//        assertEquals(tenantID, result.getTenantID());
//        assertEquals(agentInitiatedActivationPassword, result.getAgentInitiatedActivationPassword());
//    }

//        private TenantElement createTenantElement() {
//        TenantElement tenantElement = new TenantElement();
//        tenantElement.setName("ExampleCokeTenant");
//        tenantElement.setCountry("US");
//        tenantElement.setLanguage("en");
//        tenantElement.setTimeZone("US/Central");
//        return tenantElement;

//    }
}