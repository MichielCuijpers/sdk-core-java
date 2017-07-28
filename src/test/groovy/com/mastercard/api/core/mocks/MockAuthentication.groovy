package com.mastercard.api.core.mocks

import com.mastercard.api.core.exception.SdkException
import com.mastercard.api.core.model.HttpMethod

import com.mastercard.api.core.security.Authentication
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.ContentType

/**
 * Created by eamondoyle on 17/02/2016.
 */
class MockAuthentication implements Authentication {
    boolean called = false

    @Override
    HttpRequestBase sign(URI uri, HttpMethod httpMethod, ContentType contentType, Object body, HttpRequestBase message) throws SdkException {
        called = true

        message.setHeader("MockAuthentication", "MockValue")

        if (body != null) {
            message.setHeader("Authorization", "OAuth oauth_body_hash=\"Example\",\n" +
                    "    oauth_consumer_key=\"0685bd9184jfhq22\",\n" +
                    "    oauth_token=\"ad180jjd733klru7\",\n" +
                    "    oauth_signature_method=\"HMAC-SHA1\",\n" +
                    "    oauth_signature=\"wOJIO9A2W5mFwDgiDvZbTSMK%2FPY%3D\",\n" +
                    "    oauth_timestamp=\"137131200\",\n" +
                    "    oauth_nonce=\"4572616e48616d6d65724c61686176\",\n" +
                    "    oauth_version=\"1.0\"")
        } else {
            message.setHeader("Authorization", "OAuth oauth_consumer_key=\"0685bd9184jfhq22\",\n" +
                    "    oauth_token=\"ad180jjd733klru7\",\n" +
                    "    oauth_signature_method=\"HMAC-SHA1\",\n" +
                    "    oauth_signature=\"wOJIO9A2W5mFwDgiDvZbTSMK%2FPY%3D\",\n" +
                    "    oauth_timestamp=\"137131200\",\n" +
                    "    oauth_nonce=\"4572616e48616d6d65724c61686176\",\n" +
                    "    oauth_version=\"1.0\"")
        }

        return message
    }
}
