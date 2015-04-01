package com.ctl.security.ips.dsm.config;

import com.ctl.security.ips.dsm.adapter.EventAdapter;
import com.ctl.security.ips.dsm.adapter.MockEventAdapterImpl;
import manager.*;
import org.apache.logging.log4j.LogManager;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * Created by kevin.wilde on 1/21/2015.
 */
@Configuration
@ComponentScan("com.ctl.security.ips.dsm")
@Profile({"local", "dev"})
public class MockDsmBeans extends BaseDsmBeans {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(MockDsmBeans.class);

    public static final String EXPECTED_POLICY = "expectedPolicy";
    public static final String EXPECTED_DELETED_POLICY = "expectedDeletedPolicy";
    public static final String CURRENT_EXPECTED_POLICY = "currentExpectedPolicy";

    @Mock
    private Manager manager;

    @Autowired
    @Qualifier("MockEventAdapter")
    private EventAdapter eventAdapter;

    public MockDsmBeans() {
        MockitoAnnotations.initMocks(this);
    }

    @Bean
    public Manager manager() throws ManagerSecurityException_Exception, ManagerAuthenticationException_Exception, ManagerLockoutException_Exception, ManagerCommunicationException_Exception, ManagerMaxSessionsException_Exception, ManagerException_Exception, ManagerAuthorizationException_Exception, ManagerTimeoutException_Exception, ManagerIntegrityConstraintException_Exception, ManagerValidationException_Exception {

        logger.error("loading mock dsm manager!!!!!");

        setupMockManagerPolicyInteraction();
        return manager;
    }

//    @Bean
//    FirewallEventListTransport eventListTransport(){
//        FirewallEventListTransport felt = new FirewallEventListTransport();
//        felt.setFirewallEvents(new ArrayOfFirewallEventTransport());
//        return felt;
//    }

    private void setupMockManagerPolicyInteraction() throws ManagerAuthenticationException_Exception, ManagerTimeoutException_Exception, ManagerException_Exception, ManagerAuthorizationException_Exception, ManagerValidationException_Exception, ManagerIntegrityConstraintException_Exception, ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerMaxSessionsException_Exception, ManagerCommunicationException_Exception {

        setupDsmAuthentication();

        final Map<String, String> policyKeys = new HashMap<>();
        policyKeys.put(CURRENT_EXPECTED_POLICY, EXPECTED_POLICY);

        final Map<String, SecurityProfileTransport> expectedPolicies = new HashMap<>();
        final SecurityProfileTransport expectedSecurityProfileTransport = new SecurityProfileTransport();

        setupPolicyRetrieve(policyKeys, expectedPolicies, expectedSecurityProfileTransport);
        setupPolicySave(expectedSecurityProfileTransport);
        setupPolicyDelete(policyKeys, expectedPolicies);
        setupFirewallEventRetrieve();
    }

    private void setupDsmAuthentication() throws ManagerSecurityException_Exception, ManagerLockoutException_Exception, ManagerMaxSessionsException_Exception, ManagerAuthenticationException_Exception, ManagerCommunicationException_Exception, ManagerException_Exception {
        String sessionId = "123";
        when(manager.authenticate(BaseDsmBeans.APIUSER, BaseDsmBeans.PASSWORD_CORRECT)).thenReturn(sessionId);
        when(manager.authenticate(BaseDsmBeans.APIUSER, BaseDsmBeans.PASSWORD_WRONG)).thenThrow(ManagerAuthenticationException_Exception.class);
        when(manager.authenticate(BaseDsmBeans.APIUSER_WRONG, BaseDsmBeans.PASSWORD_CORRECT)).thenThrow(ManagerAuthenticationException_Exception.class);
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
                SecurityProfileTransport securityProfileTransport = expectedPolicies.get(policyKeys.get(CURRENT_EXPECTED_POLICY));
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
        when(manager.securityProfileSave(Matchers.any(SecurityProfileTransport.class), anyString())).thenReturn(expectedSecurityProfileTransport);
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

    private void setupFirewallEventRetrieve() throws ManagerException_Exception, ManagerTimeoutException_Exception, ManagerAuthenticationException_Exception, ManagerValidationException_Exception {
        when(manager.firewallEventRetrieve(any(TimeFilterTransport.class),
                any(HostFilterTransport.class),
                any(IDFilterTransport.class),
                anyString())).thenAnswer(new Answer<FirewallEventListTransport>() {
            @Override
            public FirewallEventListTransport answer(InvocationOnMock invocationOnMock) throws Throwable {
                return eventAdapter.getEventTransportList();
            }
        });//(eventAdapter.getEventTransportList());
    }

}
