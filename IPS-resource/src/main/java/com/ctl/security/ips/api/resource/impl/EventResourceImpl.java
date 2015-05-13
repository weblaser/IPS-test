package com.ctl.security.ips.api.resource.impl;

import com.ctl.security.ips.api.jms.EventMessageSender;
import com.ctl.security.ips.api.resource.EventResource;
import com.ctl.security.ips.common.domain.Event.DpiEvent;
import com.ctl.security.ips.common.jms.bean.EventBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by sean.robb on 3/10/2015.
 */
@Component
public class EventResourceImpl implements EventResource {

    @Autowired
    private EventMessageSender eventMessageSender;

    @Override
    public void notify(String account,String hostName,DpiEvent event) {
        EventBean eventBean = new EventBean(hostName,account,event);
        eventMessageSender.notify(eventBean);
    }
}
