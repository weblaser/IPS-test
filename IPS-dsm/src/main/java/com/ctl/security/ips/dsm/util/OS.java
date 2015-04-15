package com.ctl.security.ips.dsm.util;

/**
 * Created by gary hebel on 4/14/15.
 */
public enum Os {
        CLC_LINUX("CLC Linux"), CLC_WINDOWS("CLC Windows");

        private String value;

        public String getVaule(){
           return value;
        }

        private Os(String value) {
            this.value = value;
        }
}
