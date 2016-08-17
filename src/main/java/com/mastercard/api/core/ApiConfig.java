package com.mastercard.api.core;

import com.mastercard.api.core.security.Authentication;
import com.mastercard.api.core.security.CryptographyInterceptor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * SDK Configuration Overrides
 */
public final class ApiConfig {
    private static boolean sandbox = true;
    private static boolean debug = false;
    private static String host = null;
    private static Authentication authentication;
    private static Map<String,CryptographyInterceptor> cryptographyMap = new HashMap<>();

    /**
     * SDK will use sandbox APIs instead of production APIs
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

    public static void setHostOverride(String host) throws MalformedURLException{
        if (host != null) {
            URL url = new URL(host);
            ApiConfig.host = url.toString();
        } else {
            ApiConfig.host = null;
        }

    }

    public static String getHostOverride() {
        return ApiConfig.host;
    }

    /**
     * Set the global authentication object that will be used for all API requests
     * This will only be used if an authentication object is not passed to an SDK method
     * e.g.
     * <code>Object.create()</code>
     * <code>Object.read()</code>
     * <code>Object.query()</code>
     * <code>Object.list()</code>
     * <code>object.update()</code>
     * <code>object.delete()</code>
     *
     * @param authentication
     */
    public static void setAuthentication(Authentication authentication) {
        ApiConfig.authentication = authentication;
    }

    public static Authentication getAuthentication() {
        return authentication;
    }

    /**
     * add a crypto interceptor
     * @param cryptographyInterceptor
     */
    public static void addCryptographyInterceptor(CryptographyInterceptor cryptographyInterceptor) {
        if (!cryptographyMap.containsKey(cryptographyInterceptor.getTriggeringPath())){
            cryptographyMap.put(cryptographyInterceptor.getTriggeringPath(), cryptographyInterceptor);
        }
    }

    /**
     * this methid return the crypto interceptor
     * @param basePath
     * @return
     */
    public static CryptographyInterceptor getCryptographyInterceptor(String basePath) {
        for (Map.Entry<String,CryptographyInterceptor> entry : cryptographyMap.entrySet()) {
            if (entry.getKey().contains(basePath) || basePath.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
