package com.ctl.security.ips.common.exception;

/**
 * Created by Kevin.Weber on 10/29/2014.
 */
public class PolicyNotFoundException extends IllegalStateException {
    public PolicyNotFoundException() {
    }

    public PolicyNotFoundException(String s) {
        super(s);
    }

    public PolicyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PolicyNotFoundException(Throwable cause) {
        super(cause);
    }
}
