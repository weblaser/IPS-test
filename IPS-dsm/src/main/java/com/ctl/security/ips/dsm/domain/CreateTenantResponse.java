package com.ctl.security.ips.dsm.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Chad.Middleton on 3/10/2015
 */
@Data
@Accessors(chain = true)
public class CreateTenantResponse {

    private Integer tenantID;

}
