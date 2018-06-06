package com.mastercard.api.core.functional.model;

import com.mastercard.api.core.exception.ApiException;
import com.mastercard.api.core.model.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrearizzini on 08/03/2017.
 */
public class JSONEcho extends BaseObject {

    private static Map<String, OperationConfig> operationConfigs;

    static {
        operationConfigs = new HashMap<String, OperationConfig>();
        operationConfigs.put("25cf4d3e-3606-433c-8fcc-1df3813d28d5", new OperationConfig("/mock_crud_server/echo", Action.create, Arrays.asList(""), Arrays.asList("")));
    }

    public JSONEcho() {
    }

    private static String host = "http://localhost:8081";

    public JSONEcho(BaseObject o) {
        putAll(o);
    }

    public JSONEcho(Map m) {
        putAll(m);
    }

    @Override protected final OperationConfig getOperationConfig(String operationUUID) throws IllegalArgumentException{
        OperationConfig operationConfig = operationConfigs.get(operationUUID);

        if(operationConfig == null) {
            throw new IllegalArgumentException("Invalid operationUUID supplied: " + operationUUID);
        }

        return operationConfig;
    }

    @Override protected OperationMetadata getOperationMetadata() throws IllegalArgumentException {
        return new OperationMetadata(ResourceConfig.getInstance().getVersion(), host, ResourceConfig.getInstance().getContext());
    }

    /**
     * Query / Retrieve a <code>JSONEcho</code> object.
     *
     * @param   map a map of additional query parameters
     * @return  a JSONEcho object
     */
    public static JSONEcho create(RequestMap map)
            throws ApiException {

        return new JSONEcho(BaseObject.executeOperation(null, "25cf4d3e-3606-433c-8fcc-1df3813d28d5", new JSONEcho(map)));
    }
}
