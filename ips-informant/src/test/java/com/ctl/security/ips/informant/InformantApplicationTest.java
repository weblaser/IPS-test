package com.ctl.security.ips.informant;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class InformantApplicationTest {
    @InjectMocks
    InformantApplication classUnderTest;

    @Test @Ignore
    //TODO: Move this test to ips-test because it is an integration test, additionally it requires the -Dspring.profiles.active=ts to be set
    public void main_startsInformant() throws Exception {

        Exception exception = null;
        try {
            classUnderTest.main(new String[]{});
        } catch (Exception e) {
            exception = e;
            e.printStackTrace();
        }

        assertNull(exception);
    }
}