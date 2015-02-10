package com.ctl.security.ips.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Created by Kevin.Weber on 10/27/2014.
 */
@ToString
@EqualsAndHashCode()
@Accessors(chain = true)
@Data
public class Policy {

    private String vendorPolicyId;
    private String name;
    private PolicyStatus status;

    private String serverDomainName;
    private String tenantId;
    private String username;
}
