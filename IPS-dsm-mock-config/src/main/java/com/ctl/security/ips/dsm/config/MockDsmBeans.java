package com.ctl.security.ips.dsm.config;

import com.ctl.security.ips.common.domain.Event.DpiEvent;
import com.ctl.security.ips.dsm.domain.DpiEventTransportMarshaller;
import manager.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * Created by kevin.wilde on 1/21/2015.
 */
@Configuration
@ComponentScan("com.ctl.security.ips.dsm")
@PropertySource("classpath:properties/ips.dsm.mock.test.properties")
@Profile({"local", "dev"})
public class MockDsmBeans extends BaseDsmBeans {

    private static final Logger logger = LogManager.getLogger(MockDsmBeans.class);

    private final int HOST_ID = 007;
    private final String GOOD_STATUS = "Managed (Online)";
    private final String GOOD_DPI_STATUS = "Intrusion Prevention: On, Prevent, 310 rules";
    public static final String EXPECTED_POLICY = "expectedPolicy";
    public static final String EXPECTED_DELETED_POLICY = "expectedDeletedPolicy";
    public static final String CURRENT_EXPECTED_POLICY = "currentExpectedPolicy";
    public static final String SESSION_ID = "123";

    public static final Map<String, String> loginTenantMap = new HashMap<String, String>() {{
        for (int index = 0; index < 4; index++) {
            put("Account" + index, "Session" + index);
        }
    }};

    @Value("${${spring.profiles.active:local}.dsm.rest.protocol}")
    private String restProtocol;
    @Value("${${spring.profiles.active:local}.dsm.rest.host}")
    private String restHost;
    @Value("${${spring.profiles.active:local}.dsm.rest.port}")
    private Integer restPort;
    @Value("${${spring.profiles.active:local}.dsm.rest.path}")
    private String restPath;

    @Mock
    private Manager manager;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DpiEventTransportMarshaller dpiEventTransportMarshaller;

    @Value("${${spring.profiles.active:local}.ips.dsm.mock.test.port}")
    private int destinationPort;
    @Value("${${spring.profiles.active:local}.ips.dsm.mock.test.eventTriggerAddress}")
    private String host;
    @Value("${${spring.profiles.active:local}.ips.dsm.mock.test.host}")
    private String destinationHostName;

    public MockDsmBeans() {
        MockitoAnnotations.initMocks(this);
    }

    @Bean
    public Manager manager() throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthorizationException_Exception, ManagerTimeoutException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception {

        logger.info("loading mock dsm manager...");
        setupMockManagerPolicyInteraction();
        return manager;
    }

    private void setupMockManagerPolicyInteraction() throws ManagerAuthenticationException_Exception, ManagerTimeoutException_Exception, ManagerException_Exception, ManagerAuthorizationException_Exception, ManagerValidationException_Exception, ManagerIntegrityConstraintException_Exception, ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerMaxSessionsException_Exception, ManagerCommunicationException_Exception {

        setupDSMAuthentication();
        setupDsmTenantAuthentication();

        final Map<String, String> policyKeys = new HashMap<>();
        policyKeys.put(CURRENT_EXPECTED_POLICY, EXPECTED_POLICY);

        final Map<String, SecurityProfileTransport> expectedPolicies = new HashMap<>();
        final SecurityProfileTransport expectedSecurityProfileTransport = new SecurityProfileTransport();

        setupPolicyRetrieve(policyKeys, expectedPolicies, expectedSecurityProfileTransport);
        setupPolicySave(expectedSecurityProfileTransport);
        setupPolicyDelete(policyKeys, expectedPolicies);
    }

    private void setupDSMAuthentication() throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerMaxSessionsException_Exception, ManagerAuthenticationException_Exception, ManagerCommunicationException_Exception, ManagerException_Exception {
        String sessionId = SESSION_ID;
        when(manager.authenticate(BaseDsmBeans.APIUSER, BaseDsmBeans.PASSWORD_CORRECT))
                .thenReturn(sessionId);
        when(manager.authenticate(BaseDsmBeans.APIUSER, BaseDsmBeans.PASSWORD_WRONG))
                .thenThrow(ManagerAuthenticationException_Exception.class);
        when(manager.authenticate(BaseDsmBeans.APIUSER_WRONG, BaseDsmBeans.PASSWORD_CORRECT))
                .thenThrow(ManagerAuthenticationException_Exception.class);
    }


    private void setupPolicyRetrieve(final Map<String, String> policyKeys, final Map<String, SecurityProfileTransport> expectedPolicies, SecurityProfileTransport expectedSecurityProfileTransport) throws ManagerAuthenticationException_Exception, ManagerTimeoutException_Exception, ManagerException_Exception {
        Integer validDsmPolicyId = Integer.valueOf(BaseDsmBeans.VALID_DSM_POLICY_ID);
        expectedSecurityProfileTransport.setID(validDsmPolicyId);
        String validDsmPolicyName = "validDsmPolicyName";
        expectedSecurityProfileTransport.setName(validDsmPolicyName);
        expectedPolicies.put(EXPECTED_POLICY, expectedSecurityProfileTransport);

        when(manager.securityProfileRetrieve(anyInt(), anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                SecurityProfileTransport securityProfileTransport = expectedPolicies
                        .get(policyKeys.get(CURRENT_EXPECTED_POLICY));
                policyKeys.put(CURRENT_EXPECTED_POLICY, EXPECTED_POLICY);
                return securityProfileTransport;
            }
        });

