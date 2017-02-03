package com.mastercard.api.core.security;

import com.mastercard.api.core.exception.SdkException;

import java.util.List;
import java.util.Map;

/**
 * Created by andrearizzini on 13/05/2016.
 */
public interface CryptographyInterceptor {
    List<String> getTriggeringEndPath();
    Map<String,Object> encrypt(Map<String,Object> map) throws SdkException;
    Map<String,Object> decrypt(Map<String,Object> map) throws SdkException;
}



