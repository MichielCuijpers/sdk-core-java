package com.mastercard.api.core.http;

import org.apache.http.impl.client.CustomHttpClientBuilder;

/**
 * Created by andrearizzini on 27/01/2017.
 */
public class HttpBuilder {

    private static CustomHttpClientBuilder customHttpClientBuilder = null;


    private HttpBuilder() {}

    public static CustomHttpClientBuilder getInstance() {
        if (customHttpClientBuilder == null) {
            customHttpClientBuilder = CustomHttpClientBuilder.create();
        }
        return customHttpClientBuilder;
    }
}
