package com.mastercard.api.core.http;

import com.mastercard.api.core.ApiConfig;
import org.apache.http.impl.client.CustomHttpClientBuilder;

/**
 * Created by andrearizzini on 27/01/2017.
 * Deprecated class, Please use ApiConfig.
 * This class will be removed from version 1.5
 */
@Deprecated
public class HttpBuilder {


    private HttpBuilder() {}


    /**
     *  PLEASE USE ApiConfig.getHttpClientBuilder() instead
     */
    @Deprecated
    public static CustomHttpClientBuilder getInstance() {
        return ApiConfig.getHttpClientBuilder();
    }
}
