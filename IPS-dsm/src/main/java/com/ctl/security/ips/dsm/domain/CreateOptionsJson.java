package com.ctl.security.ips.dsm.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Chad.Middleton on 3/10/2015
 */
@Data
@Accessors(chain = true)
public class CreateOptionsJson {

    private String activationCodes;
    private String adminAccount;
    private String adminPassword;
    private String adminEmail;
    private Boolean confirmationRequired;
    private Boolean generateMasterPassword;

}

