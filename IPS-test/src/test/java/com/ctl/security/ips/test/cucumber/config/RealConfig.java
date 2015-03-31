
package com.ctl.security.ips.test.cucumber.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by Sean Robb on 3/30/2015.
 *
 */
@Configuration
@Profile({"ts", "qa","prod"})
public class RealConfig {


}