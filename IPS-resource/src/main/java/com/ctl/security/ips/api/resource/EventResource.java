package com.ctl.security.ips.api.resource;

import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import com.ctl.security.ips.common.domain.Event;
import com.wordnik.swagger.annotations.*;
import org.apache.http.HttpStatus;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by sean.robb on 3/10/2015.
 */
@Path("/event")
@Produces({MediaType.APPLICATION_JSON})
@Api(value = "/event", description = "Event Resource")
public interface EventResource {


    @PUT
    @Path("/{" + ResourceConstants.ACCT_PARAM + "}/{" + ResourceConstants.HOST_NAME_PARAM + "}")
    @ApiOperation(value = "Update Notification Destination for a Server")
    @ApiResponses(value = {@ApiResponse(code = HttpStatus.SC_NO_CONTENT, message = "Event Notification has Occurred"),
            @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = ResourceConstants.BAD_REQUEST),
            @ApiResponse(code = HttpStatus.SC_FORBIDDEN, message = ResourceConstants.UNAUTHORIZED)})
    void notify(@ApiParam(value = ResourceConstants.ACCOUNT, required = true) @PathParam(ResourceConstants.ACCT_PARAM) String account,
                                       @ApiParam(value = ResourceConstants.HOST_NAME_LABEL, required = true) @PathParam(ResourceConstants.HOST_NAME_PARAM) String hostName,
                                       @ApiParam(value = ResourceConstants.EVENT_LABEL, required = true) Event event);


}
