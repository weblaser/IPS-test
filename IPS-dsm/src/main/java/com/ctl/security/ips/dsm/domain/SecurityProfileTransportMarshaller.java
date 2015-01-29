package com.ctl.security.ips.dsm.domain;

import com.ctl.security.ips.common.domain.Policy;
import manager.SecurityProfileTransport;
import org.springframework.stereotype.Component;

/**
 * Created by kevin.wilde on 1/20/2015.
 */

@Component
public class SecurityProfileTransportMarshaller {

    public SecurityProfileTransport convert(Policy policy) {
        SecurityProfileTransport securityProfileTransport = new SecurityProfileTransport();
        securityProfileTransport.setName(policy.getName());
        return securityProfileTransport;
    }

    public Policy convert(SecurityProfileTransport securityProfileTransport){
        Policy policy = new Policy();
        policy.setName(securityProfileTransport.getName());
        return policy;
    }

}
