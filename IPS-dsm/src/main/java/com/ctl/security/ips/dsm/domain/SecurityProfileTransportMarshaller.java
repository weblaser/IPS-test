package com.ctl.security.ips.dsm.domain;

import com.ctl.security.ips.common.domain.Policy.Policy;
import manager.SecurityProfileTransport;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;


/**
 * Created by kevin.wilde on 1/20/2015.
 *
 */

@Component
public class SecurityProfileTransportMarshaller {

    public SecurityProfileTransport convert(Policy policy) {
        SecurityProfileTransport securityProfileTransport = new SecurityProfileTransport();
        securityProfileTransport.setID(NumberUtils.createInteger(policy.getVendorPolicyId()));
        securityProfileTransport.setName(policy.getName());
        securityProfileTransport.setParentSecurityProfileID(NumberUtils.createInteger(policy.getParentPolicyId()));
        return securityProfileTransport;
    }

    public Policy convert(SecurityProfileTransport securityProfileTransport){
        Policy policy = new Policy();
        if(securityProfileTransport.getID() != null){
            policy.setVendorPolicyId(securityProfileTransport.getID().toString());
            policy.setParentPolicyId(securityProfileTransport.getParentSecurityProfileID().toString());
        }
        policy.setName(securityProfileTransport.getName());
        return policy;
    }

}
