package com.ctl.security.ips.api.config;

import com.wordnik.swagger.jaxrs.config.BeanConfig;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResourceConfigTest {

    @Test
    public void testSwaggerConfig() throws Exception {
        //arrange
        ResourceConfig underTest = new ResourceConfig();

        //act
        BeanConfig actual = underTest.swaggerConfig();

        //assert
        assertEquals(ResourceConfig.COM_CTL_SECURITY_IPS_API, actual.getResourcePackage());
        assertEquals(ResourceConfig.IPS, actual.getBasePath());
        assertEquals(ResourceConfig.API_VERSION, actual.getVersion());
        assertEquals(ResourceConfig.IPS_API, actual.getTitle());
        assertEquals(ResourceConfig.CENTURY_LINK_API_FOR_MANAGED_IPS_SERVICES, actual.getDescription());
        assertTrue(actual.getScan());
    }
}
