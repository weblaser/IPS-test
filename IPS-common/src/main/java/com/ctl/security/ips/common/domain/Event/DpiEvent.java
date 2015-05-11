package com.ctl.security.ips.common.domain.Event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Created by Sean Robb on 5/11/2015.
 */

@ToString
@EqualsAndHashCode()
@Accessors(chain = true)
@Data
public class DpiEvent extends Event {

    @Override
    public String toString() {
        return "DPI Event Reason: " + this.getReason();
    }
}
