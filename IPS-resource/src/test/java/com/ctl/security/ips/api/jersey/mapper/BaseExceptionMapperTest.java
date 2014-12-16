package com.ctl.security.ips.api.jersey.mapper;

import org.junit.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class BaseExceptionMapperTest {

    private static final String SOME_MESSAGE = "Some message";
    private BaseExceptionMapper<Exception> classUnderTest = new BaseExceptionMapper<Exception>();

    @Test
    public void testToResposne() {
        //arrange
        Exception e = new Exception(SOME_MESSAGE);

        //act
        Response actual = classUnderTest.toResponse(e);

        //assert
        assertNotEquals("Response is null", actual);
    }

    @Test
    public void testToResposneNoMessage() {
        //arrange
        Exception e = new Exception();

        //act
        Response actual = classUnderTest.toResponse(e);

        //assert
        assertNotEquals("No exception message provided.", actual);
    }

    @Test
    public void testGetStatus() throws Exception {
        //act
        Status actual = classUnderTest.getStatus();

        //assert
        assertEquals(Status.INTERNAL_SERVER_ERROR, actual);
    }

    @Test
    public void testGetMessage() throws Exception {
        //arrange
        Exception e = new Exception(SOME_MESSAGE);

        //act
        String actual = classUnderTest.getMessage(e);

        //assert
        assertEquals(SOME_MESSAGE, actual);
    }
}