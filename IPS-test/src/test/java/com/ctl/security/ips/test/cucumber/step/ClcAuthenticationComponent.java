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

    private static final String VALID_USERNAME = "Bugs";
    private static final String VALID_PASSWORD = "vZb]9yKv==Bnmozn";
    static final String VALID_AA = "TCCD";

    @Autowired
    private AuthenticationClient authenticationClient;

    public String authenticate() {
        ClcAuthenticationResponse clcAuthenticationResponse = authenticationClient.authenticateV2Api(new ClcAuthenticationRequest(VALID_USERNAME, VALID_PASSWORD));
        String bearerToken = clcAuthenticationResponse.getBearerToken();
        return bearerToken;
    }

}
