package com.ctl.security.ips.test.cucumber.config;

import com.ctl.security.ips.dsm.DsmTenantClient;
import com.ctl.security.ips.dsm.config.MockDsmBeans;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.HttpStatus;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

/**
 * Created by kevin on 3/31/15.
 */
@Component
public class MockDsmRest {

    public static final String TENANT_ID = "42";
    @Value("${${spring.profiles.active:local}.dsm.rest.protocol}")
    private String restProtocol;
    @Value("${${spring.profiles.active:local}.dsm.rest.host}")
    private String restHost;
    @Value("${${spring.profiles.active:local}.dsm.rest.port}")
    private Integer restPort;
    @Value("${${spring.profiles.active:local}.dsm.rest.path}")
    private String restPath;

    @Autowired
    private VelocityEngine velocityEngine;

    private WireMockServer wireMockServer;

    public void init(){
        setupRestDsmWireMock();
    }

    public void setupRestDsmWireMock(){
        wireMockServer = new WireMockServer(restPort);
        configureFor(restHost, restPort);
        wireMockServer.start();


        String tenantId = TENANT_ID;
        String sessionId = MockDsmBeans.SESSION_ID;

        VelocityContext velocityContext = new VelocityContext();

        velocityContext.put("tenantId", tenantId);



        String pathTenantCreate = restPath + DsmTenantClient.PATH_TENANTS;

        StringWriter stringWriterCreate = new StringWriter();
        velocityEngine.mergeTemplate("vm/ResponseTenantCreate.vm", StandardCharsets.UTF_8.name(), velocityContext, stringWriterCreate);
        String responseTenantCreateTenantXml = stringWriterCreate.toString();

        stubFor(post(urlPathEqualTo(pathTenantCreate))
                .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withBody(responseTenantCreateTenantXml)));

        StringWriter stringWriterGet = new StringWriter();
        velocityEngine.mergeTemplate("vm/ResponseTenantGet.vm", StandardCharsets.UTF_8.name(), velocityContext, stringWriterGet);
        String responseTenantGetTenantXml = stringWriterGet.toString();

        String pathTenantGet = restPath + DsmTenantClient.PATH_TENANTS_ID + tenantId + "?" + DsmTenantClient.QUERY_PARAM_SESSION_ID + sessionId;
        stubFor(get(urlPathEqualTo(pathTenantGet))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(responseTenantGetTenantXml)));

//        wireMockServer.stop();
    }
}
