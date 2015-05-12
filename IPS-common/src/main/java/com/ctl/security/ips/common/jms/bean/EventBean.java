package com.ctl.security.ips.common.jms.bean;

import com.ctl.security.ips.common.domain.Event.DpiEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by sean.robb on 3/9/2015.
 */

@ToString
@AllArgsConstructor
@EqualsAndHashCode()
@Accessors(chain = true)
@Data
public class EventBean implements Serializable {
    private String hostName;
    private String accountId;
    private DpiEvent event;
}
