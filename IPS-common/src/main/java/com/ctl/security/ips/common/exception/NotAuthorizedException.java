package com.ctl.security.ips.common.exception;

/**
 * Created by kevin.weber on 10/30/2014.
 */
public class NotAuthorizedException extends IllegalStateException {

    public NotAuthorizedException() {
    }

    public NotAuthorizedException(String s) {
        super(s);
    }

    public NotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAuthorizedException(Throwable cause) {
        super(cause);
    }
}
