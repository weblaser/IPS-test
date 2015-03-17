package com.ctl.security.ips.api.jersey;

import com.ctl.security.ips.api.jersey.mapper.NotAuthorizedExceptionMapper;
import com.ctl.security.ips.api.jersey.mapper.PolicyNotFoundExceptionMapper;
import com.ctl.security.ips.api.resource.impl.EventResourceImpl;
import com.ctl.security.ips.api.resource.impl.NotificationResourceImpl;
import com.ctl.security.ips.api.resource.impl.PolicyResourceImpl;
import com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON;
import com.wordnik.swagger.jersey.listing.JerseyApiDeclarationProvider;
import com.wordnik.swagger.jersey.listing.JerseyResourceListingProvider;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import java.util.logging.Logger;

public class JerseyApplication extends ResourceConfig {

    public JerseyApplication() {
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);
        register(JacksonFeature.class);

        //Swagger
        register(ApiListingResourceJSON.class);
        register(JerseyApiDeclarationProvider.class);
        register(JerseyResourceListingProvider.class);

        //Security Resources
        register(PolicyResourceImpl.class);
        register(NotificationResourceImpl.class);
        register(EventResourceImpl.class);

        //Exception Mappers
        register(PolicyNotFoundExceptionMapper.class);
        register(NotAuthorizedExceptionMapper.class);

        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(new JerseyLog4jLoggerWrapper());

        registerInstances(new LoggingFilter(Logger.getLogger(JerseyApplication.class.getName()), true));
    }
}