package com.ctl.security.ips.api.jersey.mapper;

import org.junit.Test;

import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;

public class NotAuthorizedExceptionMapperTest {

    private NotAuthorizedExceptionMapper classUnderTest = new NotAuthorizedExceptionMapper();

    @Test
    public void testGetStatus() {
        //act
        Status actual = classUnderTest.getStatus();

        //assert
        assertEquals(Status.FORBIDDEN, actual);
    }

}