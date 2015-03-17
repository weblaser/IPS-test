package com.ctl.security.ips.maestro.jms;

import com.ctl.security.ips.common.jms.EventOperation;
import com.ctl.security.ips.common.jms.bean.EventBean;
import com.ctl.security.ips.maestro.service.EventNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Created by sean.robb on 3/17/2015.
 */
@Component
public class EventListener {

    @Autowired
    private EventNotifyService eventNotifyService;

    @JmsListener(destination = EventOperation.EVENT_OCCURRED)
    public void notify(EventBean eventBean) {
        eventNotifyService.notify(eventBean);
    }
}
