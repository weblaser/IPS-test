package com.ctl.security.ips.dsm;

import com.ctl.security.ips.common.domain.SecurityTenant;
import com.ctl.security.ips.dsm.domain.DsmTenant;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import manager.*;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Chad.Middleton on 1/15/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class DsmTenantClientTest {

    @InjectMocks
    private DsmTenantClient classUnderTest;

    @Mock
    private DsmLogInClient dsmLogInClient;

    @Mock
    private Unmarshaller unmarshaller;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ResponseEntity responseEntity;

    @Mock
    private ResponseEntity responseEntityTenantGet;

    private final String TENANT_GUID = "B2EABDEC-101F-E8CF-AC25-73C04B548BFA";
    private final int GOOD_STATUS_CODE = HttpStatus.SC_OK;
    private final int BAD_STATUS_CODE = HttpStatus.SC_BAD_REQUEST;
    private final Integer TENANT_ID = 1;
    private final String SESSION_ID = "123456";
    private final String USERNAME = "uniqueUsername";
    private final String PASSWORD = "password";
    private final String AGENT_PASSWORD = "1D107A18-AA1E-B379-54C5-07519C5BC5D7";
    private final String TENANT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<tenant>\n" +
            "    <agentInitiatedActivationPassword>1D107A18-AA1E-B379-54C5-07519C5BC5D7</agentInitiatedActivationPassword>\n" +
            "    <allModulesVisible>true</allModulesVisible>\n" +
            "    <country>US</country>\n" +
            "    <databaseServerID \n" +
            "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n" +
            "        <demoMode>false</demoMode>\n" +
            "        <description></description>\n" +
            "        <guid>B2EABDEC-101F-E8CF-AC25-73C04B548BFA</guid>\n" +
            "        <hideUnlicensedModules>false</hideUnlicensedModules>\n" +
            "        <language>en</language>\n" +
            "        <licenseMode>Inherited</licenseMode>\n" +
            "        <name>tenant01</name>\n" +
            "        <state>ACTIVE</state>\n" +
            "        <tenantID>1</tenantID>\n" +
            "        <timeZone>Atlantic/St_Helena</timeZone>\n" +
            "    </tenant>";
    private final String TENANT_ID_XML = "{\"createTenantResponse\":{\"tenantID\":161}}";

    public static final String PATH_TENANTS = "/tenants";
    public static final String PATH_TENANTS_ID = "/tenants/id/";
    public static final String QUERY_PARAM_SESSION_ID = "sID=";

    private final String protocol = "protocol";
    private final String host = "host";
    private final String port = "port";
    private final String path = "path";

    @Before
    public void before() {

        ReflectionTestUtils.setField(classUnderTest, "username", USERNAME);
        ReflectionTestUtils.setField(classUnderTest, "password", PASSWORD);
        ReflectionTestUtils.setField(classUnderTest, "protocol", protocol);
        ReflectionTestUtils.setField(classUnderTest, "host", host);
        ReflectionTestUtils.setField(classUnderTest, "port", port);
        ReflectionTestUtils.setField(classUnderTest, "path", path);

    }

    @Test
    public void testCreateDsmTenant_CreatedTenant() throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, JAXBException, UnsupportedEncodingException, DsmClientException {
        //arrange
        SecurityTenant securityTenant = new SecurityTenant();
        String responseId = TENANT_ID_XML;
        InputStream inputStream = new ByteArrayInputStream(responseId.getBytes("UTF-8"));
        SecurityTenant expected = new SecurityTenant().setTenantId(TENANT_ID).setAgentInitiatedActivationPassword(AGENT_PASSWORD).setGuid(TENANT_GUID);
        DsmTenant dsmTenant = new DsmTenant().setTenantID(TENANT_ID).setAgentInitiatedActivationPassword(AGENT_PASSWORD).setGuid(TENANT_GUID);

        when(unmarshaller.unmarshal(inputStream)).thenReturn(dsmTenant);
        when(dsmLogInClient.connectToDSMClient(USERNAME, PASSWORD)).thenReturn(SESSION_ID);

        when(responseEntity.getBody()).thenReturn(responseId);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(responseEntity);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(responseEntityTenantGet);
        when(responseEntityTenantGet.getBody()).thenReturn(TENANT_XML);

        //act
        SecurityTenant result = classUnderTest.createDsmTenant(securityTenant);

        //TODO uncomment tests
        //assert
        assertNotNull(result);
        assertNotNull(result.getTenantId());
        assertEquals(expected.getTenantId(), result.getTenantId());
        assertEquals(expected.getAgentInitiatedActivationPassword(), result.getAgentInitiatedActivationPassword());
        assertEquals(expected.getGuid(), result.getGuid());
    }

    @Test
    public void createDsmTenant_handlesException() throws DsmClientException {
        SecurityTenant securityTenant = new SecurityTenant();

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenThrow(JAXBException.class);

        SecurityTenant result = classUnderTest.createDsmTenant(securityTenant);

        assertNull(result);
    }

    //TODO review test after create tenant bug is fixed
    @Test(expected = DsmClientException.class)
    public void createDsmTenant_handlesLoginException() throws DsmClientException {
        SecurityTenant securityTenant = new SecurityTenant();

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
            .thenThrow(ManagerSecurityException_Exception.class);

        //SecurityTenant result = classUnderTest.createDsmTenant(securityTenant);
        assertNull(classUnderTest.createDsmTenant(securityTenant));
    }

    @Test
    public void testRetrieveDsmTenant_TenantReturned() throws Exception {
        //arrange
        SecurityTenant expected = new SecurityTenant().setTenantId(TENANT_ID).setAgentInitiatedActivationPassword(AGENT_PASSWORD);
        String responseTenant = TENANT_XML;
        InputStream inputStream = new ByteArrayInputStream(responseTenant.getBytes("UTF-8"));
        DsmTenant dsmTenant = new DsmTenant().setTenantID(TENANT_ID).setAgentInitiatedActivationPassword(AGENT_PASSWORD);

        when(dsmLogInClient.connectToDSMClient(USERNAME, PASSWORD)).thenReturn(SESSION_ID);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(responseEntityTenantGet);
        when(responseEntityTenantGet.getBody()).thenReturn(TENANT_XML);

        when(unmarshaller.unmarshal(inputStream)).thenReturn(dsmTenant);

        //act
        SecurityTenant result = classUnderTest.retrieveDsmTenant(TENANT_ID);
        //assert
        assertNotNull(result);
        assertEquals(expected.getTenantId(), result.getTenantId());
        assertEquals(expected.getAgentInitiatedActivationPassword(), result.getAgentInitiatedActivationPassword());
    }

    @Test
    public void retrieveDsmTenant_returnsNullResponseWhenTenantNotFound() throws Exception {
        //arrange
        String responseTenant = TENANT_XML;
        InputStream inputStream = new ByteArrayInputStream(responseTenant.getBytes("UTF-8"));
        DsmTenant dsmTenant = new DsmTenant().setTenantID(TENANT_ID).setAgentInitiatedActivationPassword(AGENT_PASSWORD);

        when(dsmLogInClient.connectToDSMClient(USERNAME, PASSWORD)).thenReturn(SESSION_ID);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(responseEntity);

        when(responseEntity.getBody()).thenReturn(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<error>\n" +
                "    <message>Unable to load. The system may be experiencing loss of database connectivity. Please try again.</message>\n" +
                "</error>");
        when(unmarshaller.unmarshal(inputStream)).thenReturn(dsmTenant);

        //act
        SecurityTenant securityTenant = classUnderTest.retrieveDsmTenant(TENANT_ID);

        assertNull(securityTenant);
    }


    @Test(expected = DsmClientException.class)
    public void retrieveDsmTenant_handlesLoginError() throws Exception {
        //arrange
        when(dsmLogInClient.connectToDSMClient(USERNAME, PASSWORD)).thenReturn(SESSION_ID);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenThrow(ManagerSecurityException_Exception.class);

        //act
        classUnderTest.retrieveDsmTenant(TENANT_ID);
    }

    /**
     * Given a tenant is created in the DSM API
     * When that tenant is deleted with the REST API
     * Then the tenant is no longer available
     */

    private void setupForDeleteTest(int statusCode) throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception {
        when(responseEntity.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class)))
            .thenReturn(responseEntity);

        when(dsmLogInClient.connectToDSMClient(USERNAME, PASSWORD)).thenReturn(SESSION_ID);
    }

    @Test
    public void deleteDsmTenantTest_connectsToDsm() throws Exception {
        String tenantId = TENANT_ID.toString();

        setupForDeleteTest(GOOD_STATUS_CODE);

        classUnderTest.deleteDsmTenant(tenantId);

        verify(dsmLogInClient).connectToDSMClient(anyString(), anyString());
    }

    @Test
    public void deleteDsmTenantTest_endsSession() throws Exception {
        String tenantId = TENANT_ID.toString();

        setupForDeleteTest(GOOD_STATUS_CODE);

        classUnderTest.deleteDsmTenant(tenantId);

        verify(dsmLogInClient).endSession(anyString());
    }

    @Test(expected = DsmClientException.class)
    public void deleteDsmTenantTest_failsToDeleteInDSM() throws Exception {
        String tenantId = TENANT_ID.toString();
        String address = protocol + host + ":" + port + path + PATH_TENANTS_ID + tenantId + "?"
                + QUERY_PARAM_SESSION_ID + SESSION_ID;

        setupForDeleteTest(BAD_STATUS_CODE);

        when(restTemplate.exchange(eq(address), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class)))
            .thenThrow(ManagerSecurityException_Exception.class);

        classUnderTest.deleteDsmTenant(tenantId);

        verify(restTemplate).exchange(eq(address), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void deleteDsmTenantTest_deletesDsmTenant() throws Exception {
        String tenantId = TENANT_ID.toString();
        String address = protocol + host + ":" + port + path + PATH_TENANTS_ID + tenantId + "?"
                + QUERY_PARAM_SESSION_ID + SESSION_ID;

        setupForDeleteTest(GOOD_STATUS_CODE);

        classUnderTest.deleteDsmTenant(tenantId);

        verify(restTemplate).exchange(eq(address), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class));
    }

    @Test(expected = DsmClientException.class)
    public void deleteDsmTenantTest_throwsManagerSecurityException_Exception() throws Exception {
        String tenantId = TENANT_ID.toString();

        when(dsmLogInClient.connectToDSMClient(anyString(), anyString()))
                .thenThrow(new ManagerSecurityException_Exception());

        classUnderTest.deleteDsmTenant(tenantId);

        verify(dsmLogInClient).endSession(anyString());
    }

    @Test(expected = DsmClientException.class)
    public void deleteDsmTenantTest_throwsManagerLockoutException_Exception() throws Exception {
        String tenantId = TENANT_ID.toString();

        when(dsmLogInClient.connectToDSMClient(anyString(), anyString()))
                .thenThrow(new ManagerLockoutException_Exception());

        classUnderTest.deleteDsmTenant(tenantId);

        verify(dsmLogInClient).endSession(anyString());
    }

    @Test(expected = DsmClientException.class)
    public void deleteDsmTenantTest_throwsManagerMaxSessionsException_Exception() throws Exception {
        String tenantId = TENANT_ID.toString();

        when(dsmLogInClient.connectToDSMClient(anyString(), anyString()))
                .thenThrow(new ManagerMaxSessionsException_Exception());

        classUnderTest.deleteDsmTenant(tenantId);

        verify(dsmLogInClient).endSession(anyString());
    }

    @Test(expected = DsmClientException.class)
    public void deleteDsmTenantTest_throwsManagerCommunicationException_Exception() throws Exception {
        String tenantId = TENANT_ID.toString();

        when(dsmLogInClient.connectToDSMClient(anyString(), anyString()))
                .thenThrow(new ManagerCommunicationException_Exception());

        classUnderTest.deleteDsmTenant(tenantId);

        verify(dsmLogInClient).endSession(anyString());
    }

    @Test(expected = DsmClientException.class)
    public void deleteDsmTenantTest_throwsManagerAuthenticationException_Exception() throws Exception {
        String tenantId = TENANT_ID.toString();

        when(dsmLogInClient.connectToDSMClient(anyString(), anyString()))
                .thenThrow(new ManagerAuthenticationException_Exception());

        classUnderTest.deleteDsmTenant(tenantId);

        verify(dsmLogInClient).endSession(anyString());
    }

    @Test(expected = DsmClientException.class)
    public void deleteDsmTenantTest_throwsManagerException_Exception() throws Exception {
        String tenantId = TENANT_ID.toString();

        when(dsmLogInClient.connectToDSMClient(anyString(), anyString()))
                .thenThrow(new ManagerException_Exception());

        classUnderTest.deleteDsmTenant(tenantId);

        verify(dsmLogInClient).endSession(anyString());
    }
}