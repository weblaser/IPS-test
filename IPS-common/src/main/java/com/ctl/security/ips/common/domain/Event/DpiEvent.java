package com.ctl.security.ips.common.domain.Event;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by Sean Robb on 5/11/2015.
 */

@Accessors(chain = true)
@Data
public class DpiEvent implements Serializable {

    private String hostName;
    private String reason;

    @Override
    public String toString() {
        return "DPI Event Reason: " + this.getReason();
    }
}
