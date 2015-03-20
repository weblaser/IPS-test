package com.ctl.security.ips.cli;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class IpsApiCliTest {

    @InjectMocks
    private IpsApiCli ipsApiCli;

    @Test
    public void main_executesWithoutException() throws Exception {

        Exception exception = null;
        try{
            ipsApiCli.main(null);
        }
        catch (Exception e){
            e.getStackTrace();
            exception = e;
        }

        assertNull(exception);

    }
}