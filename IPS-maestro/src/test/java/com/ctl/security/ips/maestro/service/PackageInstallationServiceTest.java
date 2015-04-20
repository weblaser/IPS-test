package com.ctl.security.ips.maestro.service;

import com.ctl.security.clc.client.common.domain.ClcExecutePackageRequest;
import com.ctl.security.clc.client.common.domain.ClcExecutePackageResponse;
import com.ctl.security.clc.client.common.domain.Link;
import com.ctl.security.clc.client.core.bean.ServerClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PackageInstallationServiceTest {

    private static final String TEST_ALIAS = "SCDV";
    private static final String TEST_TOKEN = "someToken";
    private static final String TEST_SERVER = "Server";
    private static final String TEST_ID = "someId";
    public static final int PACKAGE_STATUS_CHECK_MAX_RETRY_ATTEMPTS = 20;
    public static final int PACKAGE_STATUS_CHECK_MAX_WAIT_TIME = 0;
    @InjectMocks
    private PackageInstallationService classUnderTest;

    @Mock
    private ServerClient serverClient;

    @Before
    public void setup(){
        ReflectionTestUtils.setField(classUnderTest, "packageStatusCheckMaxRetryAttempts", PACKAGE_STATUS_CHECK_MAX_RETRY_ATTEMPTS);
        ReflectionTestUtils.setField(classUnderTest, "packageStatusCheckRetryWaitTime", PACKAGE_STATUS_CHECK_MAX_WAIT_TIME);
    }

    @Test
    public void testInstallClcPackage_InstallationClcPackageExecuted() throws Exception {
        //arrange
        List<Link> links = Arrays.asList(new Link().setHref("/fakePath").setId(TEST_ID).setRel("status"));
        List<ClcExecutePackageResponse> executePackageResponses = Arrays.asList(new ClcExecutePackageResponse()
                .setIsQueued(true)
                .setServer(TEST_SERVER)
                .setLinks(links));
        when(serverClient.executePackage(any(ClcExecutePackageRequest.class), anyString(), anyString())).thenReturn(executePackageResponses);
        when(serverClient.getPackageStatus(TEST_ID, TEST_ALIAS, TEST_TOKEN)).thenReturn("notStarted", "succeeded");

        //act
        String result = classUnderTest.installClcPackage(new ClcExecutePackageRequest(), TEST_ALIAS, TEST_TOKEN);

        //assert
        assertEquals("succeeded", result);
    }

    @Test(expected = RestClientException.class)
    public void testInstallClcPackage_failureToExecutePackage() {
        //arrange
        when(serverClient.executePackage(any(ClcExecutePackageRequest.class), anyString(), anyString())).thenThrow(new RestClientException("whatever"));
        when(serverClient.getPackageStatus(TEST_ID, TEST_ALIAS, TEST_TOKEN)).thenReturn("notStarted", "succeeded");

        //act
        classUnderTest.installClcPackage(new ClcExecutePackageRequest(), TEST_ALIAS, TEST_TOKEN);
    }

    @Test(expected = RestClientException.class)
    public void testInstallClcPackage_failureToGetPackageStatus() {
        //arrange
        when(serverClient.executePackage(any(ClcExecutePackageRequest.class), anyString(), anyString())).thenThrow(new RestClientException("whatever"));
        when(serverClient.getPackageStatus(TEST_ID, TEST_ALIAS, TEST_TOKEN)).thenReturn("notStarted", "succeeded");

        //act
        classUnderTest.installClcPackage(new ClcExecutePackageRequest(), TEST_ALIAS, TEST_TOKEN);
    }
}