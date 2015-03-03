package com.ctl.security.ips.api.resource;

import com.ctl.security.ips.common.domain.Policy;
import com.ctl.security.ips.dsm.exception.DsmPolicyClientException;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/policies")
@Produces({MediaType.APPLICATION_JSON})
@Api(value = "/policies", description = "Policy Resource")
public interface PolicyResource {

    String ACCOUNT = "Account";
    String ACCT_PARAM = "acct";
    String POLICY = "Policy";
    String POLICY_ID = "Policy ID";
    String POLICY_ID_PARAM = "policyId";
    String UNAUTHORIZED = "Unauthorized";
    String POLICY_NOT_FOUND = "Policy Not Found";
    String USERNAME_LABEL = "Username";
    String USERNAME_PARAM = "username";
    String SERVER_DOMAIN_NAME_LABEL = "Server Domain Name";
    String HOST_NAME_PARAM = "hostName";

    int NO_CONTENT = 204;
    int OK = 200;
    int BAD_REQUEST = 400;
    int FORBIDDEN = 403;


    @POST
    @Path("/{" + ACCT_PARAM + "}")
    @ApiOperation(value = "Create Policy for Given Account")
    @ApiResponses(value = {@ApiResponse(code = OK, message = "Policy Created"),
            @ApiResponse(code = FORBIDDEN, message = UNAUTHORIZED)})
    void createPolicyForAccount(@ApiParam(value = ACCOUNT, required = true) @PathParam(ACCT_PARAM) String account,
                                @ApiParam(value = POLICY, required = true) Policy policy);

    @GET
    @Path("/{" + ACCT_PARAM + "}")
    @ApiOperation(value = "Get Policies for Given Account")
    @ApiResponses(value = {@ApiResponse(code = OK, message = "Policies Found"),
            @ApiResponse(code = BAD_REQUEST, message = "No Policies Found")})
    List<Policy> getPoliciesForAccount(@ApiParam(value = ACCOUNT, required = true) @PathParam(ACCT_PARAM) String account);

    @GET
    @Path("/{" + ACCT_PARAM + "}/{" + POLICY_ID_PARAM + "}")
    @ApiOperation(value = "Get Policy for Given Account")
    @ApiResponses(value = {@ApiResponse(code = OK, message = "Policy Found"),
            @ApiResponse(code = BAD_REQUEST, message = POLICY_NOT_FOUND)})
    Policy getPolicyForAccount(@ApiParam(value = ACCOUNT, required = true) @PathParam(ACCT_PARAM) String account,
                               @ApiParam(value = POLICY_ID, required = true) @PathParam(POLICY_ID_PARAM) String policyId);


    @PUT
    @Path("/{" + ACCT_PARAM + "}/{" + POLICY_ID_PARAM + "}")
    @ApiOperation(value = "Update Policy for Given Account")
    @ApiResponses(value = {@ApiResponse(code = NO_CONTENT, message = "Policy Updated"),
            @ApiResponse(code = BAD_REQUEST, message = POLICY_NOT_FOUND),
            @ApiResponse(code = FORBIDDEN, message = UNAUTHORIZED)})
    void updatePolicyForAccount(@ApiParam(value = ACCOUNT, required = true) @PathParam(ACCT_PARAM) String account,
                                @ApiParam(value = POLICY_ID, required = true) @PathParam(POLICY_ID_PARAM) String policyId,
                                @ApiParam(value = POLICY, required = true) Policy policy);

    @DELETE
    @Path("/{" + ACCT_PARAM + "}/{" + POLICY_ID_PARAM + "}/{" + HOST_NAME_PARAM + "}")
    @ApiOperation(value = "Delete Policy for Given Account")
    @ApiResponses(value = {@ApiResponse(code = NO_CONTENT, message = "Policy Deleted"),
            @ApiResponse(code = BAD_REQUEST, message = POLICY_NOT_FOUND),
            @ApiResponse(code = FORBIDDEN, message = UNAUTHORIZED)})
    void deletePolicyForAccount(@ApiParam(value = ACCOUNT, required = true) @PathParam(ACCT_PARAM) String account,
                                @ApiParam(value = POLICY_ID, required = true) @PathParam(POLICY_ID_PARAM) String policyId,
                                @ApiParam(value = SERVER_DOMAIN_NAME_LABEL, required = true) @PathParam(HOST_NAME_PARAM) String serverDomainName,
                                @ApiParam(value = USERNAME_LABEL, required = true) @QueryParam(USERNAME_PARAM) String username) throws DsmPolicyClientException;
}
