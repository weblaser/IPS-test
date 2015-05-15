package com.ctl.security.ips.dsm.domain;

import com.ctl.security.ips.common.domain.Event.DpiEvent;
import manager.DPIEventTransport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by admin on 5/11/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class DpiEventTransportMarshallerTest{

    @InjectMocks
    private DpiEventTransportMarshaller classUnderTest;

    @Test
    public void convert_convertsDpiEventTransportToDpiEvent() throws Exception {
        DPIEventTransport dpiEventTransport = new DPIEventTransport();
        dpiEventTransport.setHostName("hostname");
        dpiEventTransport.setReason("because");
        DpiEvent dpiEvent = classUnderTest.convert(dpiEventTransport);

        assertNotNull(dpiEvent);
        assertEquals(dpiEventTransport.getHostName(), dpiEvent.getHostName());
        assertEquals(dpiEventTransport.getReason(), dpiEvent.getReason());
    }

    @Test
    public void convert_convertsDpiEventToDpiEventTransport() throws Exception {
        DpiEvent dpiEvent = new DpiEvent();
        dpiEvent.setHostName("hostname");
        dpiEvent.setReason("because");
        DPIEventTransport dpiEventTransport = classUnderTest.convert(dpiEvent);

        assertNotNull(dpiEventTransport);
        assertEquals(dpiEvent.getHostName(), dpiEventTransport.getHostName());
        assertEquals(dpiEvent.getReason(), dpiEventTransport.getReason());
    }
}