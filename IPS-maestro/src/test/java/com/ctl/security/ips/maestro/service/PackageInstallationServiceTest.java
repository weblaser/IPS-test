package com.ctl.security.ips.maestro.service;

import com.ctl.security.clc.client.common.domain.ClcExecutePackageRequest;
import com.ctl.security.clc.client.common.domain.ClcExecutePackageResponse;
import com.ctl.security.clc.client.common.domain.Link;
import com.ctl.security.clc.client.common.exception.PackageExecutionException;
import com.ctl.security.clc.client.common.exception.PackageStatusException;
import com.ctl.security.clc.client.core.bean.ServerClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    @InjectMocks
    private PackageInstallationService classUnderTest;

    @Mock
    private ServerClient serverClient;

    @Test
    public void testInstallClcPackage_InsallationClcPackageExecuted() throws Exception {
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

    @Test(expected = PackageExecutionException.class)
    public void testInstallClcPackage_failureToExecutePackage() {
        //arrange
        when(serverClient.executePackage(any(ClcExecutePackageRequest.class), anyString(), anyString())).thenThrow(new PackageExecutionException());
        when(serverClient.getPackageStatus(TEST_ID, TEST_ALIAS, TEST_TOKEN)).thenReturn("notStarted", "succeeded");

        //act
        classUnderTest.installClcPackage(new ClcExecutePackageRequest(), TEST_ALIAS, TEST_TOKEN);
    }

    @Test(expected = PackageStatusException.class)
    public void testInstallClcPackage_failureToGetPackageStatus() {
        //arrange
        when(serverClient.executePackage(any(ClcExecutePackageRequest.class), anyString(), anyString())).thenThrow(new PackageStatusException());
        when(serverClient.getPackageStatus(TEST_ID, TEST_ALIAS, TEST_TOKEN)).thenReturn("notStarted", "succeeded");

        //act
        classUnderTest.installClcPackage(new ClcExecutePackageRequest(), TEST_ALIAS, TEST_TOKEN);
    }
}