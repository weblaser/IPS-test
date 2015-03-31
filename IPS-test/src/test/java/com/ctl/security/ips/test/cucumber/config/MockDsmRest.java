package com.ctl.security.ips.test.cucumber.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;

/**
 * Created by kevin on 3/31/15.
 */
@Component
public class MockDsmRest {

    @Value("${${spring.profiles.active:local}.dsm.rest.protocol}")
    private String restProtocol;
    @Value("${${spring.profiles.active:local}.dsm.rest.host}")
    private String restHost;
    @Value("${${spring.profiles.active:local}.dsm.rest.port}")
    private Integer restPort;
    @Value("${${spring.profiles.active:local}.dsm.rest.path}")
    private String restPath;

    private WireMockServer wireMockServer;

    public void init(){
        setupRestDsmWireMock();
    }

    public void setupRestDsmWireMock(){
        wireMockServer = new WireMockServer(restPort);
        configureFor(restHost, restPort);
        wireMockServer.start();
//        wireMockServer.stop();
    }
}
