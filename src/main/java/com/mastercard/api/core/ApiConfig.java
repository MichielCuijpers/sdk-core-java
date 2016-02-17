package com.mastercard.api.core;

import com.mastercard.api.core.security.Authentication;

/**
 * SDK Configuration Overrides
 */
public class ApiConfig {
    private static boolean sandbox = true;
    private static boolean debug = false;
    private static Authentication authentication;

    /**
     * SDK will use sanbox APIs instead of production APIs
     * @param sandbox
     */
    public static void setSandbox(boolean sandbox) {
        ApiConfig.sandbox = sandbox;
    }

    public static boolean isSandbox() {
        return sandbox;
    }

    /**
     * Turn on debug logging for the SDK
     * @param debug
     */
    public static void setDebug(boolean debug) {
        ApiConfig.debug = debug;
    }

    public static boolean isDebug() {
        return debug;
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

    public static Authentication getAuthentication() {
        return authentication;
    }
}
