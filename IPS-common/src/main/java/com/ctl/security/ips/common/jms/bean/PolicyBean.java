package com.ctl.security.ips.common.jms.bean;

import com.ctl.security.ips.common.domain.Policy.Policy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by kevin.wilde on 2/13/2015.
 *
 */

@AllArgsConstructor
@Accessors(chain = true)
@Data
public class PolicyBean implements Serializable {
    private String accountId;
    private Policy policy;
    private String bearerToken;
}
