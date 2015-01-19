package com.ctl.security.ips.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by kevin.wilde on 1/19/2015.
 */

@RunWith(MockitoJUnitRunner.class)
public class PolicyServiceTest {

    @InjectMocks
    private PolicyService classUnderTest;

    @Test
    public void createPolicy_createsPolicy(){
        classUnderTest.createPolicy();
    }

}
