package com.mastercard.api.core.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrearizzini on 22/11/2016.
 */
public enum Environment {
    PRODUCTION,SANDBOX,STAGE,DEV,MTF,ITF,LOCALHOST,DEVCLOUD,LABSCLOUD,OTHER1,OTHER2,OTHER3;

    public static final Map<Environment,String[]> MAPPINGS = new HashMap();
    static {
        MAPPINGS.put(Environment.PRODUCTION, new String[] { "https://api.mastercard.com", null});
        MAPPINGS.put(Environment.SANDBOX, new String[] { "https://sandbox.api.mastercard.com", null});
        MAPPINGS.put(Environment.STAGE, new String[] { "https://stage.api.mastercard.com", null});
        MAPPINGS.put(Environment.DEV, new String[] { "https://dev.api.mastercard.com", null});
        MAPPINGS.put(Environment.MTF, new String[] { "https://sandbox.api.mastercard.com", "mtf"});
        MAPPINGS.put(Environment.ITF, new String[] { "https://sandbox.api.mastercard.com", "itf"});
        MAPPINGS.put(Environment.LOCALHOST, new String[] { "http://localhost:8081", null});
    }
}
