package com.ctl.security.ips.dao;

import com.ctl.security.ips.common.domain.Policy;
import org.springframework.stereotype.Component;

/**
 * Created by kevin.wilde on 1/23/2015.
 */

@Component
public class PolicyDao {

    public Policy saveCtlSecurityProfile(Policy policyToBeCreated) {
        return new Policy();
    }
}
