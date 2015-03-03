package com.ctl.security.ips.service;

import com.ctl.security.data.common.domain.mongo.Product;
import com.ctl.security.data.common.domain.mongo.ProductType;
import com.ctl.security.data.common.domain.mongo.bean.InstallationBean;
import com.ctl.security.ips.common.jms.bean.PolicyBean;

/**
 * Created by kevin.wilde on 2/18/2015.
 */
public abstract class PolicyService {
    public InstallationBean buildInstallationBean(PolicyBean policyBean) {
        return new InstallationBean(policyBean.getPolicy().getUsername(),
                policyBean.getAccountId(),
                policyBean.getPolicy().getHostName(),
                new Product()
                        .setName(PolicyServiceRead.TREND_MICRO_IPS)
                        .setType(ProductType.IPS));
    }
}
