package com.ctl.security.ips.test.cucumber.step;

import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.dsm.DsmPolicyClient;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by kevin.wilde on 2/6/2015.
 *
 */

@Component
public class DsmClientComponent {

    public void verifyDsmPolicyCreation(DsmPolicyClient dsmPolicyClient, Policy newlyCreatedCtlPolicy, boolean cleanup) throws DsmClientException, InterruptedException {
        assertNotNull(newlyCreatedCtlPolicy);

        Policy retrievedPolicy = null;

        int i = 0;
        int maxTries = 10;
        while(i < maxTries && (retrievedPolicy == null || retrievedPolicy.getName() == null)){
            retrievedPolicy = dsmPolicyClient.retrieveSecurityProfileByName(newlyCreatedCtlPolicy.getName());
            Thread.sleep(1000);
            i++;
        }


        assertNotNull(retrievedPolicy);
        assertNotNull("expected: " + newlyCreatedCtlPolicy.getName(), retrievedPolicy.getName());

        //The mock needs to be improved to allow for retrieving a specific policy by name.
//        assertEquals(newlyCreatedCtlPolicy.getName(), retrievedPolicy.getName());

        if(cleanup){
            cleanupPolicy(dsmPolicyClient, retrievedPolicy);
        }
    }

    private void cleanupPolicy(DsmPolicyClient dsmPolicyClient, Policy retrievedPolicy) throws DsmClientException {
        dsmPolicyClient.securityProfileDelete(Arrays.asList(NumberUtils.createInteger(retrievedPolicy.getVendorPolicyId())));

        Policy deletedPolicy = dsmPolicyClient.retrieveSecurityProfileById(NumberUtils.createInteger(retrievedPolicy.getVendorPolicyId()));
        assertTrue(deletedPolicy.getVendorPolicyId() == null);
    }


}
