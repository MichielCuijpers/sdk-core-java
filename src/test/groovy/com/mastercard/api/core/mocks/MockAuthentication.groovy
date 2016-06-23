package com.mastercard.api.core.mocks

import com.mastercard.api.core.model.HttpMethod
import com.mastercard.api.core.exception.MessageSignerException
import com.mastercard.api.core.security.Authentication
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.ContentType

/**
 * Created by eamondoyle on 17/02/2016.
 */
class MockAuthentication implements Authentication {
    boolean called = false

    @Override
    HttpRequestBase sign(URI uri, HttpMethod httpMethod, ContentType contentType, Object body, HttpRequestBase message) throws MessageSignerException {
        called = true

        message.setHeader("MockAuthentication", "MockValue")
        return message
    }
}
