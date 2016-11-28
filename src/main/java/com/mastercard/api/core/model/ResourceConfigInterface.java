package com.mastercard.api.core.model;

/**
 * Created by andrearizzini on 22/11/2016.
 */
public interface ResourceConfigInterface {

    void setEnvironment(Environment environment);

    void setEnvironment(String host, String context);

}
