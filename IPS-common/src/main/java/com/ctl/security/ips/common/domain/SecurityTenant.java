package com.ctl.security.ips.common.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by chad.middleton on 3/5/2015
 */
@Data
@Accessors(chain = true)
public class SecurityTenant {

    private Integer tenantId;
    private String agentInitiatedActivationPassword;
    private String guid;
    public String adminAccount;
    public String adminPassword;
    public String adminEmail;
    public String tenantName;
    public String state;

}
