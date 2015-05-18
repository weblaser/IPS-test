package com.ctl.security.ips.dsm.factory;

import com.ctl.security.clc.client.common.domain.ClcExecutePackageRequest;
import com.ctl.security.clc.client.common.domain.ClcServerDetailsResponse;
import com.ctl.security.clc.client.core.bean.ServerClient;
import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.common.domain.SecurityTenant;
import com.ctl.security.ips.common.jms.bean.PolicyBean;
import com.ctl.security.ips.dsm.exception.AgentInstallException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by Chad Middleton on 4/20/2015.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DsmAgentInstallPackageFactoryTest {

    private static final String SERVER = "VM2BUSED";
    private static final String REDHAT = "redHat6_64Bit";
    private static final String WINDOWS = "windows2008_64Bit";
    private static final String REDHAT_6_UUID = "redHat6_uuid";
    private static final String WINDOWS_2008_UUID = "windows2008_uuid";
    private static final String DSM_MOTHERSHIP = "DSM Mothership";

    @Mock
    private ClcServerDetailsResponse serverDetailsResponse;

    @InjectMocks
    private DsmAgentInstallPackageFactory classUnderTest;

    @Mock
    private ServerClient serverClient;

    @Before
    public void init(){
        HashMap<String, String> osOptions= new HashMap<>();
        osOptions.put(REDHAT, REDHAT_6_UUID);
        osOptions.put(WINDOWS, WINDOWS_2008_UUID);
        ReflectionTestUtils.setField(classUnderTest, "osOptions", osOptions);
        ReflectionTestUtils.setField(classUnderTest, "dsmMothership", DSM_MOTHERSHIP);
    }

    @Test
    public void testConfigurePackageRequest_GetsCorrectPackageId() throws Exception {
        //arrange
        Policy pulledInPolicy = new Policy().setHostName(SERVER);
        PolicyBean policyBean = new PolicyBean(null,pulledInPolicy,null);
        SecurityTenant securityTenant = new SecurityTenant();
        ClcExecutePackageRequest expected = new ClcExecutePackageRequest().addServer(SERVER);
        expected.getSoftwarePackage().setPackageId(REDHAT_6_UUID);

        when(serverClient.getServerDetails(anyString(), eq(SERVER), anyString())).thenReturn(serverDetailsResponse);
        when(serverDetailsResponse.getOs()).thenReturn(REDHAT);

        //act
        ClcExecutePackageRequest result = classUnderTest.configurePackageRequest(securityTenant, policyBean);
        //assert
        assertNotNull(result);
        assertEquals(expected.getSoftwarePackage().getPackageId(), result.getSoftwarePackage().getPackageId());
    }

    @Test
    public void testConfigurePackageRequest_GetsCorrectParameters() throws Exception {
        //arrange
        Policy pulledInPolicy = new Policy().setHostName(SERVER);
        PolicyBean policyBean = new PolicyBean(null,pulledInPolicy,null);
        SecurityTenant securityTenant = new SecurityTenant();
        ClcExecutePackageRequest expected = new ClcExecutePackageRequest().addServer(SERVER);
        expected.getSoftwarePackage().setPackageId(REDHAT_6_UUID);
        expected.getSoftwarePackage()
                .addParameter("DSM.Tenant.ID", securityTenant.getGuid())
                .addParameter("DSM.Agent.Activation.Password", securityTenant.getAgentInitiatedActivationPassword())
                .addParameter("DSM.Policy.ID", pulledInPolicy.getVendorPolicyId())
                .addParameter("DSM.Name", DSM_MOTHERSHIP)
                .addParameter("T3.Bearer.Token", policyBean.getBearerToken())
                .addParameter("T3.Account.Alias", policyBean.getAccountAlias());

        when(serverClient.getServerDetails(anyString(), eq(SERVER), anyString())).thenReturn(serverDetailsResponse);
        when(serverDetailsResponse.getOs()).thenReturn(REDHAT);

        //act
        ClcExecutePackageRequest result = classUnderTest.configurePackageRequest(securityTenant, policyBean);
        //assert
        assertNotNull(result);
        assertEquals(expected.getSoftwarePackage().getParameters(), result.getSoftwarePackage().getParameters());
    }

    @Test(expected = AgentInstallException.class)
    public void testConfigurePackageRequest_UnmatchedOS() throws Exception {
        //arrange
        Policy pulledInPolicy = new Policy().setHostName(SERVER);
        PolicyBean policyBean = new PolicyBean(null,pulledInPolicy,null);
        SecurityTenant securityTenant = new SecurityTenant();
        ClcExecutePackageRequest expected = new ClcExecutePackageRequest().addServer(SERVER);
        expected.getSoftwarePackage().setPackageId(REDHAT_6_UUID);

        when(serverClient.getServerDetails(anyString(), eq(SERVER), anyString())).thenReturn(serverDetailsResponse);
        when(serverDetailsResponse.getOs()).thenReturn("SomeInvalidOs");

        //act
        classUnderTest.configurePackageRequest(securityTenant, policyBean);
    }

    @Test(expected = AgentInstallException.class)
    public void testConfigurePackageRequest_NullOS() throws Exception {
        //arrange
        Policy pulledInPolicy = new Policy().setHostName(SERVER);
        PolicyBean policyBean = new PolicyBean(null,pulledInPolicy,null);
        SecurityTenant securityTenant = new SecurityTenant();
        ClcExecutePackageRequest expected = new ClcExecutePackageRequest().addServer(SERVER);
        expected.getSoftwarePackage().setPackageId(REDHAT_6_UUID);

        when(serverClient.getServerDetails(anyString(), eq(SERVER), anyString())).thenReturn(serverDetailsResponse);
        when(serverDetailsResponse.getOs()).thenReturn(null);

        //act
        ClcExecutePackageRequest result = classUnderTest.configurePackageRequest(securityTenant, policyBean);
        assertNull(result);
    }

    @Test
    public void testConfigurePackageRequest_GetsWindowsPackageId() throws Exception {
        //arrange
        Policy pulledInPolicy = new Policy().setHostName(SERVER);
        PolicyBean policyBean = new PolicyBean(null,pulledInPolicy,null);
        SecurityTenant securityTenant = new SecurityTenant();
        ClcExecutePackageRequest expected = new ClcExecutePackageRequest().addServer(SERVER);
        expected.getSoftwarePackage().setPackageId(WINDOWS_2008_UUID);

        when(serverClient.getServerDetails(anyString(), eq(SERVER), anyString())).thenReturn(serverDetailsResponse);
        when(serverDetailsResponse.getOs()).thenReturn(WINDOWS);

        //act
        ClcExecutePackageRequest result = classUnderTest.configurePackageRequest(securityTenant, policyBean);
        //assert
        assertNotNull(result);
        assertEquals(expected.getSoftwarePackage().getPackageId(), result.getSoftwarePackage().getPackageId());
    }
}