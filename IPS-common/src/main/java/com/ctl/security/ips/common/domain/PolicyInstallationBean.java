package com.ctl.security.ips.common.domain;


import com.ctl.security.data.common.domain.mongo.bean.InstallationBean;
import lombok.Data;

/**
 * Created by kevin.wilde on 2/9/2015.
 */
@Data
public class PolicyInstallationBean {

    private Policy policy;
    private InstallationBean installationBean;

}
