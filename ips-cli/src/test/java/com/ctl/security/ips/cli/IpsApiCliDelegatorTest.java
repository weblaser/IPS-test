package com.ctl.security.ips.cli;

import com.ctl.security.ips.client.PolicyClient;
import com.ctl.security.ips.common.domain.Policy.Policy;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IpsApiCliDelegatorTest {

    @InjectMocks
    private IpsApiCliDelegator classUnderTest;

    @Mock
    private PolicyClient policyClient;

    @Before
    public void setup(){
        ReflectionTestUtils.setField(classUnderTest, "gson", new Gson());
    }


    @Test
    public void executeGetPolicyForAccount_executesGetPolicyForAccount(){

        String bearerToken = "bearerToken";
        String accountId = "accountId";
        String id = "id";

        String[] args = {IpsApiCliDelegator.GET_POLICY_FOR_ACCOUNT, bearerToken, accountId, id};

        Policy policy = new Policy();
        policy.setVendorPolicyId(id);

        when(policyClient.getPolicyForAccount(accountId, id, bearerToken)).thenReturn(policy);

        classUnderTest.execute(args);

        verify(policyClient).getPolicyForAccount(accountId, id, bearerToken);

    }

}