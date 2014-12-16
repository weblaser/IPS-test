package com.ctl.security.ips.api.jersey;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.log4j.Priority;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import com.ctl.security.ips.api.jersey.mapper.NotAuthorizedExceptionMapper;
import com.ctl.security.ips.api.jersey.mapper.PolicyNotFoundExceptionMapper;
import com.ctl.security.ips.api.resource.impl.PolicyResourceImpl;
import com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON;
import com.wordnik.swagger.jersey.listing.JerseyApiDeclarationProvider;
import com.wordnik.swagger.jersey.listing.JerseyResourceListingProvider;

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

        //Exception Mappers
        register(PolicyNotFoundExceptionMapper.class);
        register(NotAuthorizedExceptionMapper.class);

        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(new JerseyLog4jLoggerWrapper());

        registerInstances(new LoggingFilter(Logger.getLogger(JerseyApplication.class.getName()), true));
    }

    private class JerseyLog4jLoggerWrapper extends Handler {

        @Override
        public void publish(LogRecord record) {
            String sourceClassName = record.getSourceClassName();
            Level level = record.getLevel();

            if (sourceClassName != null
                    && (level.intValue() >= Level.WARNING.intValue() || sourceClassName
                    .contains("org.glassfish.jersey"))) {

                org.apache.log4j.Logger logger = org.apache.log4j.Logger
                        .getLogger(sourceClassName);
                logger.log(convertToLog4JPriority(level), record.getMessage());
            }
        }

        private Priority convertToLog4JPriority(Level level) {
            if (Level.ALL.equals(level)) {
                return org.apache.log4j.Level.INFO;
            } else if (Level.CONFIG.equals(level)) {
                return org.apache.log4j.Level.INFO;
            } else if (Level.FINE.equals(level)) {
                return org.apache.log4j.Level.INFO;
            } else if (Level.FINER.equals(level)) {
                return org.apache.log4j.Level.DEBUG;
            } else if (Level.FINEST.equals(level)) {
                return org.apache.log4j.Level.DEBUG;
            } else if (Level.INFO.equals(level)) {
                return org.apache.log4j.Level.INFO;
            } else if (Level.SEVERE.equals(level)) {
                return org.apache.log4j.Level.ERROR;
            } else if (Level.WARNING.equals(level)) {
                return org.apache.log4j.Level.WARN;
            } else {
                return org.apache.log4j.Level.INFO;
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }

    }
}