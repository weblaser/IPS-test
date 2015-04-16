package com.ctl.security.ips.dsm.util;

/**
 * Created by admin on 4/15/2015.
 */
public enum OsType {
    CLC_LINUX("CLC Linux"), CLC_WINDOWS("CLC Windows");

    private String value;

    public String getValue(){
        return value;
    }

    private OsType(String value) {
        this.value = value;
    }
}
