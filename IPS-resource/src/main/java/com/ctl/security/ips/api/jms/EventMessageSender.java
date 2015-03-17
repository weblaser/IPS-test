package com.ctl.security.ips.api.jms;

import com.ctl.security.ips.common.jms.EventOperation;
import com.ctl.security.ips.common.jms.bean.EventBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by sean.robb on 3/17/2015.
 */
@Component
public class EventMessageSender {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void notify(EventBean eventBean){
        jmsTemplate.convertAndSend(EventOperation.EVENT_OCCURRED, eventBean);
    }
}
