package com.ctl.security.ips.api.config;

import com.ctl.security.ips.api.jersey.JerseyApplication;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.*;

import java.util.EnumSet;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestInitializerTest {

    @Mock
    private ServletContext servletContext;

    @Mock
    private ServletRegistration.Dynamic servletRegistration;

    @Mock
    private FilterRegistration.Dynamic filterRegistration;

    @InjectMocks
    private RestInitializer classUnderTest = new RestInitializer();

    @Test
    public void testOnStartup() throws ServletException {

        // arrange
        when(servletContext.addServlet("servlet", ServletContainer.class.getName())).thenReturn(servletRegistration);
        when(servletContext.addFilter("springSecurityFilterChain", DelegatingFilterProxy.class.getName())).thenReturn(filterRegistration);

        // act
        classUnderTest.onStartup(servletContext);

        // assert
        verify(servletContext).addListener(ContextLoaderListener.class);
        verify(servletContext).setInitParameter(ContextLoader.CONTEXT_CLASS_PARAM, AnnotationConfigWebApplicationContext.class.getName());
        verify(servletContext).setInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, ApiConfig.class.getName());
        verify(servletContext).addServlet("servlet", ServletContainer.class.getName());
        verify(servletRegistration).setInitParameter("javax.ws.rs.Application", JerseyApplication.class.getName());
        verify(servletRegistration).setInitParameter("jersey.config.server.provider.classnames", LoggingFilter.class.getName());
        verify(servletRegistration).setLoadOnStartup(1);
        verify(servletRegistration).addMapping("/api/*");

//        verify(servletContext).addFilter("springSecurityFilterChain", DelegatingFilterProxy.class.getName());
//        verify(filterRegistration).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
    }

}
