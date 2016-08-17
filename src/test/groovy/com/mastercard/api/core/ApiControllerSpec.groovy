package com.mastercard.api.core
import com.mastercard.api.core.exception.*
import com.mastercard.api.core.mocks.*
import com.mastercard.api.core.model.Action
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpResponseException
import org.apache.http.client.methods.*
import org.apache.http.entity.ContentType
import org.apache.http.impl.client.CloseableHttpClient
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

        ApiController apiController = new ApiController()

        // Action.create
        when: "getURI with action create and object map with no params"
        Action action = Action.create
        URI uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapNoId, [], null)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action create, object map no id and url has id"
        apiController.getURI(action, "${mockBaseObject.getResourcePath()}/{id}", objectMapNoId, [], null)

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

        when: "getURI with action create and object map with params and path has id"
        uri = apiController.getURI(action, "${mockBaseObject.getResourcePath()}/{id}", objectMapWithParams.clone(), [], null)

        then: "we get /mock with id but no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?Format=JSON"

        when: "getURI with action create and object map with params including id and path has no id"
        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapWithParams.clone(), [], null)

        then: "we get /mock"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action create, object map with params (including a query parameter) and path has id"
        Map<String, Object> tmpObjectMap = objectMapWithParams.clone()
        tmpObjectMap.put("query-param", "query-param-mock-value")
        uri = apiController.getURI(action, "${mockBaseObject.getResourcePath()}/{id}", tmpObjectMap, mockBaseObject.getQueryParams(null), null)

        then:
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?query-param=query-param-mock-value&Format=JSON"
        tmpObjectMap.containsKey('query-param') == false


        // Action.read
        when: "getURI with action read and object map no with no params"
        action = Action.read
        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapNoId, [], null)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action read, object map no id and url has id"
        apiController.getURI(action, "${mockBaseObject.getResourcePath()}/{id}", objectMapNoId, [], null)

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

        when: "getURI with action read and object map with params and path has id"
        uri = apiController.getURI(action, "${mockBaseObject.getResourcePath()}/{id}", objectMapWithParams.clone(), [], null)

        then: "we get /mock with id and all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?random=abc&max=10&offset=1&Format=JSON"

        when: "getURI with action read and object map with params including id and path has no id"
        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapWithParams.clone(), [], null)

        then: "we get /mock and all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?random=abc&id=123&max=10&offset=1&Format=JSON"


        // Action.update
        when: "getURI with action update and object map with no params"
        action = Action.update
        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapNoId, [], null)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action read, object map no id and url has id"
        apiController.getURI(action, "${mockBaseObject.getResourcePath()}/{id}", objectMapNoId, [], null)

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

        when: "getURI with action update and object map with params and path has id"
        uri = apiController.getURI(action, "${mockBaseObject.getResourcePath()}/{id}", objectMapWithParams.clone(), [], null)

        then: "we get /mock with id and all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?Format=JSON"

        when: "getURI with action update and object map with params including id and path has no id"
        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapWithParams.clone(), [], null)

        then: "we get /mock"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action create, object map with params (including a query parameter) and path has id"
        tmpObjectMap = objectMapWithParams.clone()
        tmpObjectMap.put("query-param", "query-param-mock-value")
        uri = apiController.getURI(action, "${mockBaseObject.getResourcePath()}/{id}", tmpObjectMap, mockBaseObject.getQueryParams(null), null)

        then:
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?query-param=query-param-mock-value&Format=JSON"
        tmpObjectMap.containsKey('query-param') == false

        // Action.delete
        when: "getURI with action delete and object map with no params"
        action = Action.delete
        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapNoId, [], null)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action delete, object map no id and url has id"
        apiController.getURI(action, "${mockBaseObject.getResourcePath()}/{id}", objectMapNoId, [], null)

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

        when: "getURI with action delete and object map with params including id"
        uri = apiController.getURI(action, "${mockBaseObject.getResourcePath()}/{id}", objectMapWithParams.clone(), [], null)

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?random=abc&max=10&offset=1&Format=JSON"

        when: "getURI with action delete and object map with params including id and path has no id"
        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapWithParams.clone(), [], null)

        then: "we get /mock"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?random=abc&id=123&max=10&offset=1&Format=JSON"


        // Action.list
        when: "getURI with action list and object map with no params"
        action = Action.list
        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapNoId, [], null)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action list, object map no id and url has id"
        apiController.getURI(action, "${mockBaseObject.getResourcePath()}/{id}", objectMapNoId, [], null)

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

        when: "getURI with action list and object map with params including id"
        uri = apiController.getURI(action, "${mockBaseObject.getResourcePath()}/{id}", objectMapWithParams.clone(), [], null)

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?random=abc&max=10&offset=1&Format=JSON"

        when: "getURI with action list and object map with params including id and path has no id"
        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMapWithParams.clone(), [], null)

        then: "we get /mock and all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?random=abc&id=123&max=10&offset=1&Format=JSON"


        when: "invalid uri"
        apiController.getURI(action, "%", [a:"%"], [], null)

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

        when: "specified an override"
        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), [:], [], "http://localhost:8080")

        then: "we get host override from the SDKs"
        uri.toASCIIString() == "http://localhost:8080/mock/MockObject?Format=JSON"

        when: "specified an override from ApiConfig"
        ApiConfig.setHostOverride("http://localhost:9999")
        uri = apiController.getURI(action, mockBaseObject.getResourcePath(), [:], [], "http://localhost:8080")

        then: "we get host override from the SDKs"
        uri.toASCIIString() == "http://localhost:9999/mock/MockObject?Format=JSON"
        ApiConfig.setHostOverride(null)

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
        URI uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapNoId.clone(), [], null)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON";


        when: "getURI with action create and object map with params including id"
        uri = apiController.getURI( action, mockComplexObject.getResourcePath(), objectMapWithParams.clone(), [], null)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"


        // Action.read
        when: "getURI with action read and object map no id"
        action = Action.read
        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapNoId.clone(), [], null)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"


        when: "getURI with action read and object map with params including id"
        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapWithParams.clone(), [], null)

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?random=abc&id=123&max=10&offset=1&Format=JSON"


        // Action.update
        when: "getURI with action update and object map no id"
        action = Action.update
        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapNoId.clone(), [], null)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"


        when: "getURI with action update and object map with params including id"
        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapWithParams.clone(), [], null)

        then: "we get /mock with id"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"


        // Action.delete
        when: "getURI with action delete and object map no id"
        action = Action.delete
        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapNoId.clone(), [], null)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"


        when: "getURI with action delete and object map with params including id"
        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapWithParams.clone(), [], null)

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?random=abc&id=123&max=10&offset=1&Format=JSON"


        // Action.list
        when: "getURI with action list and object map no id"
        action = Action.list
        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapNoId.clone(), [], null)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"


        when: "getURI with action list and object map with params including id"
        uri = apiController.getURI(action, mockComplexObject.getResourcePath(), objectMapWithParams.clone(), [], null)

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?random=abc&id=123&max=10&offset=1&Format=JSON"


        when: "invalid uri"
        apiController.getURI(action, "%", [a:"%"], [], null)

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

        when: "getURI with and object map no {mock-type-id}"
        Map<String, Object> tmpObjectMap = objectMapWithParams.clone()
        tmpObjectMap.remove("mock-type-id")
        apiController.getURI(action, mockComplexObject.getResourcePath(), tmpObjectMap, [], null)

        then:
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
        URI uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMap, [], null)

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
        Map<String, Object> objectMap = [a: "a", b: "b", id: 1]
        Map<String, Object> headerMap = ['x-sdk-mock-header': 'x-sdk-mock-value']

        ApiController apiController = new ApiController();
        URI uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMap, [], null)

        when: "getRequest for create"
        HttpRequestBase httpRequestBase = apiController.getRequest(null, uri, action, mockBaseObject.getApiVersion(), objectMap, headerMap, null)

        then:
        httpRequestBase instanceof HttpPost
        ((HttpPost) httpRequestBase).method == "POST"
        ((HttpPost) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        ((HttpPost) httpRequestBase).getEntity().content.text == JSONValue.toJSONString(objectMap.subMap(['a','b','id']))
        assertHeaders(httpRequestBase, headerMap);

        when: "getRequest for update"
        action = Action.update
        httpRequestBase = apiController.getRequest(null, uri, action, mockBaseObject.getApiVersion(), objectMap, headerMap, null)

        then:
        httpRequestBase instanceof HttpPut
        ((HttpPut) httpRequestBase).method == "PUT"
        ((HttpPut) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        ((HttpPut) httpRequestBase).getEntity().content.text == JSONValue.toJSONString(objectMap.subMap(['a','b','id']))
        assertHeaders(httpRequestBase, headerMap);
    }

    def "test getRequest create | update with headers" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        Action action = Action.create
        Map<String, Object> objectMap = [a: "a", b: "b", id: 1, 'header-param': "header-param-mock-value"]
        Map<String, Object> headerMap = ApiController.subMap(objectMap, mockBaseObject.getHeaderParams(null));

        ApiController apiController = new ApiController();
        URI uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMap, [], null)

        when: "getRequest for create"
        HttpRequestBase httpRequestBase = apiController.getRequest(null, uri, action, mockBaseObject.getApiVersion(), objectMap, headerMap, null)

        then:
        httpRequestBase instanceof HttpPost
        ((HttpPost) httpRequestBase).method == "POST"
        ((HttpPost) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        ((HttpPost) httpRequestBase).getEntity().content.text == JSONValue.toJSONString(objectMap.subMap(['a','b','id']))
        assertHeaders(httpRequestBase, headerMap);
        objectMap.containsValue('header-param') == false

        when: "getRequest for update"
        action = Action.update
        httpRequestBase = apiController.getRequest(null, uri, action, mockBaseObject.getApiVersion(), objectMap, headerMap, null)

        then:
        httpRequestBase instanceof HttpPut
        ((HttpPut) httpRequestBase).method == "PUT"
        ((HttpPut) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        ((HttpPut) httpRequestBase).getEntity().content.text == JSONValue.toJSONString(objectMap.subMap(['a','b','id']))
        assertHeaders(httpRequestBase , headerMap);
        objectMap.containsValue('header-param') == false
    }

    def "test getRequest read | list | delete" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        Action action = Action.read
        Map<String, Object> objectMap = [a: "a", b: "b", id: 1, 'x-sdk-mock-header': "x-sdk-mock-value"]
        List<String> headersList = ['x-sdk-mock-header' ]
        Map<String, Object> headerMap = ApiController.subMap(objectMap, headersList);

        ApiController apiController = new ApiController()
        URI uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMap.clone(), [], null)

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
        URI uri = apiController.getURI(action, mockBaseObject.getResourcePath(), objectMap, [], null)

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
        mockBaseObject.putAll(objectMap);

        ApiController apiController = Spy(ApiController, constructorArgs: []) {
            urlEncode(_) >> { throw new UnsupportedEncodingException("mock encoding") }
        }

        when:
        apiController.execute(null, Action.valueOf(action), mockBaseObject)

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
        mockBaseObject.putAll(objectMap);
        MockHttpClient mockHttpClient = new MockHttpClient(mockHttpResponse)

        ApiController apiController = Spy(ApiController, constructorArgs: []) {
            createHttpClient() >> mockHttpClient
        }

        when:
        Map<String, Object> response = apiController.execute(null, Action.valueOf(action), mockBaseObject)

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
        mockBaseObject.putAll(objectMap);
        MockHttpClient mockHttpClient = new MockHttpClient(mockHttpResponse)

        ApiController apiController = Spy(ApiController, constructorArgs: []) {
            createHttpClient() >> mockHttpClient
        }

        when:
        Map<String, Object> response = apiController.execute(null, Action.valueOf(action), mockBaseObject)

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
        mockBaseObject.putAll(objectMap);
        MockHttpClient mockHttpClient = new MockHttpClient(mockHttpResponse)

        ApiController apiController = Spy(ApiController, constructorArgs: []) {
            createHttpClient() >> mockHttpClient
        }

        when:
        apiController.execute(null, Action.valueOf(action), mockBaseObject)

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
        mockBaseObject.putAll(objectMap);

        MockHttpResponse mockHttpResponse = new MockHttpResponse(200, MockHttpResponse.defaultJsonResponse)
        mockHttpResponse.contentType = "$ContentType.APPLICATION_JSON.mimeType; $ContentType.APPLICATION_XML.mimeType"
        MockHttpClient mockHttpClient = new MockHttpClient(mockHttpResponse)

        ApiController apiController = Spy(ApiController, constructorArgs: []) {
            createHttpClient() >> mockHttpClient
        }

        when:
        def response = apiController.execute(null, Action.valueOf(action), mockBaseObject)

        then:
        response

        when:
        response = apiController.execute(null, Action.valueOf(action), mockBaseObject)

        then:
        response

        when:
        mockHttpResponse.contentType = "$ContentType.APPLICATION_XML.mimeType; $ContentType.APPLICATION_JSON.mimeType"
        apiController.execute(null, Action.valueOf(action), mockBaseObject)

        then:
        thrown(ApiCommunicationException)

        when:
        mockHttpResponse.contentType = null
        apiController.execute(null, Action.valueOf(action), mockBaseObject)

        then:
        thrown(IllegalStateException)

    }

    @Unroll
    def "test getPathWithReplacedPath #path #expectedPath #params #expectedParams" () {
        given:
        ApiController apiController = new ApiController()

        when:
        String result = apiController.getPathWithReplacedPath(path, params)

        then:
        result == expectedPath
        params == expectedParams

        where:
        path | expectedPath | params | expectedParams
        "/api/{id}" | "/api/1" | [id: 1] | [:]
        "/api/consumer/{consumer-id}/account/{account-id}" | "/api/consumer/1/account/a" | ['consumer-id': 1, 'account-id': "a"] | [:]
    }

    def "test getPathWithReplacedPath throws exception" () {
        given:
        ApiController apiController = new ApiController()

        when:
        apiController.getPathWithReplacedPath("/api/{consumer-id}", [id: 1])

        then:
        thrown(IllegalStateException)
    }

    def "test createHttpClient supports TLSv1.2" () {
        given:
        ApiController apiController = new ApiController()

        when:
        CloseableHttpClient httpClient = apiController.createHttpClient()

        then:
        httpClient != null
    }

}
