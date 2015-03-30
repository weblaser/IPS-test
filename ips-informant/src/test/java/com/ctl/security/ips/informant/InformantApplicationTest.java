package com.ctl.security.ips.informant;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InformantApplicationTest {
    @InjectMocks
    InformantApplication classUnderTest;

    @Test @Ignore
    //TODO:Verify Logging
    //TODO: Move this test to ips-test because it is an integration test, additionally it requires the -Dspring.profiles.active=ts to be set
    public void main_startsInformant() throws Exception {

    }
}