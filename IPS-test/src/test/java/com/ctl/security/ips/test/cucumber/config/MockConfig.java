package com.ctl.security.ips.test.cucumber.config;

import com.ctl.security.ips.dsm.config.MockDsmBeans;
import com.ctl.security.ips.test.cucumber.adapter.EventAdapter;
import com.ctl.security.ips.test.cucumber.adapter.MockEventAdapterImpl;
import org.springframework.context.annotation.*;

/**
 * Created by Sean Robb on 3/30/2015.
 *
 */
@Configuration
@PropertySource("classpath:properties/ips.dsm.mock.test.properties")
@Profile({"local", "dev"})
public class MockConfig {

    @Bean
    EventAdapter eventAdapter(){
        return new MockEventAdapterImpl();
    }

}