        when(manager.securityProfileRetrieveByName(anyString(), anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                SecurityProfileTransport securityProfileTransport = expectedPolicies.get(policyKeys.get(CURRENT_EXPECTED_POLICY));
                policyKeys.put(CURRENT_EXPECTED_POLICY, EXPECTED_POLICY);
                return securityProfileTransport;
            }
        });
    }

    private void setupPolicySave(SecurityProfileTransport expectedSecurityProfileTransport) throws ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception, ManagerAuthenticationException_Exception, ManagerTimeoutException_Exception, ManagerException_Exception, ManagerAuthorizationException_Exception {
        when(manager.securityProfileSave(any(SecurityProfileTransport.class), anyString()))
                .thenReturn(expectedSecurityProfileTransport);
    }

    private void setupPolicyDelete(final Map<String, String> policyKeys, Map<String, SecurityProfileTransport> expectedPolicies) throws ManagerAuthenticationException_Exception, ManagerTimeoutException_Exception, ManagerException_Exception, ManagerAuthorizationException_Exception {
        final SecurityProfileTransport expectedDeletedSecurityProfileTransport = new SecurityProfileTransport();
        expectedPolicies.put(EXPECTED_DELETED_POLICY, expectedDeletedSecurityProfileTransport);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                policyKeys.put(CURRENT_EXPECTED_POLICY, EXPECTED_DELETED_POLICY);
                return null;
            }
        }).when(manager).securityProfileDelete(anyList(), anyString());
    }

    private void setupDsmTenantAuthentication() throws ManagerTimeoutException_Exception, ManagerSecurityException_Exception, ManagerException_Exception {
        when(manager.signInAsTenant(
                anyString(),
                anyString()
        )).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                Integer argumentAccountId = 0;
                Object[] arguments = invocationOnMock.getArguments();
                String accountId = arguments[argumentAccountId].toString();
                String sessionId = createSessionId(accountId);
                createDpiEventRetrieveMock(sessionId, accountId);
                createHostStatusMocks(sessionId);
                return sessionId;
            }
        });
    }

    private void createHostStatusMocks(String sessionId) throws ManagerAuthenticationException_Exception, ManagerTimeoutException_Exception, ManagerException_Exception {
        HostTransport hostTransport = new HostTransport();
        hostTransport.setID(HOST_ID);

        ProtectionStatusTransport protectionStatusTransport = new ProtectionStatusTransport();
        protectionStatusTransport.setProtectionType(EnumProtectionType.AGENT);
        protectionStatusTransport.setStatus(GOOD_STATUS);
        protectionStatusTransport.setDpiStatus(GOOD_DPI_STATUS);

        ArrayOfProtectionStatusTransport protectionStatusTransportArray = new ArrayOfProtectionStatusTransport();
        protectionStatusTransportArray.getItem().add(protectionStatusTransport);

        HostStatusTransport hostStatusTransport = new HostStatusTransport();
        hostStatusTransport.setProtectionStatusTransports(protectionStatusTransportArray);

        when(manager.hostRetrieveByName(anyString(), eq(sessionId))).thenReturn(hostTransport);
        when(manager.hostGetStatus(hostTransport.getID(), sessionId)).thenReturn(hostStatusTransport);
    }

    private void createDpiEventRetrieveMock(String sessionId, String accountId) throws ManagerAuthenticationException_Exception, ManagerTimeoutException_Exception, ManagerValidationException_Exception, ManagerException_Exception {
        when(manager.dpiEventRetrieve(any(TimeFilterTransport.class),
                any(HostFilterTransport.class),
                any(IDFilterTransport.class),
                eq(sessionId))).thenAnswer(new Answer<DPIEventListTransport>() {
            @Override
            public DPIEventListTransport answer(InvocationOnMock invocationOnMock) {
                List<DpiEvent> response = null;
                try {
                    String address = "http://" + destinationHostName + ":" + destinationPort
                            + host + "/" + accountId;
                    response = Arrays.asList(restTemplate.exchange(address, HttpMethod.GET,
                            null, DpiEvent[].class).getBody());
                } catch (RestClientException rce) {
                }
                return convertAllToDpiEventListTransport(response);
            }
        });
    }

    private DPIEventListTransport getDpiEventListTransport() {
        DPIEventListTransport dpiEventListTransport = new DPIEventListTransport();
        dpiEventListTransport.setDPIEvents(new ArrayOfDPIEventTransport());
        return dpiEventListTransport;
    }

    private DPIEventListTransport convertAllToDpiEventListTransport(List<DpiEvent> response) {
        DPIEventListTransport dpiEventListTransport = getDpiEventListTransport();
        if (response != null) {
            for (DpiEvent dpiEvent : response) {
                dpiEventListTransport.getDPIEvents().getItem()
                        .add(dpiEventTransportMarshaller.convert(dpiEvent));
            }
        }
        return dpiEventListTransport;
    }

    private String createSessionId(String accountId) {
        return "Session ID For: " + accountId;
    }

}
