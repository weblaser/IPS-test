package com.ctl.security.ips.api.jersey.mapper;

import com.ctl.security.ips.common.exception.NotAuthorizedException;

import javax.ws.rs.core.Response.Status;

/**
 * Created by kevin.weber on 10/30/2014.
 */
public class NotAuthorizedExceptionMapper extends BaseExceptionMapper<NotAuthorizedException> {

    @Override
    public Status getStatus() {
        return Status.FORBIDDEN;
    }
}
