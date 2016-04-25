package com.mastercard.api.core.mocks

import com.mastercard.api.core.Action
import com.mastercard.api.core.BaseObject

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
}
