package com.ctl.security.ips.cli;

import com.ctl.security.ips.client.PolicyClient;
import com.ctl.security.ips.common.domain.Policy.Policy;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by kevin on 3/19/15.
 */

@Component
public class IpsApiCliDelegator {

    //TODO: After story SECURITY-755 is completed switch to org.apache.logging.log4j.LogManager
    private static final Logger logger = LoggerFactory.getLogger(IpsApiCliDelegator.class);

    public static final String GET_POLICY_FOR_ACCOUNT = "getPolicyForAccount";

    @Autowired
    private PolicyClient policyClient;

    @Autowired
    private Gson gson;

    public void execute(String[] args) {


        if(args != null && StringUtils.equals( args[0], GET_POLICY_FOR_ACCOUNT)){
            getPolicyForAccount(args);
        } else {
            System.err.println("No valid operation provided.");
            System.err.println("args: " + Arrays.toString(args));
        }


    }

    private void getPolicyForAccount(String[] args) {

        String bearerToken = args[1];
        String accountId = args[2];
        String id = args[3];

        Policy policy = policyClient.getPolicyForAccount(accountId, id, bearerToken);

        String policyJson = gson.toJson(policy);

        System.out.println(policyJson);
        logger.info(policyJson);
    }
}
