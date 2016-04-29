package com.mastercard.api.core.mocks

import com.mastercard.api.core.ApiController
import com.mastercard.api.core.model.Action
import com.mastercard.api.core.model.BaseObject

/**
 * Created by eamondoyle on 17/02/2016.
 */
class MockBaseObject extends BaseObject {

    @Override
    protected String getResourcePath(Action action) throws IllegalArgumentException {
        return "/mock/MockObject"
    }

    @Override
    protected List<String> getHeaderParams(Action action) throws IllegalArgumentException {
        return Arrays.asList("header-param")
    }


    public static void setApiController(ApiController apiController) {
        BaseObject.apiController = apiController;
    }
}
