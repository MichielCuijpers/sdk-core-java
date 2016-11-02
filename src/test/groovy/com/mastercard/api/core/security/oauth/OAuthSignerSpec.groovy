package com.mastercard.api.core.security.oauth

import com.mastercard.api.core.model.HttpMethod
import oauth.signpost.exception.OAuthMessageSignerException
import oauth.signpost.http.HttpParameters
import oauth.signpost.http.HttpRequest
import org.apache.http.entity.ContentType
import spock.lang.Specification

import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.PrivateKey;

public class OAuthSignerSpec extends Specification {

    String consumerKey = "L5BsiPgaF-O3qA36znUATgQXwJB6MRoMSdhjd7wt50c97279!50596e52466e3966546d434b7354584c4975693238513d3d"
    String alias = "test"
    String password = "password"
    static final keystoreName = "mcapi_sandbox_key.p12"

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
        signature == "D3wVtEdQ2j196i5Pbzv4RSzgmfDw/Jl+aHpRlIyWL6BZiL1ElrD6b54Dw8Ogb0zYOfRPqVoofFTy2+LUNFE3bP4AZ50gwE63frpQatK7jE0BG7jSfMzm1VnZz4qpml052UtfI7MzK208n3ScYUIMlwyisz/WWnpLgLGkJYt4L9LCObXPku0wcM9NYui1v2OHnCPPQJLLKHaJI/FgHbtvAgqRH9zofUseCpO4lEmxX8LoDYnGbiUm0K+dao7ncm6Gv6cRSRJdjYvtY/JwgkDmvKMl73RE6Ka9gXeNsfzRvcvkBxrUGJjEuLoSDokgonRmOpxhauu9QTzokMxrGaEsHw=="
    }

    def 'test sign with null key throws InvalidKeyException' () {
        when:
        new OAuthSigner(null).sign(Mock(HttpRequest), Mock(HttpParameters))

        then:
        def ex = thrown(OAuthMessageSignerException)
        ex.cause instanceof InvalidKeyException
    }
}
