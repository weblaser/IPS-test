package com.ctl.security.ips.test.cucumber.config;

import com.ctl.security.ips.test.cucumber.adapter.EventAdapter;
import com.ctl.security.ips.test.cucumber.adapter.EventAdapterImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by Sean Robb on 3/30/2015.
 *
 */
@Configuration
@Profile({"ts", "qa","prod"})
public class RealConfig {

    @Bean EventAdapter eventAdapter(){
        return new EventAdapterImpl();
    }
}
