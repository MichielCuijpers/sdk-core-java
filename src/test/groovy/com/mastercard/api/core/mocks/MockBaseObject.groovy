package com.mastercard.api.core.mocks

import com.mastercard.api.core.ApiController
import com.mastercard.api.core.model.Action
import com.mastercard.api.core.model.BaseObject
import com.mastercard.api.core.model.OperationConfig
import com.mastercard.api.core.model.OperationMetadata

/**
 * Created by eamondoyle on 17/02/2016.
 */
class MockBaseObject extends BaseObject {

    public String resourcePath
    public List<String> headerParams
    public List<String> queryParams
    public String host
    public Action action

    MockBaseObject() {
        action = Action.create
        resourcePath = "/mock/MockObject"
        headerParams = Arrays.asList("x-sdk-mock-header")
        queryParams = Arrays.asList("query-param")
        host = "https://sandbox.api.mastercard.com"
    }

    MockBaseObject(Action action) {
        this()
        this.action = action;
    }

    MockBaseObject(Action action, Map data) {
        this()
        setup(null, action, [], [], data, "")
    }

    MockBaseObject(String resourcePath, Action action, Map data, List<String> queryParams) {
        this()
        setup(resourcePath, action, [], queryParams, data, "")
    }

    MockBaseObject(String resourcePath, Action action, List<String> headerParams, List<String> queryParams, Map data, String host) {
        this()
        setup(resourcePath, action, headerParams, queryParams, data, host)
    }

    private setup(String resourcePath, Action action, List<String> headerParams, List<String> queryParams, Map data, String host) {
        if (resourcePath) {
            this.resourcePath += resourcePath
        }

        this.action = action;

        if (headerParams) {
            this.headerParams = headerParams
        }

        if (queryParams) {
            this.queryParams = queryParams
        }

        if (data) {
            this.putAll(data)
        }

        if (host) {
            this.host = host
        }
    }



    @Override protected OperationConfig getOperationConfig(String operationUUID) throws IllegalArgumentException{
       return new OperationConfig(resourcePath, action, queryParams, headerParams);
    }

    @Override protected OperationMetadata getOperationMetadata() throws IllegalArgumentException {
        return new OperationMetadata("mock:0.0.1", host);
    }


    public static void setApiController(ApiController apiController) {
        BaseObject.apiController = apiController;
    }

}
