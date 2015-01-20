package com.ctl.security.dsm;

import com.ctl.security.dsm.domain.CtlSecurityProfile;
import com.ctl.security.dsm.domain.SecurityProfileTransportMarshaller;
import manager.SecurityProfileTransport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by kevin.wilde on 1/20/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class SecurityProfileTransportMarshallerTest {

    @InjectMocks
    private SecurityProfileTransportMarshaller securityProfileTransportMarshaller;

    @Test
    public void securityProfileTransportMarshaller_marshallsCtlSecurityProfileToSecurityProfileTransport(){
        String name = "name";
        CtlSecurityProfile ctlSecurityProfile = null;

        SecurityProfileTransport securityProfileTransport = securityProfileTransportMarshaller.convert(ctlSecurityProfile);

        assertNotNull(securityProfileTransport);
        assertEquals(securityProfileTransport.getName(), name);
    }

}
