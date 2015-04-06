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
    private static final String active = "ACTIVE";
    private static final String pending_deletion = "PENDING_DELETION";
    private final String tenantScenario = "Tenant Scenario";
    private final String velocityTenantIdKey = "tenantId";
    private final String velocityStateKey = "state";
    private final String templateName = "vm/ResponseTenantGet.vm";

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

        velocityContext.put(velocityTenantIdKey, tenantId);

        mockTenantCreate(tenantId,sessionId,velocityContext);

        mockTenantDelete(tenantId, sessionId,velocityContext);


//        wireMockServer.stop();
    }

    private void mockTenantCreate(String tenantId, String sessionId, VelocityContext velocityContext) {
        String pathTenantCreate = restPath + DsmTenantClient.PATH_TENANTS;
        String tenantCreatedState = "TenantCreated";

        StringWriter stringWriterCreate = new StringWriter();
        velocityEngine.mergeTemplate(templateName, StandardCharsets.UTF_8.name(), velocityContext, stringWriterCreate);
        String responseTenantCreateTenantXml = stringWriterCreate.toString();

        stubFor(post(urlPathEqualTo(pathTenantCreate))
                .inScenario(tenantScenario)
                .willSetStateTo(tenantCreatedState)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(responseTenantCreateTenantXml)));

        mockTenantGet(tenantId, sessionId, velocityContext, tenantCreatedState, active);
    }

    private void mockTenantDelete(String tenantId, String sessionId, VelocityContext velocityContext) {
        String tenantDeletedState = "TenantDeleted";

        String pathTenantDelete = restPath + DsmTenantClient.PATH_TENANTS_ID + tenantId
                + "?" + DsmTenantClient.QUERY_PARAM_SESSION_ID + sessionId;

        stubFor(delete(urlPathEqualTo(pathTenantDelete))
                .inScenario(tenantScenario)
                .willSetStateTo(tenantDeletedState)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)));

        mockTenantGet(tenantId, sessionId, velocityContext, tenantDeletedState, pending_deletion);
    }

    private void mockTenantGet(String tenantId, String sessionId, VelocityContext velocityContext,String scenarioState,String state) {
        velocityContext.put(velocityStateKey, state);

        StringWriter stringWriterGet = new StringWriter();
        velocityEngine.mergeTemplate(templateName, StandardCharsets.UTF_8.name(),
                velocityContext, stringWriterGet);
        String responseTenantGetTenantXml = stringWriterGet.toString();

        String pathTenantGet = restPath + DsmTenantClient.PATH_TENANTS_ID + tenantId
                + "?" + DsmTenantClient.QUERY_PARAM_SESSION_ID + sessionId;

        stubFor(get(urlPathEqualTo(pathTenantGet))
                .inScenario(tenantScenario)
                .whenScenarioStateIs(scenarioState)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody(responseTenantGetTenantXml)));
    }
}
