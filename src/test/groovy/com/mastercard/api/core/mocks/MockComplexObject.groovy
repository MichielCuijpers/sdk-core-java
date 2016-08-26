package com.mastercard.api.core.mocks
import com.mastercard.api.core.model.Action
/**
 * Created by eamondoyle on 17/02/2016.
 */
class MockComplexObject extends MockBaseObject {


    MockComplexObject(Action action, Map data) {
        super(action, data)
        this.resourcePath = "/mock/v2/{mock-type-id}/MockObjectComplex";
    }

    MockComplexObject(String resourcePath, Action action, Map data, List<String> queryParams) {
        super(resourcePath, action, data, queryParams)
    }

}
