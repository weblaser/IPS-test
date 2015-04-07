package com.ctl.security.ips.common.domain.Event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by sean.robb on 3/9/2015.
 */

@ToString
@EqualsAndHashCode()
@Accessors(chain = true)
@Data
public class FirewallEvent implements Serializable {

    private String hostName;
    private String reason;
    //TODO save accountID as well
}
