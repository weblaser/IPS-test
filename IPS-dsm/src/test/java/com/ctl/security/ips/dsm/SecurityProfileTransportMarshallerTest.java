package com.ctl.security.ips.dsm;

import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.dsm.domain.SecurityProfileTransportMarshaller;
import manager.SecurityProfileTransport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

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
        Integer parentId = new Integer(7);
        String name = "name" + System.currentTimeMillis();
        Policy policy = new Policy();
        policy.setVendorPolicyId(id.toString());
        policy.setParentPolicyId(parentId.toString());
        policy.setName(name);

        SecurityProfileTransport securityProfileTransport = securityProfileTransportMarshaller.convert(policy);

        assertNotNull(securityProfileTransport);
        assertEquals(securityProfileTransport.getName(), name);
        assertEquals(securityProfileTransport.getID(), id);
        assertEquals(securityProfileTransport.getParentSecurityProfileID(), parentId);
    }

    @Test
    public void convertPolicyToSecurityProfileTransport_handlesNullMembers(){
        String id = null;
        String name = null;
        Policy policy = new Policy();
        policy.setVendorPolicyId(id);
        policy.setParentPolicyId(id);
        policy.setName(name);

        SecurityProfileTransport securityProfileTransport = securityProfileTransportMarshaller.convert(policy);

        assertNotNull(securityProfileTransport);
        assertEquals(securityProfileTransport.getName(), name);
        assertEquals(securityProfileTransport.getID(), id);
        assertEquals(securityProfileTransport.getParentSecurityProfileID(), id);
    }

    @Test
    public void convertSecurityProfileTransportToPolicy_marshallsSecurityProfileTransportToCtlSecurityProfile(){
        Integer id = new Integer(0);
        int parentId = 7;
        String name = "name" + System.currentTimeMillis();
        SecurityProfileTransport securityProfileTransport = new SecurityProfileTransport();
        securityProfileTransport.setID(id);
        securityProfileTransport.setName(name);
        securityProfileTransport.setParentSecurityProfileID(parentId);

        Policy policy = securityProfileTransportMarshaller.convert(securityProfileTransport);

        assertNotNull(policy);
        assertEquals(policy.getName(), name);
        assertEquals(policy.getVendorPolicyId(), id.toString());
        assertEquals(policy.getParentPolicyId(), Integer.toString(parentId));
    }


    @Test
    public void convertSecurityProfileTransportToPolicy_handlesNullMembers(){
        Integer id = null;
        String name = null;
        SecurityProfileTransport securityProfileTransport = new SecurityProfileTransport();
        securityProfileTransport.setID(id);
        securityProfileTransport.setName(name);
        securityProfileTransport.setParentSecurityProfileID(id);

        Policy policy = securityProfileTransportMarshaller.convert(securityProfileTransport);

        assertNotNull(policy);
        assertEquals(policy.getName(), name);
        assertNull(policy.getVendorPolicyId());
        assertNull(policy.getParentPolicyId());
    }

}
