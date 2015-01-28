package com.ctl.security.ips.dao;

import com.ctl.security.ips.dsm.domain.CtlSecurityProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

/**
 * Created by kevin.wilde on 1/23/2015.
 */

@RunWith(MockitoJUnitRunner.class)
public class PolicyDaoTest {

    @InjectMocks
    private PolicyDao classUnderTest;

    @Test
    public void saveCtlSecurityProfile_savesCtlSecurityProfile(){
        CtlSecurityProfile ctlSecurityProfileToBeCreated = new CtlSecurityProfile();

        CtlSecurityProfile newlyCreatedCtlSecurityProfile = classUnderTest.saveCtlSecurityProfile(ctlSecurityProfileToBeCreated);

        assertNotNull(newlyCreatedCtlSecurityProfile);
    }
}
