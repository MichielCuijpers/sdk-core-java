package com.mastercard.api.core.mocks

import com.mastercard.api.core.BaseObject

/**
 * Created by eamondoyle on 17/02/2016.
 */
class MockComplexObject extends BaseObject {
    @Override
    protected String getBasePath() {
        return "/mock/v2"
    }

    @Override
    protected String getObjectType() {
        return "/{mock-type-id}/MockObjectComplex/{mock-id}"
    }

    @Override
    protected List<String> getHeaderParams() {
        return Arrays.asList("header-param")
    }
}
