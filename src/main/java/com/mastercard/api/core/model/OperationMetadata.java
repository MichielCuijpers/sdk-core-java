package com.mastercard.api.core.model;

/**
 * Created by andrearizzini on 24/08/2016.
 */
public class OperationMetadata {
    String apiVersion;
    String host;

    public OperationMetadata(String apiVersion, String host) {
        this.apiVersion = apiVersion;
        this.host = host;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getHost() {
        return host;
    }
}
