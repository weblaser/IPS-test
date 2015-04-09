package com.ctl.security.ips.test.cucumber.config;

import com.ctl.security.clc.client.core.config.ClcClientCoreAppConfig;
import com.ctl.security.clc.client.test.config.ClcMockConfig;
import com.ctl.security.data.client.config.SecurityDataClientAppConfig;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * @author lane.maxwell
 */
@Configuration
//TODO fix this component scan
@ComponentScan("com.ctl.security.ips")
@Import({SecurityDataClientAppConfig.class,ClcClientCoreAppConfig.class,MockConfig.class,RealConfig.class, ClcMockConfig.class})
@PropertySources({
        @PropertySource("classpath:properties/security.data.client.properties"),
        @PropertySource("classpath:properties/ips.service.properties"),
        @PropertySource("classpath:properties/ips.test.properties"),
        @PropertySource("classpath:properties/dsm.client.properties")
})
public class CucumberConfiguration {

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public VelocityEngine velocityEngine(){
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init();
        return velocityEngine;
    }
}
