package com.mastercard.api.core.security.utils;

/**
 * Created by andrearizzini on 12/05/2016.
 */
public enum SecurityParameters {
    RSA_TRANSFORM("RSA/ECB/PKCS1Padding"),
    AES_TRANSFORM("AES/CBC/PKCS5Padding"),  //This does the best padding for English
    AES_ALGORTHM("AES");


    String name;

    SecurityParameters(String name) {
        this.name = name;
    }
}
