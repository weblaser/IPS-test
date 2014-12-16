package com.ctl.security.ips.api.config;

import com.wordnik.swagger.jaxrs.config.BeanConfig;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApiConfigTest {

    @Test
    public void testSwaggerConfig() throws Exception {
        //arrange
        ApiConfig underTest = new ApiConfig();

        //act
        BeanConfig actual = underTest.swaggerConfig();

        //assert
        assertEquals(ApiConfig.COM_CTL_SECURITY_IPS_API, actual.getResourcePackage());
        assertEquals(ApiConfig.IPS, actual.getBasePath());
        assertEquals(ApiConfig.API_VERSION, actual.getVersion());
        assertEquals(ApiConfig.IPS_API, actual.getTitle());
        assertEquals(ApiConfig.CENTURY_LINK_API_FOR_MANAGED_IPS_SERVICES, actual.getDescription());
        assertTrue(actual.getScan());
    }
}
