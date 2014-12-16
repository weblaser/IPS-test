package com.ctl.security.ips.test.cucumber.runner;

import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
//@Cucumber.Options(tags = { "@ALL_TESTS" }, monochrome = true, format = {"html:target/cucumber", "junit:target/junit.xml", "json:target/cucumber-report.json"}) 
@Cucumber.Options(tags = {"@Environment, @Smoke, @Integration, @Regression"}, monochrome = true, format = {"html:target/cucumber", "junit:target/junit.xml", "json:target/cucumber-report.json"},
        features = {"src/main/java/com/ctl/security/ips/test/cucumber/feature"},
        glue ={"com/ctl/security/ips/test/cucumber/step"}
)
// NOTES:
// 	1) To only run UI tests add "tags = { "@UI" }, " into Cucumber.Options
//  2) To only run API tests add "tags = { "@API" }, " into Cucumber.Options
//  3) To run UI tests requiring setup, use:  tags = { "@WIP_UI, @SETUP"}
//	4) To run all tests remove the "tags" argument
// 
// example of a "not" tag => tags = { "~@WIP" }
// can use -Dcucumber.options="--format {\"pretty\", \"html:target/cucumber\", \"junit:target/junit.xml\"} --glue classpath:com/savvis src/test/resources"
public class NightlyTestRunner {
    public NightlyTestRunner() {
    }

    @BeforeClass
    public static void setup() {
        // Do NOT start selenium here, only start it based on @Before(value="@UI"), so only a feature file that has @UI causes it to start
        // The @Before is in the UnifyComputeSystemSteps class)
//		WebDriverController.getDriver();

    }

    @AfterClass
    public static void teardown() {
        // stop the Selenium web driver
//		WebDriverController.stopDriver();
    }
}