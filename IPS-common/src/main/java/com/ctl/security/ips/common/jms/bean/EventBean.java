package com.ctl.security.ips.common.jms.bean;

import com.ctl.security.ips.common.domain.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Created by sean.robb on 3/9/2015.
 */

@ToString
@AllArgsConstructor
@EqualsAndHashCode()
@Accessors(chain = true)
@Data
public class EventBean {
    private String hostName;
    private String accountId;
    private Event event;
}
