package com.mastercard.api.core;

import com.mastercard.api.core.security.Authentication;
import com.mastercard.api.core.security.CryptographyInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * SDK Configuration Overrides
 */
public final class ApiConfig {
    private static boolean sandbox = true;
    private static boolean debug = false;
    private static Authentication authentication;
    private static Map<String,CryptographyInterceptor> cryptographyMap = new HashMap<>();

    /**
     * SDK will use sandbox APIs instead of production APIs
     */
    public static void setSandbox(boolean sandbox) {
        ApiConfig.sandbox = sandbox;
    }

    public static boolean isSandbox() {
        return sandbox;
    }

    public static boolean isProduction() {
        return !sandbox;
    }

    /**
     * Turn on debug logging for the SDK
     * @param debug
     */
    public static void setDebug(boolean debug) {
        ApiConfig.debug = debug;

        if (debug) {
            /**
             # Root logger option
             log4j.rootLogger=DEBUG, stdout

             # Direct log messages to stdout
             log4j.appender.stdout=org.apache.log4j.ConsoleAppender
             log4j.appender.stdout.Target=System.out
             log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
             log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss:SSS} [%t] %-5p %c{2}:%L - %m%n
             */


            java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
            java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);

            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
            System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
            System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");

        } else {
            java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(Level.OFF);
            java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.OFF);

            String[] propertiesToRemove = new String[] { "org.apache.commons.logging.Log", "org.apache.commons.logging.simplelog.showdatetime", "org.apache.commons.logging.simplelog.log.httpclient.wire", "org.apache.commons.logging.simplelog.log.org.apache.http"};
            for (String property : propertiesToRemove) {
                System.getProperties().keySet().remove(property);
            }
        }

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
