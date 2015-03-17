package com.ctl.security.ips.dsm.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created by Chad.Middleton on 3/10/2015
 */
@Data
@Accessors(chain = true)
public class TenantElement {

    private String agentInitiatedActivationPassword;
    private Boolean allModulesVisible;
    private String country;
    private Integer databaseServerId;
    private Boolean demoMode;
    private String description;
    private String guid;
    private Boolean hideUnlicensedModules;
    private String language;
    private String licenseMode;
    private List<String> modulesVisible;
    private String name;
    private String state;
    private Integer tenantID;
    private String timeZone;

}
