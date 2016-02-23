package com.mastercard.api.core

import com.mastercard.api.core.exception.ApiCommunicationException
import com.mastercard.api.core.exception.AuthenticationException
import com.mastercard.api.core.exception.InvalidRequestException
import com.mastercard.api.core.exception.MessageSignerException
import com.mastercard.api.core.exception.NotAllowedException
import com.mastercard.api.core.exception.ObjectNotFoundException
import com.mastercard.api.core.exception.SystemException
import com.mastercard.api.core.mocks.MockAuthentication
import com.mastercard.api.core.mocks.MockBaseObject
import com.mastercard.api.core.mocks.MockHttpClient
import com.mastercard.api.core.mocks.MockHttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpResponseException
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.ContentType
import org.json.simple.JSONValue
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by eamondoyle on 11/02/2016.
 */
class ApiControllerSpec extends Specification {
    @Shared MockAuthentication mockAuthentication

    def setupSpec() {
        ApiConfig.setSandbox(true)
    }

    def setup() {
        mockAuthentication = new MockAuthentication()
        ApiConfig.authentication = mockAuthentication
    }

    def "test constructor: ApiController(String basePath)" () {
        when:
        ApiController apiController = new ApiController("/mock")

        then:
        apiController.apiPath == "$ApiController.API_BASE_SANDBOX_URL/mock"

        when:
        new ApiController(null)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "basePath cannot be empty"

        when:
        new ApiController("")

        then:
        ex = thrown(IllegalArgumentException)
        ex.message == "basePath cannot be empty"

        when:
        new ApiController(" ")

        then:
        ex = thrown(IllegalArgumentException)
        ex.message == "basePath cannot be empty"
    }

    def "test checkState" () {
        given:
        String originalLive = ApiController.API_BASE_LIVE_URL
        String originalSandbox = ApiController.API_BASE_SANDBOX_URL
        ApiController apiController = new ApiController("/mock")

        when:
        ApiController.API_BASE_LIVE_URL = "API_BASE_LIVE_URL"
        apiController.checkState()

        then:
        def ex = thrown(IllegalStateException)
        ex.message == "Invalid URL supplied for API_BASE_LIVE_URL"

        when:
        ApiController.API_BASE_LIVE_URL = originalLive
        ApiController.API_BASE_SANDBOX_URL = "API_BASE_SANDBOX_URL"
        apiController.checkState()

        then:
        ex = thrown(IllegalStateException)
        ex.message == "Invalid URL supplied for API_BASE_SANDBOX_URL"

        cleanup:
        ApiController.API_BASE_LIVE_URL = originalLive
        ApiController.API_BASE_SANDBOX_URL = originalSandbox
    }

    def "test appendToQueryString" () {
        given:
        ApiController apiController = new ApiController("/mock")
        StringBuilder sb = new StringBuilder()

        when:
        sb = apiController.appendToQueryString(sb, "a=1")

        then:
        sb.toString() == "?a=1"

        when:
        sb = apiController.appendToQueryString(sb, "b=2")

        then:
        sb.toString() == "?a=1&b=2"

        when:
        sb = apiController.appendToQueryString(sb, "c=true")

        then:
        sb.toString() == "?a=1&b=2&c=true"
    }

    def "test urlEncode" () {
        given:
        ApiController apiController = new ApiController("/mock")
        String stringToEncode = "a b+="

        when:
        String encoded = apiController.urlEncode(stringToEncode)

        then:
        encoded == "a+b%2B%3D"
    }

    def "test getURI" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        String basePath = mockBaseObject.getBasePath()
        String type = mockBaseObject.getObjectType()
        Action action
        Map<String, Object> objectMapWithParams = [random: "abc", id: "123", max: "10", offset: "1"]
        Map<String, Object> objectMapNoId = [:]

        ApiController apiController = new ApiController(basePath)

        String baseResultUri = "$ApiController.API_BASE_SANDBOX_URL$basePath/$type"
        String jsonFormat = "Format=JSON"
        StringBuilder sb = new StringBuilder()


        // Action.create
        when: "getURI with action create and object map no id"
        action = Action.create
        sb = new StringBuilder()
                .append(baseResultUri)
                .append("?$jsonFormat")
        URI uri = apiController.getURI(type, action, objectMapNoId)

        then: "we get /mock with no params"
        uri.toASCIIString() == sb.toString()

        when: "getURI with action create and object map with params including id"
        uri = apiController.getURI(type, action, objectMapWithParams.clone())

        then: "we get /mock with no params"
        uri.toASCIIString() == sb.toString()


        // Action.read
        when: "getURI with action read and object map no id"
        action = Action.read
        sb = new StringBuilder()
                .append(baseResultUri)
                .append("?$jsonFormat")

