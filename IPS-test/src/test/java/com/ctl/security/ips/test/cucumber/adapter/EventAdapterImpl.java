package com.ctl.security.ips.test.cucumber.adapter;

import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import manager.FirewallEventListTransport;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Sean Robb on 3/30/2015.
 */

public class EventAdapterImpl implements EventAdapter {

    @Override
    public void triggerEvent(ConfigurationItem configurationItem, List<FirewallEvent> firewallEvents) {

    }
}
