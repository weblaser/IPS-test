package com.ctl.security.ips.test.cucumber.runner;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(monochrome = true,
        features = {"src/test/java/com/ctl/security/ips/test/cucumber/feature"},
        glue = {"com/ctl/security/ips/test/cucumber/step"}
)
public class SecurityAcceptanceTestRunner {

}
