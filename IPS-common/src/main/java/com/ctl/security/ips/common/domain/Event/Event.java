package com.ctl.security.ips.common.domain.Event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by admin on 5/11/2015.
 */

@EqualsAndHashCode()
@Accessors(chain = true)
@Data
public class Event implements Serializable{

    private String hostName;
    private String reason;

    @Override
    public String toString() {
        return super.toString();
    }
}
