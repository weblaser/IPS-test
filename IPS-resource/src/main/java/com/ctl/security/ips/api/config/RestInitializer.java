package com.ctl.security.ips.api.config;

import com.ctl.security.ips.api.jersey.JerseyApplication;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.*;
import java.util.EnumSet;


@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestInitializer implements WebApplicationInitializer {

	@Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Listeners
        servletContext.addListener(ContextLoaderListener.class);
        servletContext.setInitParameter(ContextLoader.CONTEXT_CLASS_PARAM, AnnotationConfigWebApplicationContext.class.getName());
        servletContext.setInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, ApiConfig.class.getName());

        // Register Jersey 2.0 servlet
        ServletRegistration.Dynamic servletRegistration = servletContext.addServlet("servlet", ServletContainer.class.getName());
        servletRegistration.setInitParameter("javax.ws.rs.Application", JerseyApplication.class.getName());
        servletRegistration.setInitParameter("jersey.config.server.provider.classnames", LoggingFilter.class.getName());
        servletRegistration.setLoadOnStartup(1);
        servletRegistration.addMapping("/api/*");
//
//        FilterRegistration.Dynamic springSecurityFilterChain = servletContext.addFilter("springSecurityFilterChain", DelegatingFilterProxy.class.getName());
//        springSecurityFilterChain.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*" );

    }

}