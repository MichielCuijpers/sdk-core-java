package com.mastercard.api.core;

import org.apache.http.client.methods.*;

/**
 * Created by eamondoyle on 11/02/2016.
 */
public enum Method {
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

    private Method(Class<? extends HttpRequestBase> type) {
        this.requestType = type;
    }

    public String getMethod() {
        try {
            return this.requestType.newInstance().getMethod().toUpperCase();
        }
        catch (Exception e) {
            return this.name();
        }
    }

    /**
     *
     * @param action
     * @return
     */
    public static Method fromAction(Action action) {
        Method method = null;

        switch (action) {
            case show:
                method = GET;
                break;
            case list:
                method = GET;
                break;
            case update:
                method = PUT;
                break;
            case create:
                method = POST;
                break;
            case delete:
                method = DELETE;
                break;
        }

        return method;
    }
}