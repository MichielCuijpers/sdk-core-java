package com.mastercard.api.core.mocks
import com.mastercard.api.core.model.Action
import com.mastercard.api.core.model.OperationMetadata

/**
 * Created by eamondoyle on 17/02/2016.
 */
class MockComplexTwoObject extends MockBaseObject {


    MockComplexTwoObject(Action action, Map data) {
        super(action, data)
        this.resourcePath = "/mock/v2//MockComplexTwoObject";
    }

    MockComplexTwoObject(String resourcePath, Action action, Map data, List<String> queryParams) {
        super(resourcePath, action, data, queryParams)
    }

    @Override protected OperationMetadata getOperationMetadata() throws IllegalArgumentException {
        return new OperationMetadata("mockComplexTwoObject:0.0.1", super.getOperationMetadata().host, "", true, "text/json")
    }

}
