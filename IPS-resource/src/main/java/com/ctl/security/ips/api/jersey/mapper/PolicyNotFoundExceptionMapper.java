package com.ctl.security.ips.api.jersey.mapper;

import com.ctl.security.ips.common.exception.PolicyNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Created by Kevin.Weber on 10/29/2014.
 */
public class PolicyNotFoundExceptionMapper extends BaseExceptionMapper<PolicyNotFoundException> {
    @Override
    public Status getStatus() {
        return Response.Status.BAD_REQUEST;
    }

}