        uri = apiController.getURI(type, action, objectMapNoId)

        then: "we get /mock with no params"
        uri.toASCIIString() == sb.toString()

        when: "getURI with action read and object map with params including id"
        sb = new StringBuilder()
                .append(baseResultUri)
                .append("/$objectMapWithParams.id")
                .append("?random=$objectMapWithParams.random&")
                .append("max=$objectMapWithParams.max&")
                .append("offset=$objectMapWithParams.offset&")
                .append(jsonFormat)

        uri = apiController.getURI(type, action, objectMapWithParams.clone())

        then: "we get /mock with all params appended"
        uri.toASCIIString() == sb.toString()


        // Action.update
        when: "getURI with action update and object map no id"
        action = Action.update
        sb = new StringBuilder()
                .append(baseResultUri)
                .append("?" + jsonFormat)

        uri = apiController.getURI(type, action, objectMapNoId)

        then: "we get /mock with no params"
        uri.toASCIIString() == sb.toString()

        when: "getURI with action update and object map with params including id"
        sb = new StringBuilder()
                .append(baseResultUri)
                .append("/$objectMapWithParams.id")
                .append("?$jsonFormat")

        uri = apiController.getURI(type, action, objectMapWithParams.clone())

        then: "we get /mock with id"
        uri.toASCIIString() == sb.toString()


        // Action.delete
        when: "getURI with action delete and object map no id"
        action = Action.delete
        sb = new StringBuilder()
                .append(baseResultUri)
                .append("?$jsonFormat")

        uri = apiController.getURI(type, action, objectMapNoId)

        then: "we get /mock with no params"
        uri.toASCIIString() == sb.toString()

        when: "getURI with action delete and object map with params including id"
        sb = new StringBuilder()
                .append(baseResultUri)
                .append("/$objectMapWithParams.id")
                .append("?random=$objectMapWithParams.random&")
                .append("max=$objectMapWithParams.max&")
                .append("offset=$objectMapWithParams.offset&")
                .append(jsonFormat)

        uri = apiController.getURI(type, action, objectMapWithParams.clone())

        then: "we get /mock with all params appended"
        uri.toASCIIString() == sb.toString()


        // Action.list
        when: "getURI with action list and object map no id"
        action = Action.list
        sb = new StringBuilder()
                .append(baseResultUri)
                .append("?$jsonFormat")

        uri = apiController.getURI(type, action, objectMapNoId)

        then: "we get /mock with no params"
        uri.toASCIIString() == sb.toString()

        when: "getURI with action list and object map with params including id"
        sb = new StringBuilder()
                .append(baseResultUri)
                .append("?random=$objectMapWithParams.random&")
                .append("id=$objectMapWithParams.id&")
                .append("max=$objectMapWithParams.max&")
                .append("offset=$objectMapWithParams.offset&")
                .append(jsonFormat)

        uri = apiController.getURI(type, action, objectMapWithParams.clone())

        then: "we get /mock with all params appended"
        uri.toASCIIString() == sb.toString()

