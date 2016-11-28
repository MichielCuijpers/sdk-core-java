package com.mastercard.api.core.model;

/**
 * Created by andrearizzini on 24/08/2016.
 */
public class OperationMetadata {
    String apiVersion;
    String host;
    String context;

    public OperationMetadata(String apiVersion, String host) {
        this.apiVersion = apiVersion;
        this.host = host;
    }

    public OperationMetadata(String apiVersion, String host, String context) {
        this.apiVersion = apiVersion;
        this.host = host;
        this.context = context;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getHost() {
        return host;
    }

    public String getContext() {
        return context;
    }
}
