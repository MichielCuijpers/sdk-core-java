package com.mastercard.api.core.model;

import java.util.List;

/**
 * Created by andrearizzini on 24/08/2016.
 */
public class OperationConfig {

    String resourcePath;
    Action action;
    List<String> queryParams;
    List<String> headerParams;

    public String getResourcePath() {
        return resourcePath;
    }

    public Action getAction() {
        return action;
    }

    public List<String> getQueryParams() {
        return queryParams;
    }

    public List<String> getHeaderParams() {
        return headerParams;
    }

    public OperationConfig(String resourcePath, Action action, List<String> queryParams,
            List<String> headerParams) {
        this.resourcePath = resourcePath;
        this.action = action;
        this.queryParams = queryParams;
        this.headerParams = headerParams;
    }


}
