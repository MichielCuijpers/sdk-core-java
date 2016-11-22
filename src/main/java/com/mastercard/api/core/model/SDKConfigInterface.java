package com.mastercard.api.core.model;

/**
 * Created by andrearizzini on 22/11/2016.
 */
public interface SDKConfigInterface {

    void setEnvironment(Environment environment);

    void setEnvironment(String host, String context);

}