        when: "invalid uri"
        apiController.getURI("%", action, [a:"%"])

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

    }

    def "test getRequest null authentication" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        ApiConfig.authentication = null
        ApiController apiController = new ApiController(mockBaseObject.getBasePath())

        when:
        apiController.getRequest(null, null, null, null, null)

        then:
        thrown(MessageSignerException)

    }

    def "test getRequest authentication" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        String basePath = mockBaseObject.getBasePath()
        Map<String, Object> objectMap = [a: 1]
        Action action = Action.create

        MockAuthentication providedAuthentication = new MockAuthentication()

        ApiController apiController = new ApiController(basePath)
        URI uri = apiController.getURI(mockBaseObject.getObjectType(), action, objectMap)

        when: "using global authentication"
        apiController.getRequest(null, uri, action, objectMap, [:])

        then: "global authentication is called"
        mockAuthentication.called == true
        providedAuthentication.called == false

        when: "using provided authentication"
        providedAuthentication = new MockAuthentication()
        mockAuthentication = new MockAuthentication()
        ApiConfig.authentication = mockAuthentication
        apiController.getRequest(providedAuthentication, uri, action, objectMap, [:])

        then: "provided authentication is called"
        mockAuthentication.called == false
        providedAuthentication.called == true
    }

    def "test getRequest create | update" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        String basePath = mockBaseObject.getBasePath()
        Action action = Action.create
        Map<String, Object> objectMap = [a: "a", b: "b", id: 1]
        Map<String, Object> headers = ['x-sdk-mock-header': "x-sdk-mock-value"]

        ApiController apiController = new ApiController(basePath)
        URI uri = apiController.getURI(mockBaseObject.getObjectType(), action, objectMap)

        when: "getRequest for create"
        HttpRequestBase httpRequestBase = apiController.getRequest(null, uri, action, objectMap, headers)

        then:
        httpRequestBase instanceof HttpPost
        ((HttpPost) httpRequestBase).method == "POST"
        ((HttpPost) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        ((HttpPost) httpRequestBase).getEntity().content.text == JSONValue.toJSONString(objectMap)
        assertHeaders(httpRequestBase,headers)

        when: "getRequest for update"
        action = Action.update
        httpRequestBase = apiController.getRequest(null, uri, action, objectMap, headers)

        then:
        httpRequestBase instanceof HttpPut
        ((HttpPut) httpRequestBase).method == "PUT"
        ((HttpPut) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        ((HttpPut) httpRequestBase).getEntity().content.text == JSONValue.toJSONString(objectMap)
        assertHeaders(httpRequestBase,headers)
    }

    def "test getRequest read | list | delete" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        String basePath = mockBaseObject.getBasePath()
        Action action = Action.read
        Map<String, Object> objectMap = [a: "a", b: "b", id: 1]
        Map<String, Object> headers = ['x-sdk-mock-header': "x-sdk-mock-value"]

        ApiController apiController = new ApiController(basePath)
        URI uri = apiController.getURI(mockBaseObject.getObjectType(), action, objectMap.clone())

        when: "getRequest for read"
        HttpRequestBase httpRequestBase = apiController.getRequest(null, uri, action, objectMap, headers)

        then:
        httpRequestBase instanceof HttpGet
        ((HttpGet) httpRequestBase).method == "GET"
        ((HttpGet) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        assertHeaders(httpRequestBase,headers)

        when: "getRequest for list"
        action = Action.list
        httpRequestBase = apiController.getRequest(null, uri, action, objectMap, headers)

        then:
        httpRequestBase instanceof HttpGet
        ((HttpGet) httpRequestBase).method == "GET"
        ((HttpGet) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        assertHeaders(httpRequestBase,headers)

        when: "getRequest for delete"
        action = Action.delete
        httpRequestBase = apiController.getRequest(null, uri, action, objectMap, headers)

        then:
        httpRequestBase instanceof HttpDelete
        ((HttpDelete) httpRequestBase).method == "DELETE"
        ((HttpDelete) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        assertHeaders(httpRequestBase,headers)
    }

    def "test getRequest null USER_AGENT" () {
        given:
        ApiController.USER_AGENT = "mock"

        MockBaseObject mockBaseObject = new MockBaseObject()
        String basePath = mockBaseObject.getBasePath()
        Action action = Action.read
        Map<String, Object> objectMap = [a: "a", b: "b", id: 1]
        Map<String, Object> headers = ['x-sdk-mock-header': "x-sdk-mock-value"]

        ApiController apiController = new ApiController(basePath)
        URI uri = apiController.getURI(mockBaseObject.getObjectType(), action, objectMap)

        when:
        HttpRequestBase httpRequestBase = apiController.getRequest(null, uri, action, objectMap, headers)

        then:
        httpRequestBase.getFirstHeader("User-Agent").value == "Java-SDK/$Constants.VERSION mock" as String

        cleanup:
        ApiController.USER_AGENT = null
    }

    private void assertHeaders(HttpRequestBase httpRequestBase, Map<String, String> customHeaders) {
        assert httpRequestBase.getFirstHeader("Accept").value == "application/json"
        assert httpRequestBase.getFirstHeader("User-Agent").value == "Java-SDK/$Constants.VERSION" as String
        assert httpRequestBase.getFirstHeader("MockAuthentication").value == "MockValue"

        customHeaders.each {
            assert httpRequestBase.getFirstHeader(it.key).value == it.value
        }
    }

    def "test getAction exceptions" () {
        given:
        ApiController apiController = new ApiController("/mock")

        when:
        apiController.getAction(null)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Action cannot be null"

        when:
        apiController.getAction("invalid")

        then:
        ex = thrown(IllegalArgumentException)
        ex.message == "Invalid action supplied: invalid"
    }

    @Unroll
    def "test getAction #actionStr #action" () {
        given:
        ApiController apiController = new ApiController("/mock")

        when:
        Action result = apiController.getAction(actionStr)

        then:
        result == action

        where:
        actionStr | action
        "create" | Action.create
        "delete" | Action.delete
        "list" | Action.list
        "read" | Action.read
        "update" | Action.update
    }

    def "test execute IllegalStateException when urlEncode throws exception" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        String basePath = mockBaseObject.getBasePath()
        String  action = "read"
        String type = mockBaseObject.getObjectType()
        Map<String, Object> objectMap = [id: 1]

        ApiController apiController = Spy(ApiController, constructorArgs: [basePath]) {
            urlEncode(_) >> { throw new UnsupportedEncodingException("mock encoding") }
        }

        when:
        apiController.execute(null, type, action, objectMap)

        then:
        thrown(IllegalStateException)
    }

    @Unroll
    def "test execute mockHttpResponse #mockHttpResponse executeResult #executeResult" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        String basePath = mockBaseObject.getBasePath()
        String  action = "create"
        String type = mockBaseObject.getObjectType()
        Map<String, Object> objectMap = [a: 1]
        MockHttpClient mockHttpClient = new MockHttpClient(mockHttpResponse)

        ApiController apiController = Spy(ApiController, constructorArgs: [basePath]) {
            createHttpClient() >> mockHttpClient
        }

        when:
        Map<String, Object> response = apiController.execute(null, type, action, objectMap)

        then:
        response == executeResult
        mockHttpClient.closed == true

        where:
        mockHttpResponse | executeResult
        new MockHttpResponse(200, MockHttpResponse.defaultJsonResponse) | MockHttpResponse.defaultJsonResponse
        new MockHttpResponse(200, [MockHttpResponse.defaultJsonResponse, MockHttpResponse.defaultJsonResponse]) | [list: [MockHttpResponse.defaultJsonResponse, MockHttpResponse.defaultJsonResponse]]
        new MockHttpResponse(201, MockHttpResponse.defaultJsonResponse) | MockHttpResponse.defaultJsonResponse
        new MockHttpResponse(204, null) | null
    }

    @Unroll
    def "test execute exceptions mockHttpResponse #mockHttpResponse thrownEx #thrownEx" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        String basePath = mockBaseObject.getBasePath()
        String  action = "create"
        String type = mockBaseObject.getObjectType()
        Map<String, Object> objectMap = [a: 1]
        MockHttpClient mockHttpClient = new MockHttpClient(mockHttpResponse)

        ApiController apiController = Spy(ApiController, constructorArgs: [basePath]) {
            createHttpClient() >> mockHttpClient
        }

        when:
        apiController.execute(null, type, action, objectMap)

        then:
        thrown(thrownEx)
        mockHttpClient.closed == true

        where:
        mockHttpResponse                            | thrownEx
        new MockHttpResponse(200, null)             | ApiCommunicationException
        new MockHttpResponse(400, [error: "error"]) | InvalidRequestException
        new MockHttpResponse(401, [error: "error"]) | AuthenticationException
        new MockHttpResponse(403, [error: "error"]) | NotAllowedException
        new MockHttpResponse(404, [error: "error"]) | ObjectNotFoundException
        new MockHttpResponse(500, [error: "error"]) | SystemException
        new MockHttpResponse(503, [error: "error"]) | ApiCommunicationException
        new MockHttpResponse(503, [error: "error"], { throw new HttpResponseException(503, "mock") }) | ApiCommunicationException
        new MockHttpResponse(503, [error: "error"], { throw new ClientProtocolException() }) | ApiCommunicationException
        new MockHttpResponse(503, [error: "error"], { throw new IOException() }) | ApiCommunicationException
    }

    @Unroll
    def "test execute content type mockHttpResponse #mockHttpResponse thrownEx #thrownEx" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        String basePath = mockBaseObject.getBasePath()
        String  action = "delete"
        String type = mockBaseObject.getObjectType()
        Map<String, Object> objectMap = [a: 1]

        MockHttpResponse mockHttpResponse = new MockHttpResponse(200, MockHttpResponse.defaultJsonResponse)
        mockHttpResponse.contentType = "$ContentType.APPLICATION_JSON.mimeType; $ContentType.APPLICATION_XML.mimeType"
        MockHttpClient mockHttpClient = new MockHttpClient(mockHttpResponse)

        ApiController apiController = Spy(ApiController, constructorArgs: [basePath]) {
            createHttpClient() >> mockHttpClient
        }

        when:
        def response = apiController.execute(null, type, action, objectMap)

        then:
        response

        when:
        response = apiController.execute(null, type, action, null)

        then:
        response

        when:
        mockHttpResponse.contentType = "$ContentType.APPLICATION_XML.mimeType; $ContentType.APPLICATION_JSON.mimeType"
        apiController.execute(null, type, action, objectMap)

        then:
        thrown(ApiCommunicationException)

        when:
        mockHttpResponse.contentType = null
        apiController.execute(null, type, action, objectMap)

        then:
        thrown(IllegalStateException)

    }

}
