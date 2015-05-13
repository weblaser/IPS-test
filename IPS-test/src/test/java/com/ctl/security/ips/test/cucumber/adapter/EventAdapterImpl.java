package com.ctl.security.ips.test.cucumber.adapter;

import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import com.ctl.security.ips.common.domain.Event.DpiEvent;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * Created by Sean Robb on 3/30/2015.
 */

public class EventAdapterImpl implements EventAdapter {

    @Override
    public void triggerDpiEvent(ConfigurationItem configurationItem, List<DpiEvent> dpiEvents) {

        try {
            String dip = configurationItem.getHostName(); // ip address
            int port = 4321;
            PrintWriter output = new PrintWriter(new Socket(dip, port).getOutputStream());
            output.print("magicstring");
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
