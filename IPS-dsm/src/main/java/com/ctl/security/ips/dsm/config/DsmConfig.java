package com.ctl.security.ips.dsm.config;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

@PropertySources({@PropertySource("classpath:/dsm.client.properties")})
@Configuration
@ComponentScan(basePackages = {"com.ctl.security.ips.dsm"})
@Import({DsmBeans.class})
public class DsmConfig {
    private static final Logger logger = Logger.getLogger(DsmConfig.class);
    public static final String DSM_REST_BEAN = "DsmRestBean";

//    @Bean
//    public ITenantAPI iTenantAPI(){
//
//        String restApiUrl = "http://206.128.154.197:4119/rest";
//        ResteasyProviderFactory instance = ResteasyProviderFactory.getInstance();
//        RegisterBuiltin.register(instance);
//        ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor();
//        ITenantAPI iTenantAPI = ProxyFactory.create(ITenantAPI.class, restApiUrl, executor);
//
//        return iTenantAPI;
//    }

    @Bean (name = DSM_REST_BEAN)
    public RestTemplate restTemplate(){return new RestTemplate();}
}