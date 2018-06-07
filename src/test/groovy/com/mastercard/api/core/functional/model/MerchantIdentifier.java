package com.mastercard.api.core.functional.model;

import com.mastercard.api.core.exception.ApiException;
import com.mastercard.api.core.model.*;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by andrearizzini on 08/03/2017.
 */
public class MerchantIdentifier extends BaseObject {

    public MerchantIdentifier() {
    }

    private static String host = "https://stage.api.mastercard.com";

    public MerchantIdentifier(BaseObject o) {
        putAll(o);
    }

    public MerchantIdentifier(Map m) {
        putAll(m);
    }


    @Override protected final OperationConfig getOperationConfig(String operationUUID) throws IllegalArgumentException{
        return new OperationConfig("/MerchantID/openAPI/merchantid/v1/merchantid", Action.query, Arrays.asList("Type", "MerchantId"), Arrays.asList(""));

    }

    @Override protected OperationMetadata getOperationMetadata() throws IllegalArgumentException {
        return new OperationMetadata("0.0.1", host, null);
    }

    public static void setHost(String host) {
        MerchantIdentifier.host = host;
    }

    /**
     * Query / Retrieve a <code>Insights</code> object.
     *
     * @param       query a map of additional query parameters
     *
     * @return      a Insights object
     *
     */
    public static MerchantIdentifier query(RequestMap map)
            throws ApiException {

        return new MerchantIdentifier(BaseObject.executeOperation(null, "uuid", new MerchantIdentifier(map)));
    }

}
