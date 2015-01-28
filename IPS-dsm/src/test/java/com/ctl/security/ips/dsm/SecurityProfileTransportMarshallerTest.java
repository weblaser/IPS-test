package com.ctl.security.ips.dsm;

import com.ctl.security.ips.dsm.domain.CtlSecurityProfile;
import com.ctl.security.ips.dsm.domain.SecurityProfileTransportMarshaller;
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
        String name = "name" + System.currentTimeMillis();
        CtlSecurityProfile ctlSecurityProfile = new CtlSecurityProfile();
        ctlSecurityProfile.setName(name);

        SecurityProfileTransport securityProfileTransport = securityProfileTransportMarshaller.convert(ctlSecurityProfile);

        assertNotNull(securityProfileTransport);
        assertEquals(securityProfileTransport.getName(), name);
    }
    @Test
    public void securityProfileTransportMarshaller_marshallsSecurityProfileTransportToCtlSecurityProfile(){
        String name = "name" + System.currentTimeMillis();
        SecurityProfileTransport securityProfileTransport = new SecurityProfileTransport();
        securityProfileTransport.setName(name);

        CtlSecurityProfile ctlSecurityProfile = securityProfileTransportMarshaller.convert(securityProfileTransport);

        assertNotNull(ctlSecurityProfile);
        assertEquals(ctlSecurityProfile.getName(), name);
    }

}
