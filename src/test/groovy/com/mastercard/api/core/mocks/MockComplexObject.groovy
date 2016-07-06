package com.mastercard.api.core.mocks

import com.mastercard.api.core.ApiController
import com.mastercard.api.core.model.Action
import com.mastercard.api.core.model.BaseObject

/**
 * Created by eamondoyle on 17/02/2016.
 */
class MockComplexObject extends BaseObject {
    @Override
    protected String getResourcePath(Action action) throws IllegalArgumentException {
        return "/mock/v2/{mock-type-id}/MockObjectComplex"
    }

    @Override
    protected List<String> getHeaderParams(Action action) throws IllegalArgumentException {
        return Arrays.asList("header-param")
    }

    @Override
    protected List<String> getQueryParams(Action action) throws IllegalArgumentException {
        return Arrays.asList("query-param")
    }

    @Override protected String getApiVersion() {
        return "0.0.1";
    }

    public static void setApiController(ApiController apiController) {
        this.apiController = apiController;
    }
}
