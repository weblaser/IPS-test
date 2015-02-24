package com.ctl.security.ips.api.resource;

import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/policies")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Api(value = "/policies", description = "Policy Resource")
public interface PolicyResource {

    String ACCOUNT = "Account";
    String ACCT = "acct";
    String POLICY = "Policy";
    String POLICY_ID = "Policy ID";
    String POLICYID = "policyId";
    String UNAUTHORIZED = "Unauthorized";
    String POLICY_NOT_FOUND = "Policy Not Found";
    String USERNAME_LABEL = "Username";
    String USERNAME = "username";
    String SERVER_DOMAIN_NAME_LABEL = "Server Domain Name";
    public static final String SERVER_DOMAIN_NAME = "serverDomainName";

    int NO_CONTENT = 204;
    int OK = 200;
    int BAD_REQUEST = 400;
    int FORBIDDEN = 403;


    @POST
    @Path("/{acct}")
    @ApiOperation(value = "Create Policy for Given Account")
    @ApiResponses(value = {@ApiResponse(code = OK, message = "Policy Created"),
            @ApiResponse(code = FORBIDDEN, message = UNAUTHORIZED)})
    void createPolicyForAccount(@ApiParam(value = ACCOUNT, required = true) @PathParam(ACCT) String account,
                                @ApiParam(value = POLICY, required = true) Policy policy);

    @GET
    @Path("/{acct}")
    @ApiOperation(value = "Get Policies for Given Account")
    @ApiResponses(value = {@ApiResponse(code = OK, message = "Policies Found"),
            @ApiResponse(code = BAD_REQUEST, message = "No Policies Found")})
    List<Policy> getPoliciesForAccount(@ApiParam(value = ACCOUNT, required = true) @PathParam(ACCT) String account);

    @GET
    @Path("/{acct}/{policyId}")
    @ApiOperation(value = "Get Policy for Given Account")
    @ApiResponses(value = {@ApiResponse(code = OK, message = "Policy Found"),
            @ApiResponse(code = BAD_REQUEST, message = POLICY_NOT_FOUND)})
    Policy getPolicyForAccount(@ApiParam(value = ACCOUNT, required = true) @PathParam(ACCT) String account,
                               @ApiParam(value = POLICY_ID, required = true) @PathParam(POLICYID) String policyId);


    @PUT
    @Path("/{acct}/{policyId}")
    @ApiOperation(value = "Update Policy for Given Account")
    @ApiResponses(value = {@ApiResponse(code = NO_CONTENT, message = "Policy Updated"),
            @ApiResponse(code = BAD_REQUEST, message = POLICY_NOT_FOUND),
            @ApiResponse(code = FORBIDDEN, message = UNAUTHORIZED)})
    void updatePolicyForAccount(@ApiParam(value = ACCOUNT, required = true) @PathParam(ACCT) String account,
                                @ApiParam(value = POLICY_ID, required = true) @PathParam(POLICYID) String policyId,
                                @ApiParam(value = POLICY, required = true) Policy policy);

    @DELETE
    @Path("/{acct}/{policyId}/{serverDomainName}")
    @ApiOperation(value = "Delete Policy for Given Account")
    @ApiResponses(value = {@ApiResponse(code = NO_CONTENT, message = "Policy Deleted"),
            @ApiResponse(code = BAD_REQUEST, message = POLICY_NOT_FOUND),
            @ApiResponse(code = FORBIDDEN, message = UNAUTHORIZED)})
    void deletePolicyForAccount(@ApiParam(value = ACCOUNT, required = true) @PathParam(ACCT) String account,
                                @ApiParam(value = POLICY_ID, required = true) @PathParam(POLICYID) String policyId,
                                @ApiParam(value = SERVER_DOMAIN_NAME_LABEL, required = true) @PathParam(SERVER_DOMAIN_NAME) String serverDomainName,
                                @ApiParam(value = USERNAME_LABEL, required = true) @QueryParam(USERNAME) String username) throws DsmPolicyClientException;
}
