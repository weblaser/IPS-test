package com.ctl.security.ips.test.cucumber.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;

/**
 * Created by Sean Robb on 4/16/2015.
 *
 */

@Component
public class MockNotificationDestination {

    @Value("${${spring.profiles.active:local}.ips.test.port}")
    private int destinationPort;

    @Value("${${spring.profiles.active:local}.ips.test.host}")
    private String destinationHostName;

    private WireMockServer wireMockServer;
    public void init() {
        wireMockServer = new WireMockServer(destinationPort);
        configureFor(destinationHostName,destinationPort);
        wireMockServer.start();
    }

    @Bean
    public WireMockServer notificationDestinationWireMockServer(){
        return wireMockServer;
    }
}
