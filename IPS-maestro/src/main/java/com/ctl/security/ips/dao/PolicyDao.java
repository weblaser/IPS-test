package com.ctl.security.ips.dao;

import com.ctl.security.ips.dsm.domain.CtlSecurityProfile;
import org.springframework.stereotype.Component;

/**
 * Created by kevin.wilde on 1/23/2015.
 */

@Component
public class PolicyDao {

    public CtlSecurityProfile saveCtlSecurityProfile(CtlSecurityProfile ctlSecurityProfileToBeCreated) {
        return new CtlSecurityProfile();
    }
}
