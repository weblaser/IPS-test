package com.ctl.security.ips.test.cucumber.config;

import com.ctl.security.ips.dsm.adapter.EventAdapter;
import com.ctl.security.ips.dsm.adapter.MockEventAdapterImpl;
import org.springframework.config.java.annotation.Bean;
import org.springframework.config.java.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by Sean Robb on 3/30/2015.
 *
 */
@Configuration
@Profile({"local", "dev"})
public class MockConfig {

    @Bean
    EventAdapter eventAdapter(){
        return new MockEventAdapterImpl();
    }

}
