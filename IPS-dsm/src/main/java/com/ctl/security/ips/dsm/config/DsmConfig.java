package com.ctl.security.ips.dsm.config;

import com.ctl.security.ips.client.config.ClientConfig;
import com.ctl.security.library.common.config.SecurityLibraryCommonAppConfig;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.annotation.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@PropertySources({  @PropertySource("classpath:properties/dsm.client.properties"),
                    @PropertySource("classpath:properties/clc.data.properties")})
@Configuration
@ComponentScan(basePackages = {"com.ctl.security.ips.dsm"})
@Import({DsmBeans.class, ClientConfig.class, SecurityLibraryCommonAppConfig.class})
public class DsmConfig {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(DsmConfig.class);
    public static final String DSM_REST_BEAN = "DsmRestBean";

    @Bean(name= DSM_REST_BEAN)
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//        Proxy proxy= new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy-us.sky.savvis.net", 8080));
//        requestFactory.setProxy(proxy);
        requestFactory.setConnectTimeout(120000);
        requestFactory.setReadTimeout(120000);
        return new RestTemplate(requestFactory);
    }

//    }

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

}