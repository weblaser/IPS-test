package com.ctl.security.ips.dsm;

import com.ctl.security.ips.common.domain.Policy;
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
        Policy policy = new Policy();
        policy.setName(name);

        SecurityProfileTransport securityProfileTransport = securityProfileTransportMarshaller.convert(policy);

        assertNotNull(securityProfileTransport);
        assertEquals(securityProfileTransport.getName(), name);
    }
    @Test
    public void securityProfileTransportMarshaller_marshallsSecurityProfileTransportToCtlSecurityProfile(){
        String name = "name" + System.currentTimeMillis();
        SecurityProfileTransport securityProfileTransport = new SecurityProfileTransport();
        securityProfileTransport.setName(name);

        Policy policy = securityProfileTransportMarshaller.convert(securityProfileTransport);

        assertNotNull(policy);
        assertEquals(policy.getName(), name);
    }

}
