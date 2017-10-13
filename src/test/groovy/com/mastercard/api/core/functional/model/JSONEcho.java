package com.mastercard.api.core.functional.model;

import com.mastercard.api.core.exception.ApiException;
import com.mastercard.api.core.model.*;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by andrearizzini on 08/03/2017.
 */
public class JSONEcho extends BaseObject {

    public JSONEcho() {
    }

    private static String host = "https://stage.api.mastercard.com";

    public JSONEcho(BaseObject o) {
        putAll(o);
    }

    public JSONEcho(Map m) {
        putAll(m);
    }


    @Override protected final OperationConfig getOperationConfig(String operationUUID) throws IllegalArgumentException{
        //return new OperationConfig("/mcapitest/JsonNativePost", Action.create, Arrays.asList(""), Arrays.asList(""));
        return new OperationConfig("/mcapitest/JsonNativePostPublic", Action.create, Arrays.asList(""), Arrays.asList(""));

    }

    @Override protected OperationMetadata getOperationMetadata() throws IllegalArgumentException {
        return new OperationMetadata("0.0.1", host, null);
    }

    public static void setHost(String host) {
        JSONEcho.host = host;
    }

    /**
     * Query / Retrieve a <code>Insights</code> object.
     *
     * @param       query a map of additional query parameters
     *
     * @return      a Insights object
     *
     */
    public static JSONEcho create(RequestMap map)
            throws ApiException {

        return new JSONEcho(BaseObject.executeOperation(null, "uuid", new JSONEcho(map)));
    }

}
