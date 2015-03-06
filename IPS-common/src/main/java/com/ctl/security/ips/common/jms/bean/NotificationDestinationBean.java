package com.ctl.security.ips.common.jms.bean;

import com.ctl.security.data.common.domain.mongo.NotificationDestination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@ToString
@AllArgsConstructor
@EqualsAndHashCode()
@Accessors(chain = true)
@Data
public class NotificationDestinationBean implements Serializable {

    private String hostName;
    private String accountId;
    private List<NotificationDestination> notificationDestinations;

}
