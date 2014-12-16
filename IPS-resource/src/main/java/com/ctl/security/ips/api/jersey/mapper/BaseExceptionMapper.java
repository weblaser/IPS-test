package com.ctl.security.ips.api.jersey.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by Kevin.Weber on 10/29/2014.
 */
public class BaseExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {
    @Override
    public Response toResponse(E e) {
        return Response.status(getStatus()).entity(getMessage(e)).type("text/plain").build();
    }

    public Status getStatus() {
        return Status.INTERNAL_SERVER_ERROR;
    }

    public String getMessage(E e) {
        return e.getMessage() == null ? "No exception message provided." : e.getMessage();
    }
}
