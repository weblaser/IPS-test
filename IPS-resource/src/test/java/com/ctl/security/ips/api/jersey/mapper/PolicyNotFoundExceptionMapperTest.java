package com.ctl.security.ips.api.jersey.mapper;

import org.junit.Test;

import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;


public class PolicyNotFoundExceptionMapperTest {

    private PolicyNotFoundExceptionMapper classUnderTest = new PolicyNotFoundExceptionMapper();

    @Test
    public void testGetStatus() throws Exception {
        //act
        Status actual = classUnderTest.getStatus();

        //assert
        assertEquals(Status.BAD_REQUEST, actual);
    }
}