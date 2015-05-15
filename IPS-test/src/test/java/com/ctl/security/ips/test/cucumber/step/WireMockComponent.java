package com.ctl.security.ips.test.cucumber.step;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Created by kevin on 4/2/15.
 */
//
@Component
public class WireMockComponent {


    public void createWireMockServerPostStub(WireMockServer wireMockServer, String notificationUrlPath, int httpStatus) {
        wireMockServer.stubFor(post(urlPathEqualTo(notificationUrlPath))
                .willReturn(aResponse()
                        .withStatus(httpStatus)));
    }

}
