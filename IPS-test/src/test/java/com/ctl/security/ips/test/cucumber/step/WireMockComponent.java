package com.ctl.security.ips.test.cucumber.step;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * Created by kevin on 4/2/15.
 */

@Component
public class WireMockComponent {

//    public WireMockServer createWireMockServer(int port) {
//        WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(port));
//        wireMockServer.start();
//        return wireMockServer;
//    }


//    public WireMock createWireMockClient(String destinationHostName, int destinationPort) {
//        WireMock wireMock = new WireMock(destinationHostName, destinationPort);
//        return wireMock;
//    }

//    public void createWireMockServerPostStub(WireMock wireMock, String notificationUrlPath, int httpStatus) {
//        wireMock.register(post(urlPathEqualTo(notificationUrlPath))
//                .willReturn(aResponse()
//                        .withStatus(httpStatus)));
//    }

    public WireMockServer createWireMockServer(String destinationHostName, int destinationPort) {
        WireMockServer wireMockServer = new WireMockServer(destinationPort);
        configureFor(destinationHostName, destinationPort);
        wireMockServer.start();
        return wireMockServer;
    }

    public void createWireMockServerPostStub(WireMockServer wireMockServer, String notificationUrlPath, int httpStatus) {
        wireMockServer.stubFor(post(urlPathEqualTo(notificationUrlPath))
                .willReturn(aResponse()
                        .withStatus(httpStatus)));
    }
}
