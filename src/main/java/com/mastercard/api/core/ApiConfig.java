package com.mastercard.api.core;

import com.mastercard.api.core.security.Authentication;

public class ApiConfig {
    public static boolean sandbox = true;
    public static boolean debug = false;
    public static Authentication authentication;

    public static void setSandbox(boolean sandbox) {
        ApiConfig.sandbox = sandbox;
    }

    public static void setDebug(boolean debug) {
        ApiConfig.debug = debug;
    }

    public static void setAuthentication(Authentication authentication) {
        ApiConfig.authentication = authentication;
    }
}
