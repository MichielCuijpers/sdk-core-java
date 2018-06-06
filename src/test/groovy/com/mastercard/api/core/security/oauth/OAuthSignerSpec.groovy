package com.mastercard.api.core.security.oauth

import com.mastercard.api.core.model.HttpMethod
import oauth.signpost.exception.OAuthMessageSignerException
import oauth.signpost.http.HttpParameters
import oauth.signpost.http.HttpRequest
import org.apache.http.entity.ContentType
import spock.lang.Ignore
import spock.lang.Specification

import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.PrivateKey;

public class OAuthSignerSpec extends Specification {

    String consumerKey = "uLXKmWNmIkzIGKfA2injnNQqpZaxaBSKxa3ixEVu2f283c95!33b9b2bd960147e387fa6f3f238f07170000000000000000"
    String alias = "fake-key"
    String password = "fakepassword"
    static final keystoreName = "fake-key.p12"

    def 'test sign' () {
        given:
        InputStream is = OAuthAuthentication.class.getClassLoader().getResourceAsStream(keystoreName)
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(is, password.toCharArray());
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());

        HttpMethod post = HttpMethod.POST
        ContentType json = ContentType.APPLICATION_JSON
        String body = "{\"Account\":{\"Status\":\"true\",\"Listed\":\"true\",\"ReasonCode\":\"S\",\"Reason\":\"STOLEN\"}"

        OAuthRequest request = new OAuthRequest();
        request.setMethod(post);
        request.setRequestUrl("https://sandbox.api.mastercard.com/api/mock");
        request.setContentType(json);
        request.setBody(body);



        HttpParameters httpParameters = new HttpParameters()
        httpParameters.put("oauth_body_hash", "u0JWorwmuzHq%2B83yrTJkjURYjUo%3D")
        httpParameters.put("oauth_consumer_key", consumerKey)
        httpParameters.put("oauth_nonce", "6517064390040109503")
        httpParameters.put("oauth_signature_method", "RSA-SHA1")
        httpParameters.put("oauth_timestamp", "1455820772")
        httpParameters.put("oauth_version", "1.0")

        OAuthSigner oAuthSigner = new OAuthSigner(privateKey)

        when:
        String signature = oAuthSigner.sign(request, httpParameters)

        then:
        signature == "UL+yrxiGyZduujqUzh7rBe7LZM28o4RC1CKHj+vSjMGn5jOBXBoObASKLeJXo6LcUJ23IBuFGMsnfkYdRwjHsWYhUeU4zki3B6p40BwNRdtrO7NYB4cSldFHoM8N6EMNXNfTGAYryKx+KJpxVoOXG+aqXFku6l1Ayz9uGJnVo6OxpsLDbVkc9u5XAHmc3hJvcYZ59bD5odzofbCDB3Es0VCEzksFAHTrwwk1NDcOS6w47N6CZ++pCAbHlC7GKvmB7LiPIdcR5OnDGj2o4RlhSw2f440KK1f7vgOPF9fOs5OD3E5lnC0ossLfIEu7KO1Nzy0d0ksS7Pb2lmtlOtfqkw=="
    }

    def 'test sign with null key throws InvalidKeyException' () {
        when:
        new OAuthSigner(null).sign(Mock(HttpRequest), Mock(HttpParameters))

        then:
        def ex = thrown(OAuthMessageSignerException)
        ex.cause instanceof InvalidKeyException
    }
}
