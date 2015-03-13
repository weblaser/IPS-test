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
import static org.junit.Assert.assertNull;

/**
 * Created by kevin.wilde on 1/20/2015.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SecurityProfileTransportMarshallerTest {

    @InjectMocks
    private SecurityProfileTransportMarshaller securityProfileTransportMarshaller;

    @Test
    public void convertPolicyToSecurityProfileTransport_marshallsCtlSecurityProfileToSecurityProfileTransport(){
        Integer id = new Integer(0);
        String name = "name" + System.currentTimeMillis();
        Policy policy = new Policy();
        policy.setVendorPolicyId(id.toString());
        policy.setName(name);

        SecurityProfileTransport securityProfileTransport = securityProfileTransportMarshaller.convert(policy);

        assertNotNull(securityProfileTransport);
        assertEquals(securityProfileTransport.getName(), name);
        assertEquals(securityProfileTransport.getID(), id);
    }

    @Test
    public void convertPolicyToSecurityProfileTransport_handlesNullMembers(){
        String id = null;
        String name = null;
        Policy policy = new Policy();
        policy.setVendorPolicyId(id);
        policy.setName(name);

        SecurityProfileTransport securityProfileTransport = securityProfileTransportMarshaller.convert(policy);

        assertNotNull(securityProfileTransport);
        assertEquals(securityProfileTransport.getName(), name);
        assertEquals(securityProfileTransport.getID(), id);
    }

    @Test
    public void convertSecurityProfileTransportToPolicy_marshallsSecurityProfileTransportToCtlSecurityProfile(){
        Integer id = new Integer(0);
        String name = "name" + System.currentTimeMillis();
        SecurityProfileTransport securityProfileTransport = new SecurityProfileTransport();
        securityProfileTransport.setID(id);
        securityProfileTransport.setName(name);

        Policy policy = securityProfileTransportMarshaller.convert(securityProfileTransport);

        assertNotNull(policy);
        assertEquals(policy.getName(), name);
        assertEquals(policy.getVendorPolicyId(), id.toString());
    }


    @Test
    public void convertSecurityProfileTransportToPolicy_handlesNullMembers(){
        Integer id = null;
        String name = null;
        SecurityProfileTransport securityProfileTransport = new SecurityProfileTransport();
        securityProfileTransport.setID(id);
        securityProfileTransport.setName(name);

        Policy policy = securityProfileTransportMarshaller.convert(securityProfileTransport);

        assertNotNull(policy);
        assertEquals(policy.getName(), name);
        assertNull(policy.getVendorPolicyId());
    }

}
