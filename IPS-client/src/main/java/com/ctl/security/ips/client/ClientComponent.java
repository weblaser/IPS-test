package com.ctl.security.ips.client;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Created by sean.robb on 3/5/2015.
 */

@Component
public class ClientComponent {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    public HttpHeaders createHeaders(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, BEARER + bearerToken);
        return headers;
    }
}
