package com.ctl.security.ips.common.domain.Policy;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(chain = true)
@Data
public class Policy implements Serializable {

    private String vendorPolicyId;
    private String parentPolicyId;
    private String name;
    private PolicyStatus status;

    private String hostName;
    private String tenantId;
    private String username;
}
