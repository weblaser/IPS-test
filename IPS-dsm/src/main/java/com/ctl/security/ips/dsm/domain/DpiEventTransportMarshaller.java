package com.ctl.security.ips.dsm.domain;

import com.ctl.security.ips.common.domain.Event.DpiEvent;
import manager.DPIEventTransport;
import org.springframework.stereotype.Component;

/**
 * Created by admin on 5/11/2015.
 */
@Component
public class DpiEventTransportMarshaller {

    public DPIEventTransport convert(DpiEvent dpiEvent) {
        DPIEventTransport dpiEventTransport = new DPIEventTransport();
        dpiEventTransport.setHostName(dpiEvent.getHostName());
        dpiEventTransport.setReason(dpiEvent.getReason());
        return dpiEventTransport;
    }

    public DpiEvent convert(DPIEventTransport dpiEventTransport) {
        DpiEvent dpiEvent = new DpiEvent();
        dpiEvent.setHostName(dpiEventTransport.getHostName());
        dpiEvent.setReason(dpiEventTransport.getReason());
        return dpiEvent;
    }
}
