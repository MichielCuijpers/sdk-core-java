package com.mastercard.api.core.security.oauth

import com.mastercard.api.core.model.HttpMethod
import org.apache.http.entity.ContentType;
import spock.lang.Specification

import java.nio.charset.Charset

public class OAuthRequestSpec extends Specification {

    def 'test setters and getters' () {
        given:
        OAuthRequest oAuthRequest = new OAuthRequest()
        Map<String, String> formParams = [a: 1, b: "2", c: true]

        when:
        oAuthRequest.setHeader("MockName", "MockValue")
        oAuthRequest.setBody("body")
        oAuthRequest.setContentType(ContentType.APPLICATION_JSON)
        oAuthRequest.setFormParams(formParams)
        oAuthRequest.setMethod(HttpMethod.POST)
        oAuthRequest.setRequestUrl("https://sandbox.api.mastercard.com/mock")

        then:
        oAuthRequest.getHeader("MockName") == "MockValue"
        oAuthRequest.getAllHeaders() == ["MockName": "MockValue"]
        oAuthRequest.getBody() == "body"
        oAuthRequest.getContentType() == "application/json"
        oAuthRequest.getFormParams() == formParams
        oAuthRequest.getMethod() == "POST"
        oAuthRequest.getOauthBodyHash() == "Agg/RXngimEkJcDBoX7ket14O5Q="
        oAuthRequest.getRequestUrl() == "https://sandbox.api.mastercard.com/mock"
        oAuthRequest.unwrap() == oAuthRequest

        when:
        oAuthRequest.addFormParam("add", "value")

        then:
        oAuthRequest.getFormParams() == [a: 1, b: "2", c: true, add: "value"]
    }

    def 'test getMessagePayload' () {
        given:
        OAuthRequest oAuthRequest = new OAuthRequest()
        oAuthRequest.addFormParam("a", "1")
        oAuthRequest.addFormParam("b", "2")
        String s = "a&1b&2"

        when:
        InputStream is = oAuthRequest.getMessagePayload()

        // Convert to string to check
        int n = is.available();
        byte[] bytes = new byte[n];
        is.read(bytes, 0, n);
        String responseStr = new String(bytes, Charset.forName("UTF-8")); // Or any encoding.

        then:
        responseStr == s
    }

}
