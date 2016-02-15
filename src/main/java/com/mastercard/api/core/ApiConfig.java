package com.mastercard.api.core;

import com.mastercard.api.core.security.Authentication;

/**
 * SDK Configuration Overrides
 */
public class ApiConfig {
    public static boolean sandbox = true;
    public static boolean debug = false;
    public static Authentication authentication;

    /**
     * SDK will use sanbox APIs instead of production APIs
     * @param sandbox
     */
    public static void setSandbox(boolean sandbox) {
        ApiConfig.sandbox = sandbox;
    }

    /**
     * Turn on debug logging for the SDK
     * @param debug
     */
    public static void setDebug(boolean debug) {
        ApiConfig.debug = debug;
    }

    /**
     * Set the global authentication object that will be used for all API requests
     * This will only be used if an authentication object is not passed to an SDK method
     * e.g.
     * <code>Object.create()</code>
     * <code>Object.read()</code>
     * <code>Object.list()</code>
     * <code>Object.update()</code>
     * <code>Object.delete()</code>
     *
     * @param authentication
     */
    public static void setAuthentication(Authentication authentication) {
        ApiConfig.authentication = authentication;
    }
}
