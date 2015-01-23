package com.ctl.security.dsm.domain;

import manager.SecurityProfileTransport;
import org.springframework.stereotype.Component;

/**
 * Created by kevin.wilde on 1/20/2015.
 */

@Component
public class SecurityProfileTransportMarshaller {

    public SecurityProfileTransport convert(CtlSecurityProfile ctlSecurityProfile) {
        SecurityProfileTransport securityProfileTransport = new SecurityProfileTransport();
        securityProfileTransport.setName("name");
        return securityProfileTransport;
    }

    public CtlSecurityProfile convert(SecurityProfileTransport securityProfileTransport){
        CtlSecurityProfile ctlSecurityProfile = new CtlSecurityProfile();
        ctlSecurityProfile.setName("name");
        return ctlSecurityProfile;
    }

}
