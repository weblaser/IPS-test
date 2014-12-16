package com.ctl.security.ips.api.jersey;

import com.ctl.security.ips.api.jersey.mapper.NotAuthorizedExceptionMapper;
import com.ctl.security.ips.api.jersey.mapper.PolicyNotFoundExceptionMapper;
import com.ctl.security.ips.api.resource.impl.PolicyResourceImpl;
import com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON;
import com.wordnik.swagger.jersey.listing.JerseyApiDeclarationProvider;
import com.wordnik.swagger.jersey.listing.JerseyResourceListingProvider;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ServerProperties;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JerseyApplicationTest {
    JerseyApplication jerseyApplication = new JerseyApplication();

    @Test
    public void constructorTest() throws Exception {
        // Features
        assertTrue(jerseyApplication.getConfiguration().isRegistered(JacksonFeature.class));

        // Resources
        assertTrue(jerseyApplication.getConfiguration().isRegistered(ApiListingResourceJSON.class));
        assertTrue(jerseyApplication.getConfiguration().isRegistered(JerseyApiDeclarationProvider.class));
        assertTrue(jerseyApplication.getConfiguration().isRegistered(JerseyResourceListingProvider.class));
        assertTrue(jerseyApplication.getConfiguration().isRegistered(PolicyResourceImpl.class));
        assertTrue(jerseyApplication.getConfiguration().isRegistered(PolicyNotFoundExceptionMapper.class));
        assertTrue(jerseyApplication.getConfiguration().isRegistered(NotAuthorizedExceptionMapper.class));


        // Properties
        assertEquals(true, jerseyApplication.getProperty(ServerProperties.BV_SEND_ERROR_IN_RESPONSE));
        assertEquals(true, jerseyApplication.getProperty(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK));
    }

}
