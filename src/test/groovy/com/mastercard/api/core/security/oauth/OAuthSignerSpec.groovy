package com.mastercard.api.core.security.oauth

import com.mastercard.api.core.HttpMethod
import oauth.signpost.exception.OAuthMessageSignerException
import oauth.signpost.http.HttpParameters
import oauth.signpost.http.HttpRequest
import org.apache.http.entity.ContentType
import spock.lang.Specification

import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.PrivateKey;

public class OAuthSignerSpec extends Specification {

    String clientId = "gVaoFbo86jmTfOB4NUyGKaAchVEU8ZVPalHQRLTxeaf750b6!414b543630362f426b4f6636415a5973656c33735661383d"
    String alias = "test"
    String password = "password"
    static final keystoreName = "test_key.p12"

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
        httpParameters.put("oauth_consumer_key", clientId)
        httpParameters.put("oauth_nonce", "6517064390040109503")
        httpParameters.put("oauth_signature_method", "RSA-SHA1")
        httpParameters.put("oauth_timestamp", "1455820772")
        httpParameters.put("oauth_version", "1.0")

        OAuthSigner oAuthSigner = new OAuthSigner(privateKey)

        when:
        String signature = oAuthSigner.sign(request, httpParameters)

        then:
        signature == "L/PqLaTp2TV/oGTZPwT0gGy/kMtBEXCqWtgyGsfR3SAmDWLuxIElUgMraBXdFvgKq+yCfWrsVE9N3exbPbmUFahb5nL7d//26SXQY2Vgwp6SY46w5liqpm9Pa4yZNgn2WaYawifPkbzmnZ1193VasffVIahrKcTPWUMAs9lZnhh1HForrAJYuv+4743RxbvRIQjMnZiVP0q+mfK2IHx+0hVLIDhzYb1+D8ammnAGBDzCs3be5gXtxM3pE5MrxfF8bQfdbKRz9w3auyx+vF56p5qMLujkUVF+WTOP0acOav1LFSySXf7bWXeHv/G0XzrS/e3VG4TzC1G/iPoHqPsNgw=="
    }

    def 'test sign with null key throws InvalidKeyException' () {
        when:
        new OAuthSigner(null).sign(Mock(HttpRequest), Mock(HttpParameters))

        then:
        def ex = thrown(OAuthMessageSignerException)
        ex.cause instanceof InvalidKeyException
    }
}
