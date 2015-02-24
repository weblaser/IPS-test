package com.ctl.security.ips.client.config;

import com.ctl.security.library.common.config.SecurityLibraryCommonAppConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.context.annotation.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.HttpMethod;
import java.net.URI;

/**
 * Created by Kevin.Weber on 10/29/2014.
 */
@Configuration
@ComponentScan(basePackages = "com.ctl.security.ips.client")
@PropertySource("classpath:properties/ips.client.properties")
@Import({SecurityLibraryCommonAppConfig.class})
public class ClientConfig {
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }
}
