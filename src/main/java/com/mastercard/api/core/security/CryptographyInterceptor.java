package com.mastercard.api.core.security;

import com.mastercard.api.core.model.RequestMap;

/**
 * Created by andrearizzini on 13/05/2016.
 */
public interface CryptographyInterceptor {

    enum CryptographyContext {
        HEADER,BODY,BOTH
    }

    CryptographyContext getContext();

    RequestMap encrypt(RequestMap map);
    RequestMap decrypt(RequestMap map);


}



