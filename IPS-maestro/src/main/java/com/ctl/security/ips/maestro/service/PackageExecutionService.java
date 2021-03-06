package com.ctl.security.ips.maestro.service;


import com.ctl.security.clc.client.common.domain.ClcExecutePackageRequest;
import com.ctl.security.clc.client.common.domain.ClcExecutePackageResponse;
import com.ctl.security.clc.client.core.bean.ServerClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by Chad.Middleton on 3/25/2015
 */
@Service
public class PackageExecutionService {

    public static final String SUCCEEDED = "succeeded";
    public static final String FAILED = "failed";

    @Autowired
    private ServerClient serverClient;

    @Value("${${spring.profiles.active:local}.ips.packageStatusCheck.maxRetryAttempts}")
    private Integer packageStatusCheckMaxRetryAttempts;

    @Value("${${spring.profiles.active:local}.ips.packageStatusCheck.retryWaitTime}")
    private Integer packageStatusCheckRetryWaitTime;


    private static final Logger logger = LogManager.getLogger(PackageExecutionService.class);

    public String executePackage(ClcExecutePackageRequest clcExecutePackageRequest, String accountAlias, String bearerToken) {
        logger.info("Installing CLC Package: "+clcExecutePackageRequest.getSoftwarePackage().getPackageId());
        List<ClcExecutePackageResponse> clcExecutePackageResponses = serverClient.executePackage(clcExecutePackageRequest, accountAlias, bearerToken);
        int counter = 0;
        String response = new String();
        while (!SUCCEEDED.equals(response) && !FAILED.equals(response) && counter < packageStatusCheckMaxRetryAttempts) {
            if(counter > 0){
                sleep();
            }
            response = serverClient.getPackageStatus(clcExecutePackageResponses.get(0).getLinks().get(0).getId(), accountAlias, bearerToken);
            counter++;
        }
        return response;
    }

    private void sleep() {
        try {
            Thread.sleep(packageStatusCheckRetryWaitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
