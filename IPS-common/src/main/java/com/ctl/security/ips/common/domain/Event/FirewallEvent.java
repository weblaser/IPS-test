package com.ctl.security.ips.common.domain.Event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Created by sean.robb on 3/9/2015.
 */

@ToString
@EqualsAndHashCode()
@Accessors(chain = true)
@Data
public class FirewallEvent extends Event {


    @Override
    public String toString() {
        return "Firewall Event Reason: " +  this.getReason();
    }
}
