package com.ctl.security.ips.test.cucumber.step;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Created by kevin on 4/2/15.
 */

@Component
public class WireMockComponent {
    WireMockServer createWireMockServer(String destinationHostName, int destinationPort) {
        WireMockServer wireMockServer = new WireMockServer(destinationPort);
        configureFor(destinationHostName, destinationPort);
        wireMockServer.start();
        return wireMockServer;
    }

    void createWireMockServerStub(String notificationUrlPath, int httpStatus) {
        stubFor(post(urlPathEqualTo(notificationUrlPath))
                .willReturn(aResponse()
                        .withStatus(httpStatus)));
    }
}
