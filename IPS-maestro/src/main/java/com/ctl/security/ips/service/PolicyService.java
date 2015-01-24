package com.ctl.security.ips.service;

import com.ctl.security.dsm.DsmPolicyClient;
import com.ctl.security.dsm.domain.CtlSecurityProfile;
import com.ctl.security.dsm.exception.DsmPolicyClientException;
import com.ctl.security.ips.dao.PolicyDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by kevin.wilde on 1/19/2015.
 */
public class PolicyService {

    private DsmPolicyClient dsmPolicyClient;
    private PolicyDao policyDao;

    @Autowired
    public PolicyService(DsmPolicyClient dsmPolicyClient, PolicyDao policyDao) {
        this.dsmPolicyClient = dsmPolicyClient;
        this.policyDao = policyDao;
    }

    public CtlSecurityProfile createPolicy(CtlSecurityProfile ctlSecurityProfileToBeCreated) throws DsmPolicyClientException {
        CtlSecurityProfile newlyCreatedCtlSecurityProfile = dsmPolicyClient.createCtlSecurityProfile(ctlSecurityProfileToBeCreated);
        CtlSecurityProfile newlyPersistedCtlSecurityProfile = policyDao.saveCtlSecurityProfile(newlyCreatedCtlSecurityProfile);
        return newlyPersistedCtlSecurityProfile;
    }
}
