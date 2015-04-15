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

    WireMockServer createWireMockServer(String destinationHostName, int destinationPort) {
        WireMockServer wireMockServer = new WireMockServer(destinationPort);
//        WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(destinationPort).bindAddress(destinationHostName));
        WireMock wireMock = new WireMock(destinationHostName, destinationPort);

//        configureFor(destinationHostName, destinationPort);
        wireMockServer.start();
        return wireMockServer;
    }

    void createWireMockServerStub(WireMockServer wireMockServer, String notificationUrlPath, int httpStatus) {
        wireMockServer.stubFor(post(urlPathEqualTo(notificationUrlPath))
                .willReturn(aResponse()
                        .withStatus(httpStatus)
                        .withBody("")));
    }
}
