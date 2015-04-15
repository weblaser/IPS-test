package com.ctl.security.ips.test.cucumber.adapter;

import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.jsonResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Created by Sean Robb on 3/30/2015.
 */
public class MockEventAdapterImpl implements EventAdapter {

    private WireMockServer wireMockForSoapDsmMocking;

    @Value("${${spring.profiles.active:local}.ips.dsm.mock.test.eventTriggerAddress}")
    private String host;

    @Value("${${spring.profiles.active:local}.ips.dsm.mock.test.port}")
    private int port;

    @Value("${${spring.profiles.active:local}.ips.dsm.mock.test.host}")
    private String hostName;

    @PostConstruct
    public void init() {
        wireMockForSoapDsmMocking = new WireMockServer(port);
        configureFor(hostName, port);
        wireMockForSoapDsmMocking.start();
    }

    @Override
    public void triggerEvent(ConfigurationItem configurationItem, List<FirewallEvent> firewallEvents) {

        ResponseDefinition responseDefinition = jsonResponse(firewallEvents);

        String url = host + "/" + configurationItem.getAccount().getCustomerAccountId();

        wireMockForSoapDsmMocking.stubFor(get(urlEqualTo(url))
                .willReturn(aResponse().like(responseDefinition)));

    }
}
