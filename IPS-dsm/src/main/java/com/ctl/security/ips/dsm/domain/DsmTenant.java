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
        name = "tenant"
)
public class DsmTenant {

    private String guid;

    private Boolean allModulesVisible;

    private Integer tenantID;

    private Integer databaseServerID;

    private String agentInitiatedActivationPassword;

    private String description;

    private String name;

    private String state;

    private Boolean demoMode;

    private String timeZone;

    private String language;

    private String licenseMode;

    private Boolean hideUnlicensedModules;

    private String country;

}
