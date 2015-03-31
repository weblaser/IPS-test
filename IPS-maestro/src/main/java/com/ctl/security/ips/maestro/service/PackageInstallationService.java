package com.ctl.security.ips.maestro.service;


import com.ctl.security.clc.client.common.domain.ClcExecutePackageRequest;
import com.ctl.security.clc.client.common.domain.ClcExecutePackageResponse;
import com.ctl.security.clc.client.core.bean.ServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by Chad.Middleton on 3/25/2015
 */
@Service
public class PackageInstallationService {

    @Autowired
    private ServerClient serverClient;

    public String installClcPackage(ClcExecutePackageRequest clcExecutePackageRequest, String accountAlias, String bearerToken) {
        List<ClcExecutePackageResponse> clcExecutePackageResponses = serverClient.executePackage(clcExecutePackageRequest, accountAlias, bearerToken);
        int counter = 0;
        String response;
        do {
            response = serverClient.getPackageStatus(clcExecutePackageResponses.get(0).getLinks().get(0).getId(), accountAlias, bearerToken);
            counter++;
            sleep();
        }
        while (!response.equals("succeeded") && !response.equals("failed") && counter < 20);
        return response;
    }

    private void sleep() {
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
