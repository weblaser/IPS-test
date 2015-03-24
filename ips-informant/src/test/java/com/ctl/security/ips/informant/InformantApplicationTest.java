package com.ctl.security.ips.informant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class InformantApplicationTest {
    @InjectMocks
    InformantApplication classUnderTest;

    @Test
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