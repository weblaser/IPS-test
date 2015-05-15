package com.ctl.security.ips.test.cucumber.adapter;

import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import com.ctl.security.ips.common.domain.Event.DpiEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * Created by Sean Robb on 3/30/2015.
 */

public class EventAdapterImpl implements EventAdapter {

    private static final Logger logger = LogManager.getLogger(EventAdapterImpl.class);

    @Override
    public void triggerDpiEvent(ConfigurationItem configurationItem, List<DpiEvent> dpiEvents) {

        try {
            logger.info("Attacking " + configurationItem.getHostName());
            PrintWriter output = new PrintWriter(new Socket(configurationItem.getHostName(), 4321).getOutputStream());
            output.print("magicstring");
            output.flush();
            output.close();
            logger.info("Attack Complete...");
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
