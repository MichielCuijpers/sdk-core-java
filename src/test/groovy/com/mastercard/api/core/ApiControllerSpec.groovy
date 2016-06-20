package com.mastercard.api.core
import com.mastercard.api.core.exception.*
import com.mastercard.api.core.mocks.*
import com.mastercard.api.core.model.Action
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpResponseException
import org.apache.http.client.methods.*
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

    def cleanupSpec() {
        mockAuthentication = null;
        ApiConfig.authentication = null;
    }

    def "test constructor: ApiController(String basePath)" () {
        when:
        ApiController apiController = new ApiController()

        then:
        apiController.apiPath == "$ApiController.API_BASE_SANDBOX_URL"
    }


    def "test checkState" () {
        given:
        String originalLive = ApiController.API_BASE_LIVE_URL
        String originalSandbox = ApiController.API_BASE_SANDBOX_URL
        ApiController apiController = new ApiController()

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
        ApiController apiController = new ApiController()
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
        ApiController apiController = new ApiController()
        String stringToEncode = "a b+="

        when:
        String encoded = apiController.urlEncode(stringToEncode)

        then:
        encoded == "a+b%2B%3D"
    }

    def "test getURI" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()

        Map<String, Object> objectMapWithParams = [random: "abc", id: "123", max: "10", offset: "1"]
        Map<String, Object> objectMapNoId = [:]

        when:
        Action action = Action.create
        ApiController apiController = new ApiController()
        URI uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapNoId)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action create and object map with params including id"
        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapWithParams.clone())

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"


        // Action.read
        when: "getURI with action read and object map no id"
        action = Action.read


        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapNoId)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action read and object map with params including id"


        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapWithParams.clone())

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?random=abc&max=10&offset=1&Format=JSON"


        // Action.update
        when: "getURI with action update and object map no id"
        action = Action.update

        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapNoId)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action update and object map with params including id"

        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapWithParams.clone())

        then: "we get /mock with id"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?Format=JSON"


        // Action.delete
        when: "getURI with action delete and object map no id"
        action = Action.delete

        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapNoId)

        then: "we get /mock with no params"
        uri.toASCIIString() ==  "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action delete and object map with params including id"

        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapWithParams.clone())

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?random=abc&max=10&offset=1&Format=JSON"


        // Action.list
        when: "getURI with action list and object map no id"
        action = Action.list

        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapNoId)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action list and object map with params including id"


        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapWithParams.clone())

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?random=abc&id=123&max=10&offset=1&Format=JSON"

        when: "invalid uri"
        apiController.getURI(action, "%", [a:"%"] )

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

    }

    def "test getURI with MockComplexObject throws IllegalStateException" () {
        given:
        MockComplexObject mockComplexObject = new MockComplexObject()
        Action action
        Map<String, Object> objectMapWithParams = [random: "abc", id: "123", max: "10", offset: "1"]

        ApiController apiController = new ApiController()

        // Action.create
        when: "getURI with action create and object map no id"
        action = Action.create
        URI uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapWithParams)

        then:
        thrown(IllegalStateException)

    }

    def "test getURI with MockComplexObject" () {
        given:
        MockComplexObject mockComplexObject = new MockComplexObject()
        Action action
        Map<String, Object> objectMapWithParams = [random: "abc", id: "123", max: "10", offset: "1", 'mock-type-id': "111"]
        Map<String, Object> objectMapNoId = ['mock-type-id': "111"]


        ApiController apiController = new ApiController()

        // Action.create
        when: "getURI with action create and object map no id"
        action = Action.create
        URI uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapNoId.clone())

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON";

        when: "getURI with action create and object map with params including id"
        uri = apiController.getURI( action, mockComplexObject.getResourcePath(), objectMapWithParams.clone())

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"


        // Action.read
        when: "getURI with action read and object map no id"
        action = Action.read

        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapNoId.clone())

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"

        when: "getURI with action read and object map with params including id"
        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapWithParams.clone())

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex/123?random=abc&max=10&offset=1&Format=JSON"


        // Action.update
        when: "getURI with action update and object map no id"
        action = Action.update
        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapNoId.clone())

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"

        when: "getURI with action update and object map with params including id"
        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapWithParams.clone())

        then: "we get /mock with id"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex/123?Format=JSON"


        // Action.delete
        when: "getURI with action delete and object map no id"
        action = Action.delete
        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapNoId.clone())

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"

        when: "getURI with action delete and object map with params including id"
        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapWithParams.clone())

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex/123?random=abc&max=10&offset=1&Format=JSON"


        // Action.list
        when: "getURI with action list and object map no id"
        action = Action.list
        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapNoId.clone())

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"

        when: "getURI with action list and object map with params including id"
        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapWithParams.clone())

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?random=abc&id=123&max=10&offset=1&Format=JSON"

        when: "invalid uri"
        apiController.getURI(action, "%", [a:"%"] )

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

    }

    def "test getRequest null authentication" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        ApiConfig.authentication = null
        ApiController apiController = new ApiController()

        when:
        apiController.getRequest(null, null, null, null, null, null, null)

        then:
        thrown(MessageSignerException)

    }

    def "test getRequest authentication" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        Map<String, Object> objectMap = [a: 1]
        Map<String, Object> headerMap = [:]
        Action action = Action.create

        MockAuthentication providedAuthentication = new MockAuthentication()

        ApiController apiController = new ApiController()
        URI uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMap)

        when: "using global authentication"
        apiController.getRequest(null, uri, action, mockBaseObject.getApiVersion(), objectMap, headerMap, null)

        then: "global authentication is called"
        mockAuthentication.called == true
        providedAuthentication.called == false

        when: "using provided authentication"
        providedAuthentication = new MockAuthentication()
        mockAuthentication = new MockAuthentication()
        ApiConfig.authentication = mockAuthentication
        apiController.getRequest(providedAuthentication, uri, action, mockBaseObject.getApiVersion(),objectMap, headerMap, null)

        then: "provided authentication is called"
        mockAuthentication.called == false
        providedAuthentication.called == true
    }

    def "test getRequest create | update" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        Action action = Action.create
        Map<String, Object> objectMap = [a: "a", b: "b", id: 1, 'x-sdk-mock-header': "x-sdk-mock-value"]
        List<String> headersList = ['x-sdk-mock-header' ]
        Map<String, Object> headerMap = ApiController.subMap(objectMap, headersList);


        ApiController apiController = new ApiController();
        URI uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMap)

        when: "getRequest for create"
        HttpRequestBase httpRequestBase = apiController.getRequest(null, uri, action, mockBaseObject.getApiVersion(), objectMap, headerMap, null)

        then:
        httpRequestBase instanceof HttpPost
        ((HttpPost) httpRequestBase).method == "POST"
        ((HttpPost) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        ((HttpPost) httpRequestBase).getEntity().content.text == JSONValue.toJSONString(objectMap.subMap(['a','b','id']))
        assertHeaders(httpRequestBase,objectMap.subMap(['x-sdk-mock-header']));

        when: "getRequest for update"
        action = Action.update
        httpRequestBase = apiController.getRequest(null, uri, action, mockBaseObject.getApiVersion(), objectMap, headerMap, null)

        then:
        httpRequestBase instanceof HttpPut
        ((HttpPut) httpRequestBase).method == "PUT"
        ((HttpPut) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        ((HttpPut) httpRequestBase).getEntity().content.text == JSONValue.toJSONString(objectMap.subMap(['a','b','id']))
        assertHeaders(httpRequestBase,objectMap.subMap(['x-sdk-mock-header']));
    }

    def "test getRequest read | list | delete" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        Action action = Action.read
        Map<String, Object> objectMap = [a: "a", b: "b", id: 1, 'x-sdk-mock-header': "x-sdk-mock-value"]
        List<String> headersList = ['x-sdk-mock-header' ]
        Map<String, Object> headerMap = ApiController.subMap(objectMap, headersList);

        ApiController apiController = new ApiController()
        URI uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMap.clone())

        when: "getRequest for read"
        HttpRequestBase httpRequestBase = apiController.getRequest(null, uri, action, mockBaseObject.getApiVersion(), objectMap, headerMap, null)

        then:
        httpRequestBase instanceof HttpGet
        ((HttpGet) httpRequestBase).method == "GET"
        ((HttpGet) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        assertHeaders(httpRequestBase,objectMap.subMap(['x-sdk-mock-header']));

        when: "getRequest for list"
        action = Action.list
        httpRequestBase = apiController.getRequest(null, uri, action, mockBaseObject.getApiVersion(), objectMap, headerMap, null)

        then:
        httpRequestBase instanceof HttpGet
        ((HttpGet) httpRequestBase).method == "GET"
        ((HttpGet) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        assertHeaders(httpRequestBase,objectMap.subMap(['x-sdk-mock-header']));

        when: "getRequest for delete"
        action = Action.delete
        httpRequestBase = apiController.getRequest(null, uri, action, mockBaseObject.getApiVersion(), objectMap, headerMap, null)

        then:
        httpRequestBase instanceof HttpDelete
        ((HttpDelete) httpRequestBase).method == "DELETE"
        ((HttpDelete) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        assertHeaders(httpRequestBase,objectMap.subMap(['x-sdk-mock-header']));
    }

    def "test getRequest null USER_AGENT" () {
        given:
        ApiController.USER_AGENT = "mock"

        MockBaseObject mockBaseObject = new MockBaseObject()

        Action action = Action.read
        Map<String, Object> objectMap = [a: "a", b: "b", id: 1, 'x-sdk-mock-header': "x-sdk-mock-value"]
        List<String> headersList = ['x-sdk-mock-header' ]
        Map<String, Object> headerMap = ApiController.subMap(objectMap, headersList);

        ApiController apiController = new ApiController()
        URI uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMap)

        when:
        HttpRequestBase httpRequestBase = apiController.getRequest(null, uri, action, mockBaseObject.getApiVersion(), objectMap, headerMap, null)

        then:
        httpRequestBase.getFirstHeader("User-Agent").value == "Java-SDK/0.0.1 mock" as String

        cleanup:
        ApiController.USER_AGENT = null
    }

    private void assertHeaders(HttpRequestBase httpRequestBase, Map<String, String> customHeaders) {
        assert httpRequestBase.getFirstHeader("Accept").value == "application/json"
        assert httpRequestBase.getFirstHeader("User-Agent").value == "Java-SDK/0.0.1" as String
        assert httpRequestBase.getFirstHeader("MockAuthentication").value == "MockValue"

        customHeaders.each {
            assert httpRequestBase.getFirstHeader(it.key).value == it.value
        }
    }

    def "test execute IllegalStateException when urlEncode throws exception" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        String  action = "read"
        String type = mockBaseObject.getResourcePath()
        Map<String, Object> objectMap = [id: 1]

        ApiController apiController = Spy(ApiController, constructorArgs: []) {
            urlEncode(_) >> { throw new UnsupportedEncodingException("mock encoding") }
        }

        when:
        apiController.execute(null, Action.valueOf(action), type, mockBaseObject.getApiVersion() , [], objectMap)

        then:
        thrown(IllegalStateException)
    }

    @Unroll
    def "test execute mockHttpResponse #mockHttpResponse executeResult #executeResult" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        String  action = "create"
        String type = mockBaseObject.getResourcePath()
        Map<String, Object> objectMap = [a: 1]
        MockHttpClient mockHttpClient = new MockHttpClient(mockHttpResponse)

        ApiController apiController = Spy(ApiController, constructorArgs: []) {
            createHttpClient() >> mockHttpClient
        }

        when:
        Map<String, Object> response = apiController.execute(null, Action.valueOf(action), type, mockBaseObject.getApiVersion(), [], objectMap)

        then:
        response == executeResult
        mockHttpClient.closed == true

        where:
        mockHttpResponse | executeResult
        new MockHttpResponse(200, MockHttpResponse.defaultJsonResponse) | MockHttpResponse.defaultJsonResponse
        new MockHttpResponse(201, MockHttpResponse.defaultJsonResponse) | MockHttpResponse.defaultJsonResponse
        new MockHttpResponse(204, null) | null
    }

    @Unroll
    def "test execute with list mockHttpResponse #mockHttpResponse executeResult #executeResult" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        String  action = "list"
        String type = mockBaseObject.getResourcePath()
        Map<String, Object> objectMap = [a: 1]
        MockHttpClient mockHttpClient = new MockHttpClient(mockHttpResponse)

        ApiController apiController = Spy(ApiController, constructorArgs: []) {
            createHttpClient() >> mockHttpClient
        }

        when:
        Map<String, Object> response = apiController.execute(null, Action.valueOf(action), type, mockBaseObject.getApiVersion(), [], objectMap)

        then:
        response == executeResult
        mockHttpClient.closed == true

        where:
        mockHttpResponse | executeResult
        new MockHttpResponse(200, [Countries: [Country: [MockHttpResponse.defaultJsonResponse, MockHttpResponse.defaultJsonResponse]]]) | [list: [MockHttpResponse.defaultJsonResponse, MockHttpResponse.defaultJsonResponse]]
        new MockHttpResponse(200, [Countries: [Country: []]]) | [list: []]
        new MockHttpResponse(200, [Countries: ""]) | [list: []]
    }

    @Unroll
    def "test execute exceptions mockHttpResponse #mockHttpResponse thrownEx #thrownEx" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        String  action = "create"
        String type = mockBaseObject.getResourcePath()
        Map<String, Object> objectMap = [a: 1]
        MockHttpClient mockHttpClient = new MockHttpClient(mockHttpResponse)

        ApiController apiController = Spy(ApiController, constructorArgs: []) {
            createHttpClient() >> mockHttpClient
        }

        when:
        apiController.execute(null, Action.valueOf(action), type, mockBaseObject.getApiVersion(), [], objectMap)

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
        String  action = "delete"
        String type = mockBaseObject.getResourcePath()
        Map<String, Object> objectMap = [a: 1]

        MockHttpResponse mockHttpResponse = new MockHttpResponse(200, MockHttpResponse.defaultJsonResponse)
        mockHttpResponse.contentType = "$ContentType.APPLICATION_JSON.mimeType; $ContentType.APPLICATION_XML.mimeType"
        MockHttpClient mockHttpClient = new MockHttpClient(mockHttpResponse)

        ApiController apiController = Spy(ApiController, constructorArgs: []) {
            createHttpClient() >> mockHttpClient
        }

        when:
        def response = apiController.execute(null, Action.valueOf(action), type, mockBaseObject.getApiVersion(), [], objectMap)

        then:
        response

        when:
        response = apiController.execute(null, Action.valueOf(action), type, mockBaseObject.getApiVersion(), [], null)

        then:
        response

        when:
        mockHttpResponse.contentType = "$ContentType.APPLICATION_XML.mimeType; $ContentType.APPLICATION_JSON.mimeType"
        apiController.execute(null, Action.valueOf(action), type, mockBaseObject.getApiVersion(), [], objectMap)

        then:
        thrown(ApiCommunicationException)

        when:
        mockHttpResponse.contentType = null
        apiController.execute(null, Action.valueOf(action), type, mockBaseObject.getApiVersion(), [], objectMap)

        then:
        thrown(IllegalStateException)

    }

}
