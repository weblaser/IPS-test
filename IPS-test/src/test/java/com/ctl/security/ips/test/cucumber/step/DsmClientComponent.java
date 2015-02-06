package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by kevin.wilde on 2/6/2015.
 */

@Component
public class DsmClientComponent {

    public void verifyDsmPolicyCreation(DsmPolicyClient dsmPolicyClient, Policy newlyCreatedCtlPolicy) throws DsmPolicyClientException {
        Policy retrievedPolicy = dsmPolicyClient.retrieveSecurityProfileById(new Integer(newlyCreatedCtlPolicy.getId()).intValue());
        assertNotNull(retrievedPolicy);
        assertEquals(newlyCreatedCtlPolicy.getName(), retrievedPolicy.getName());

        dsmPolicyClient.securityProfileDelete(Arrays.asList(NumberUtils.createInteger(retrievedPolicy.getId())));

        Policy deletedPolicy = dsmPolicyClient.retrieveSecurityProfileById(NumberUtils.createInteger(retrievedPolicy.getId()));
        assertTrue(deletedPolicy.getId() == null);
    }

    public void verifyDsmPolicyCreation(DsmPolicyClient dsmPolicyClient, String newlyCreatedCtlPolicyId) throws DsmPolicyClientException {
        Policy retrievedPolicy = dsmPolicyClient.retrieveSecurityProfileById(new Integer(newlyCreatedCtlPolicyId).intValue());
        assertNotNull(retrievedPolicy);
//        assertEquals(newlyCreatedCtlPolicy.getName(), retrievedPolicy.getName());

        dsmPolicyClient.securityProfileDelete(Arrays.asList(NumberUtils.createInteger(retrievedPolicy.getId())));

        Policy deletedPolicy = dsmPolicyClient.retrieveSecurityProfileById(NumberUtils.createInteger(retrievedPolicy.getId()));
        assertTrue(deletedPolicy.getId() == null);
    }
}
