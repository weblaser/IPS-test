package com.ctl.security.ips.api.resource;

import com.ctl.security.ips.common.domain.Policy.Policy;
import com.ctl.security.ips.dsm.exception.DsmClientException;
import com.wordnik.swagger.annotations.*;
import org.apache.http.HttpStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/policies")
@Produces({MediaType.APPLICATION_JSON})
@Api(value = "/policies", description = "Policy Resource")
public interface PolicyResource {

    public static final String POLICY_ID = "Policy ID";
    public static final String POLICY_ID_PARAM = "policyId";
    public static final String POLICY_NOT_FOUND = "Policy Not Found";
    public static final String POLICY = "Policy";
    public static final String USERNAME_LABEL = "Username";
    public static final String USERNAME_PARAM = "username";
    public static final String TOKEN = "Bearer Token";


    @POST
    @Path("/{" + ResourceConstants.ACCT_PARAM + "}")
    @ApiOperation(value = "Create Policy for Given Account")
    @ApiResponses(value = {@ApiResponse(code = HttpStatus.SC_OK, message = "Policy Created"),
            @ApiResponse(code = HttpStatus.SC_FORBIDDEN, message = ResourceConstants.UNAUTHORIZED)})
    void createPolicyForAccount(@ApiParam(value = ResourceConstants.ACCOUNT, required = true) @PathParam(ResourceConstants.ACCT_PARAM) String account,
                                @ApiParam(value = POLICY, required = true) Policy policy,
                                @ApiParam(value = TOKEN, required = true) @HeaderParam("Authorization") String bearerToken);

    @GET
    @Path("/{" + ResourceConstants.ACCT_PARAM + "}")
    @ApiOperation(value = "Get Policies for Given Account")
    @ApiResponses(value = {@ApiResponse(code = HttpStatus.SC_OK, message = "Policies Found"),
            @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = "No Policies Found")})
    List<Policy> getPoliciesForAccount(@ApiParam(value = ResourceConstants.ACCOUNT, required = true) @PathParam(ResourceConstants.ACCT_PARAM) String account);

    @GET
    @Path("/{" + ResourceConstants.ACCT_PARAM + "}/{" + POLICY_ID_PARAM + "}")
    @ApiOperation(value = "Get Policy for Given Account")
    @ApiResponses(value = {@ApiResponse(code = HttpStatus.SC_OK, message = "Policy Found"),
            @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = POLICY_NOT_FOUND)})
    Policy getPolicyForAccount(@ApiParam(value = ResourceConstants.ACCOUNT, required = true) @PathParam(ResourceConstants.ACCT_PARAM) String account,
                               @ApiParam(value = POLICY_ID, required = true) @PathParam(POLICY_ID_PARAM) String policyId);


    @PUT
    @Path("/{" + ResourceConstants.ACCT_PARAM + "}/{" + POLICY_ID_PARAM + "}")
    @ApiOperation(value = "Update Policy for Given Account")
    @ApiResponses(value = {@ApiResponse(code = HttpStatus.SC_NO_CONTENT, message = "Policy Updated"),
            @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = POLICY_NOT_FOUND),
            @ApiResponse(code = HttpStatus.SC_FORBIDDEN, message = ResourceConstants.UNAUTHORIZED)})
    void updatePolicyForAccount(@ApiParam(value = ResourceConstants.ACCOUNT, required = true) @PathParam(ResourceConstants.ACCT_PARAM) String account,
                                @ApiParam(value = POLICY_ID, required = true) @PathParam(POLICY_ID_PARAM) String policyId,
                                @ApiParam(value = POLICY, required = true) Policy policy);

    @DELETE
    @Path("/{" + ResourceConstants.ACCT_PARAM + "}/{" + POLICY_ID_PARAM + "}/{" + ResourceConstants.HOST_NAME_PARAM + "}")
    @ApiOperation(value = "Delete Policy for Given Account")
    @ApiResponses(value = {@ApiResponse(code = HttpStatus.SC_NO_CONTENT, message = "Policy Deleted"),
            @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = POLICY_NOT_FOUND),
            @ApiResponse(code = HttpStatus.SC_FORBIDDEN, message = ResourceConstants.UNAUTHORIZED)})
    void deletePolicyForAccount(@ApiParam(value = ResourceConstants.ACCOUNT, required = true) @PathParam(ResourceConstants.ACCT_PARAM) String account,
                                @ApiParam(value = POLICY_ID, required = true) @PathParam(POLICY_ID_PARAM) String policyId,
                                @ApiParam(value = ResourceConstants.HOST_NAME_LABEL, required = true) @PathParam(ResourceConstants.HOST_NAME_PARAM) String serverDomainName,
                                @ApiParam(value = USERNAME_LABEL, required = true) @QueryParam(USERNAME_PARAM) String username,
                                @ApiParam(value = TOKEN, required = true) @HeaderParam("Authorization") String bearerToken)throws DsmClientException;
}
