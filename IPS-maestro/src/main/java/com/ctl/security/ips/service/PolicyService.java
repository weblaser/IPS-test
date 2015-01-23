package com.ctl.security.ips.service;

import com.ctl.security.dsm.DsmPolicyClient;
import com.ctl.security.dsm.domain.CtlSecurityProfile;
import com.ctl.security.dsm.exception.DsmPolicyClientException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by kevin.wilde on 1/19/2015.
 */
public class PolicyService {

    @Autowired
    private DsmPolicyClient dsmPolicyClient;

    public CtlSecurityProfile createPolicy(CtlSecurityProfile ctlSecurityProfileToBeCreated) throws DsmPolicyClientException {
        CtlSecurityProfile newlyCreatedCtlSecurityProfile = null;
        newlyCreatedCtlSecurityProfile = dsmPolicyClient.createCtlSecurityProfile(ctlSecurityProfileToBeCreated);
        return newlyCreatedCtlSecurityProfile;
    }
}
