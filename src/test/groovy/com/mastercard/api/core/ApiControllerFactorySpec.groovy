package com.mastercard.api.core

import spock.lang.Specification

/**
 * Created by eamondoyle on 11/02/2016.
 */
class ApiControllerFactorySpec extends Specification {

    def "test api controller factory with sandbox"() {
        given:
        ApiConfig.setSandbox(true)
        ApiControllerFactory apiControllerFactory = new ApiControllerFactory()

        when:
        ApiController apiController = apiControllerFactory.createApiController("/mock")

        then:
        apiController.apiPath == Constants.API_BASE_SANDBOX_URL + "/mock"
    }

    def "test api controller factory with prod"() {
        given:
        ApiConfig.setSandbox(false)
        ApiControllerFactory apiControllerFactory = new ApiControllerFactory()

        when:
        ApiController apiController = apiControllerFactory.createApiController("/mock")

        then:
        apiController.apiPath == Constants.API_BASE_LIVE_URL + "/mock"
    }

}
