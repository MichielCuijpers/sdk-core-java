package com.mastercard.api.core
import com.mastercard.api.core.exception.*
import com.mastercard.api.core.functional.model.ResourceConfig
import com.mastercard.api.core.mocks.*
import com.mastercard.api.core.model.Action
import com.mastercard.api.core.model.Environment
import com.mastercard.api.core.model.OperationConfig
import com.mastercard.api.core.model.OperationMetadata
import com.mastercard.api.core.model.RequestMap
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




    @Unroll
    def "test getUri: Using SubDomain: Environment: #envrironment "() {
        given:
        ResourceConfig config = ResourceConfig.getInstance();
        config.clearOverride();
        OperationConfig operationConfig = new OperationConfig("/mdes/digitization/{:env}/1/0/getToken", Action.create, [], [])


        when:
        ApiConfig.registerResourceConfig(config);
        ApiConfig.setEnvironment(envrironment)
        ApiController controller = new ApiController()
        OperationMetadata operationMetadata = new OperationMetadata("0.0.1", config.getHost(), config.getContext());
        URI uri = controller.getURI(operationConfig, operationMetadata, new RequestMap());

        then:
        uri.toURL().toString() == result

        cleanup:
        ApiConfig.setEnvironment(Environment.SANDBOX)

        where:
        envrironment                 | result
        Environment.PRODUCTION       | "https://api.mastercard.com/mdes/digitization/1/0/getToken?Format=JSON"
        Environment.SANDBOX          | "https://sandbox.api.mastercard.com/mdes/digitization/1/0/getToken?Format=JSON"
        Environment.STAGE            | "https://stage.api.mastercard.com/mdes/digitization/1/0/getToken?Format=JSON"
        Environment.ITF              | "https://sandbox.api.mastercard.com/mdes/digitization/itf/1/0/getToken?Format=JSON"
        Environment.MTF              | "https://sandbox.api.mastercard.com/mdes/digitization/mtf/1/0/getToken?Format=JSON"


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
        MockBaseObject mockBaseObject

        Map<String, Object> objectMapWithParams = [random: "abc", id: "123", max: "10", offset: "1"]
        Map<String, Object> objectMapNoId = [:]

        ApiController apiController = new ApiController()

        // Action.create
        when: "getURI with action create and object map with no params"
        mockBaseObject = new MockBaseObject(null, Action.create, objectMapNoId, [])
        URI uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action create, object map no id and url has id"
        mockBaseObject = new MockBaseObject("/{id}", Action.create, objectMapNoId, [])
        apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

        when: "getURI with action create and object map with params and path has id"
        mockBaseObject = new MockBaseObject("/{id}", Action.create, objectMapWithParams.clone(), [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get /mock with id but no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?Format=JSON"

        when: "getURI with action create and object map with params including id and path has no id"
        mockBaseObject = new MockBaseObject(null, Action.create, objectMapWithParams.clone(), [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get /mock"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action create, object map with params (including a query parameter) and path has id"
        Map<String, Object> tmpObjectMap = objectMapWithParams.clone()
        tmpObjectMap.put("query-param", "query-param-mock-value")
        mockBaseObject = new MockBaseObject("/{id}", Action.create, tmpObjectMap, [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then:
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?query-param=query-param-mock-value&Format=JSON"
        mockBaseObject.containsKey('query-param') == false


        // Action.read
        when: "getURI with action read and object map no with no params"
        mockBaseObject = new MockBaseObject(null, Action.read, objectMapNoId, [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action read, object map no id and url has id"
        mockBaseObject = new MockBaseObject("/{id}", Action.read, objectMapNoId, [])
        apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

        when: "getURI with action read and object map with params and path has id"
        mockBaseObject = new MockBaseObject("/{id}", Action.read, objectMapWithParams.clone(), [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get /mock with id and all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?random=abc&max=10&offset=1&Format=JSON"

        when: "getURI with action read and object map with params including id and path has no id"
        mockBaseObject = new MockBaseObject(null, Action.read, objectMapWithParams.clone(), [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get /mock and all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?random=abc&id=123&max=10&offset=1&Format=JSON"


        // Action.update
        when: "getURI with action update and object map with no params"
        mockBaseObject = new MockBaseObject(null, Action.update, objectMapNoId, [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action read, object map no id and url has id"
        mockBaseObject = new MockBaseObject("/{id}", Action.update, objectMapNoId, [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

        when: "getURI with action update and object map with params and path has id"
        mockBaseObject = new MockBaseObject("/{id}", Action.update, objectMapWithParams.clone(), [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get /mock with id and all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?Format=JSON"

        when: "getURI with action update and object map with params including id and path has no id"
        mockBaseObject = new MockBaseObject(null, Action.update, objectMapWithParams.clone(), [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get /mock"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action create, object map with params (including a query parameter) and path has id"
        tmpObjectMap = objectMapWithParams.clone()
        tmpObjectMap.put("query-param", "query-param-mock-value")
        mockBaseObject = new MockBaseObject("/{id}", Action.update, tmpObjectMap, [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then:
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?query-param=query-param-mock-value&Format=JSON"
        mockBaseObject.containsKey('query-param') == false

        // Action.delete
        when: "getURI with action delete and object map with no params"
        mockBaseObject = new MockBaseObject(null, Action.delete, objectMapNoId, [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action delete, object map no id and url has id"
        mockBaseObject = new MockBaseObject("/{id}", Action.delete, objectMapNoId, [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

        when: "getURI with action delete and object map with params including id"
        mockBaseObject = new MockBaseObject("/{id}", Action.delete, objectMapWithParams.clone(), [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?random=abc&max=10&offset=1&Format=JSON"

        when: "getURI with action delete and object map with params including id and path has no id"
        mockBaseObject = new MockBaseObject(null, Action.delete, objectMapWithParams.clone(), [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get /mock"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?random=abc&id=123&max=10&offset=1&Format=JSON"


        // Action.list
        when: "getURI with action list and object map with no params"
        mockBaseObject = new MockBaseObject(null, Action.list, objectMapNoId, [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?Format=JSON"

        when: "getURI with action list, object map no id and url has id"
        mockBaseObject = new MockBaseObject("/{id}", Action.list,  objectMapNoId, [])
        apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

        when: "getURI with action list and object map with params including id"
        mockBaseObject = new MockBaseObject("/{id}", Action.list,  objectMapWithParams.clone(), [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject/123?random=abc&max=10&offset=1&Format=JSON"

        when: "getURI with action list and object map with params including id and path has no id"
        mockBaseObject = new MockBaseObject(null, Action.list,  objectMapWithParams.clone(), [])
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get /mock and all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/MockObject?random=abc&id=123&max=10&offset=1&Format=JSON"


        when: "invalid uri"
        mockBaseObject = new MockBaseObject(null, Action.list, [a:"%"], [])
        mockBaseObject.resourcePath = "%"
        apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

        when: "specified an override"
        mockBaseObject = new MockBaseObject(null, Action.list, [:], [])
        mockBaseObject.host = "http://localhost:8080"
        uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "we get host override from the SDKs"
        uri.toASCIIString() == "http://localhost:8080/mock/MockObject?Format=JSON"
    }

    def "test getURI with MockComplexObject" () {
        given:
        Action action
        Map<String, Object> objectMapWithParams = [random: "abc", id: "123", max: "10", offset: "1", 'mock-type-id': "111"]
        Map<String, Object> objectMapNoId = ['mock-type-id': "111"]


        ApiController apiController = new ApiController()

        // Action.create
        when: "getURI with action create and object map no id"
        MockComplexObject input = new MockComplexObject(Action.create, objectMapNoId.clone());
        URI uri = apiController.getURI(input.getOperationConfig(""), input.getOperationMetadata(), input)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON";


        when: "getURI with action create and object map with params including id"
        input = new MockComplexObject(Action.create, objectMapWithParams.clone());
        uri = apiController.getURI(input.getOperationConfig(""), input.getOperationMetadata(), input)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"


        // Action.read
        when: "getURI with action read and object map no id"
        input = new MockComplexObject(Action.read, objectMapNoId.clone());
        uri = apiController.getURI(input.getOperationConfig(""), input.getOperationMetadata(), input)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"


        when: "getURI with action read and object map with params including id"
        input = new MockComplexObject(Action.read, objectMapWithParams.clone());
        uri = apiController.getURI(input.getOperationConfig(""), input.getOperationMetadata(), input)

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?random=abc&id=123&max=10&offset=1&Format=JSON"


        // Action.update
        when: "getURI with action update and object map no id"
        input = new MockComplexObject(Action.update, objectMapNoId.clone());
        uri = apiController.getURI(input.getOperationConfig(""), input.getOperationMetadata(), input)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"


        when: "getURI with action update and object map with params including id"
        input = new MockComplexObject(Action.update, objectMapWithParams.clone());
        uri = apiController.getURI(input.getOperationConfig(""), input.getOperationMetadata(), input)

        then: "we get /mock with id"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"


        // Action.delete
        when: "getURI with action delete and object map no id"
        action = Action.delete
        input = new MockComplexObject(Action.delete, objectMapNoId.clone());
        uri = apiController.getURI(input.getOperationConfig(""), input.getOperationMetadata(), input)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"


        when: "getURI with action delete and object map with params including id"
        input = new MockComplexObject(Action.delete, objectMapWithParams.clone());
        uri = apiController.getURI(input.getOperationConfig(""), input.getOperationMetadata(), input)

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?random=abc&id=123&max=10&offset=1&Format=JSON"


        // Action.list
        when: "getURI with action list and object map no id"
        input = new MockComplexObject(Action.list, objectMapNoId.clone());
        uri = apiController.getURI(input.getOperationConfig(""), input.getOperationMetadata(), input)

        then: "we get /mock with no params"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?Format=JSON"


        when: "getURI with action list and object map with params including id"
        input = new MockComplexObject(Action.list, objectMapWithParams.clone());
        uri = apiController.getURI(input.getOperationConfig(""), input.getOperationMetadata(), input)

        then: "we get /mock with all params appended"
        uri.toASCIIString() == "https://sandbox.api.mastercard.com/mock/v2/111/MockObjectComplex?random=abc&id=123&max=10&offset=1&Format=JSON"


        when: "invalid uri"
        MockComplexObject mockComplexObject = new MockComplexObject("%", Action.list, [a:"%"], [])
        apiController.getURI(mockComplexObject.getOperationConfig(""), mockComplexObject.getOperationMetadata(), mockComplexObject)

        then: "IllegalStateException is thrown"
        thrown(IllegalStateException)

        when: "getURI with and object map no {mock-type-id}"
        Map<String, Object> tmpObjectMap = objectMapWithParams.clone()
        tmpObjectMap.remove("mock-type-id")
        mockComplexObject = new MockComplexObject("%", Action.list, tmpObjectMap, [])
        apiController.getURI(mockComplexObject.getOperationConfig(""), mockComplexObject.getOperationMetadata(), mockComplexObject)

        then:
        thrown(IllegalStateException)

    }

    def "test getRequest null authentication" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject()
        ApiConfig.authentication = null
        ApiController apiController = new ApiController()

        when:
        apiController.getRequest(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then:
        thrown(MessageSignerException)

    }

    def "test getRequest authentication" () {
        given:
        Map<String, Object> objectMap = [a: 1]
        MockBaseObject mockBaseObject = new MockBaseObject(objectMap)
        Action action = Action.create

        MockAuthentication providedAuthentication = new MockAuthentication()

        ApiController apiController = new ApiController()
        URI uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        when: "using global authentication"
        apiController.getRequest(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "global authentication is called"
        mockAuthentication.called == true
        providedAuthentication.called == false

        when: "using provided authentication"
        providedAuthentication = new MockAuthentication()
        mockAuthentication = new MockAuthentication()
        ApiConfig.authentication = mockAuthentication
        apiController.getRequest(providedAuthentication, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then: "provided authentication is called"
        mockAuthentication.called == false
        providedAuthentication.called == true
    }

    def "test getRequest create | update" () {
        given:
        Map<String, Object> objectMap = [a: "a", b: "b", id: 1, 'x-sdk-mock-header': 'x-sdk-mock-value']
        Map<String, Object> headerMap = ['x-sdk-mock-header': 'x-sdk-mock-value']
        MockBaseObject mockBaseObject = new MockBaseObject(Action.create, objectMap)

        ApiController apiController = new ApiController();
        URI uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        when: "getRequest for create"
        HttpRequestBase httpRequestBase = apiController.getRequest(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then:
        httpRequestBase instanceof HttpPost
        ((HttpPost) httpRequestBase).method == "POST"
        ((HttpPost) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        ((HttpPost) httpRequestBase).getEntity().content.text == JSONValue.toJSONString(objectMap.subMap(['a','b','id']))
        assertHeaders(httpRequestBase, headerMap);
        mockBaseObject.get("x-sdk-mock-header") == null

        when: "getRequest for update"
        mockBaseObject = new MockBaseObject(Action.update, objectMap)
        httpRequestBase = apiController.getRequest(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then:
        httpRequestBase instanceof HttpPut
        ((HttpPut) httpRequestBase).method == "PUT"
        ((HttpPut) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        ((HttpPut) httpRequestBase).getEntity().content.text == JSONValue.toJSONString(objectMap.subMap(['a','b','id']))
        assertHeaders(httpRequestBase, headerMap);
        mockBaseObject.get("x-sdk-mock-header") == null
    }

    def "test getRequest read | list | delete" () {
        given:
        Map<String, Object> objectMap = [a: "a", b: "b", id: 1, 'x-sdk-mock-header': "x-sdk-mock-value"]
        Map<String, Object> headerMap = ['x-sdk-mock-header': "x-sdk-mock-value"]
        MockBaseObject mockBaseObject = new MockBaseObject(Action.read, objectMap)

        ApiController apiController = new ApiController()


        MockBaseObject clonedObject = (MockBaseObject) mockBaseObject.clone()
        ApiController.subMap(clonedObject, clonedObject.getOperationConfig().getHeaderParams());
        URI uri = apiController.getURI(clonedObject.getOperationConfig(), clonedObject.getOperationMetadata(), clonedObject)

        when: "getRequest for read"
        HttpRequestBase httpRequestBase = apiController.getRequest(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then:
        httpRequestBase instanceof HttpGet
        ((HttpGet) httpRequestBase).method == "GET"
        ((HttpGet) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        assertHeaders(httpRequestBase, headerMap);

        when: "getRequest for list"
        mockBaseObject = new MockBaseObject(Action.list, objectMap)
        httpRequestBase = apiController.getRequest(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then:
        httpRequestBase instanceof HttpGet
        ((HttpGet) httpRequestBase).method == "GET"
        ((HttpGet) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        assertHeaders(httpRequestBase, headerMap);

        when: "getRequest for delete"
        mockBaseObject = new MockBaseObject(Action.delete, objectMap)
        httpRequestBase = apiController.getRequest(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then:
        httpRequestBase instanceof HttpDelete
        ((HttpDelete) httpRequestBase).method == "DELETE"
        ((HttpDelete) httpRequestBase).getURI().toASCIIString() == uri.toASCIIString()
        assertHeaders(httpRequestBase, headerMap);
    }

    def "test getRequest with USER_AGENT" () {
        given:
        ApiController.USER_AGENT = "mock"

        MockBaseObject mockBaseObject = new MockBaseObject()

        Action action = Action.read

        ApiController apiController = new ApiController()
        URI uri = apiController.getURI(mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        when:
        HttpRequestBase httpRequestBase = apiController.getRequest(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

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
        MockBaseObject mockBaseObject = new MockBaseObject(Action.read, [id: 1])

        ApiController apiController = Spy(ApiController, constructorArgs: []) {
            urlEncode(_) >> { throw new UnsupportedEncodingException("mock encoding") }
        }

        when:
        apiController.execute(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then:
        thrown(IllegalStateException)
    }

    @Unroll
    def "test execute mockHttpResponse #mockHttpResponse executeResult #executeResult" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject(Action.create, [id: 1])

        MockHttpClient mockHttpClient = new MockHttpClient(mockHttpResponse)

        ApiController apiController = Spy(ApiController, constructorArgs: []) {
            createHttpClient() >> mockHttpClient
        }

        when:
        Map<String, Object> response = apiController.execute(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then:
        response == executeResult
        mockHttpClient.closed == true

        where:
        mockHttpResponse | executeResult
        new MockHttpResponse(200, MockHttpResponse.defaultJsonResponse) | MockHttpResponse.defaultJsonResponse
        new MockHttpResponse(201, MockHttpResponse.defaultJsonResponse) | MockHttpResponse.defaultJsonResponse
        new MockHttpResponse(200, null) | null
        new MockHttpResponse(204, null) | null
    }

    @Unroll
    def "test execute with list mockHttpResponse #mockHttpResponse executeResult #executeResult" () {
        given:
        MockBaseObject mockBaseObject = new MockBaseObject(Action.list, [id: 1])
        MockHttpClient mockHttpClient = new MockHttpClient(mockHttpResponse)

        ApiController apiController = Spy(ApiController, constructorArgs: []) {
            createHttpClient() >> mockHttpClient
        }

        when:
        Map<String, Object> response = apiController.execute(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

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
        MockBaseObject mockBaseObject = new MockBaseObject(Action.create, [id: 1])
        MockHttpClient mockHttpClient = new MockHttpClient(mockHttpResponse)

        ApiController apiController = Spy(ApiController, constructorArgs: []) {
            createHttpClient() >> mockHttpClient
        }

        when:
        apiController.execute(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then:
        thrown(thrownEx)
        mockHttpClient.closed == true

        where:
        mockHttpResponse                            | thrownEx
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
        MockBaseObject mockBaseObject = new MockBaseObject(Action.delete, [id: 1])

        MockHttpResponse mockHttpResponse = new MockHttpResponse(200, MockHttpResponse.defaultJsonResponse)
        mockHttpResponse.contentType = "$ContentType.APPLICATION_JSON.mimeType; $ContentType.APPLICATION_XML.mimeType"
        MockHttpClient mockHttpClient = new MockHttpClient(mockHttpResponse)

        ApiController apiController = Spy(ApiController, constructorArgs: []) {
            createHttpClient() >> mockHttpClient
        }

        when:
        def response = apiController.execute(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then:
        response

        when:
        response = apiController.execute(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then:
        response

        when:
        mockHttpResponse.contentType = "$ContentType.APPLICATION_XML.mimeType; $ContentType.APPLICATION_JSON.mimeType"
        apiController.execute(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

        then:
        thrown(ApiCommunicationException)

        when:
        mockHttpResponse.contentType = null
        apiController.execute(null, mockBaseObject.getOperationConfig(), mockBaseObject.getOperationMetadata(), mockBaseObject)

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
