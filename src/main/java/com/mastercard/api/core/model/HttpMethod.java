package com.mastercard.api.core.model;

import org.apache.http.client.methods.*;

/**
 * Enum to represent HTTP Methods
 */
public enum HttpMethod {
    GET(HttpGet.class),
    PUT(HttpPut.class),
    POST(HttpPost.class),
    DELETE(HttpDelete.class),
    HEAD(HttpHead.class),
    PATCH(HttpPatch.class);

    private final Class<? extends HttpRequestBase> requestType;

    public Class<? extends HttpRequestBase> getRequestType() {
        return this.requestType;
    }

    private HttpMethod(Class<? extends HttpRequestBase> type) {
        this.requestType = type;
    }

    /**
     * Returns the HTTP Method as a String
     * @return
     */
    public String getHttpMethodAsString() {
        try {
            return this.getRequestType().newInstance().getMethod().toUpperCase();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Get HTTP HttpMethod from an Action
     *
     * @param action
     * @return
     */
    public static HttpMethod fromAction(Action action) {
        HttpMethod httpMethod = null;

        switch (action) {
            case query:
                httpMethod = GET;
                break;
            case read:
                httpMethod = GET;
                break;
            case list:
                httpMethod = GET;
                break;
            case update:
                httpMethod = PUT;
                break;
            case create:
                httpMethod = POST;
                break;
            case delete:
                httpMethod = DELETE;
                break;
        }

        return httpMethod;
    }
}