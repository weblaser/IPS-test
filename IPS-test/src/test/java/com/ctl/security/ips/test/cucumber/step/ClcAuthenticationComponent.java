package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.clc.client.common.domain.ClcAuthenticationRequest;
import com.ctl.security.clc.client.common.domain.ClcAuthenticationResponse;
import com.ctl.security.clc.client.core.bean.AuthenticationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by sean.robb on 3/5/2015.
 */

@Component
public class ClcAuthenticationComponent {

    private static final String VALID_USERNAME = "security.test.user";
    private static final String VALID_PASSWORD = "wNeDVpD6cQBf3X";
    public static final String VALID_AA = "SCDV";

    @Autowired
    private AuthenticationClient authenticationClient;

    public ClcAuthenticationResponse authenticate() {
        ClcAuthenticationResponse clcAuthenticationResponse = authenticationClient.authenticateV2Api(new ClcAuthenticationRequest(VALID_USERNAME, VALID_PASSWORD));
        return clcAuthenticationResponse;
    }

}
