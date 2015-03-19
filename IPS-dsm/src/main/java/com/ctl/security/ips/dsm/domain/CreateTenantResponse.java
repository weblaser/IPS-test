package com.ctl.security.ips.dsm.domain;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Chad.Middleton on 3/10/2015
 */
@Data
@Accessors(chain = true)
@XmlRootElement(
        name = "createTenantResponse"
)
public class CreateTenantResponse {

    private Integer tenantID;

}
