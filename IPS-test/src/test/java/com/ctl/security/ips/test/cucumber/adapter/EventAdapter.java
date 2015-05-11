package com.ctl.security.ips.test.cucumber.adapter;

import com.ctl.security.data.common.domain.mongo.ConfigurationItem;
import com.ctl.security.ips.common.domain.Event.DpiEvent;
import com.ctl.security.ips.common.domain.Event.Event;
import com.ctl.security.ips.common.domain.Event.FirewallEvent;
import manager.FirewallEventListTransport;

import java.util.List;

/**
 * Created by Sean Robb on 3/30/2015.
 */
public interface EventAdapter {
    void triggerDpiEvent(ConfigurationItem configurationItem, List<DpiEvent> dpiEvents);
}
