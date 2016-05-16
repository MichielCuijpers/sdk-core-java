package com.mastercard.api.core.security;

import java.util.Map;

/**
 * Created by andrearizzini on 13/05/2016.
 */
public interface CryptographyInterceptor {
    CryptographyContext getContext();

    Map<String,Object> encrypt(Map<String,Object> map);
    Map<String,Object> decrypt(Map<String,Object> map);


}



