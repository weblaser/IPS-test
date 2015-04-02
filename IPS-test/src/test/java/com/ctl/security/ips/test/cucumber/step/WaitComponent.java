package com.ctl.security.ips.test.cucumber.step;

import org.springframework.stereotype.Component;

/**
 * Created by kevin on 4/2/15.
 */

@Component
public class WaitComponent {
    void sleep(int amount, int currentAttempts) {
        if(currentAttempts > 0) {
            try {
                Thread.sleep(amount);
            } catch (Exception e) {
            }
        }
    }
}
