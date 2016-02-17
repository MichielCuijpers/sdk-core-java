package com.mastercard.api.core

import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpHead
import org.apache.http.client.methods.HttpPatch
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by eamondoyle on 16/02/2016.
 */
class HttpMethodSpec extends Specification {

    def 'test class types' () {
        expect:
        HttpMethod.DELETE.requestType == HttpDelete.class
        HttpMethod.GET.requestType == HttpGet.class
        HttpMethod.HEAD.requestType == HttpHead.class
        HttpMethod.PATCH.requestType == HttpPatch.class
        HttpMethod.POST.requestType == HttpPost.class
        HttpMethod.PUT.requestType == HttpPut.class
    }

    @Unroll
    def 'test action [#action] returns method [#method]' () {
        given:
        HttpMethod httpMethod = HttpMethod.fromAction(action)

        expect:
        httpMethod == method

        where:
        action | method
        Action.create | HttpMethod.POST
        Action.delete | HttpMethod.DELETE
        Action.list | HttpMethod.GET
        Action.read | HttpMethod.GET
        Action.update | HttpMethod.PUT
    }

    @Unroll
    def 'test method [#method] returns http method string [#string]' () {
        expect:
        method.httpMethodAsString == string

        where:
        method              | string
        HttpMethod.POST     | "POST"
        HttpMethod.DELETE   | "DELETE"
        HttpMethod.HEAD     | "HEAD"
        HttpMethod.PATCH    | "PATCH"
        HttpMethod.POST     | "POST"
        HttpMethod.GET      | "GET"
        HttpMethod.PUT      | "PUT"
    }

}
