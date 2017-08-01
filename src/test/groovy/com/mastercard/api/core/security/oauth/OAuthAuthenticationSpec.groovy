package com.mastercard.api.core.security.oauth

import com.mastercard.api.core.Constants
import com.mastercard.api.core.exception.SdkException
import com.mastercard.api.core.model.HttpMethod
import oauth.signpost.OAuth
import oauth.signpost.basic.DefaultOAuthConsumer
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import spock.lang.Specification

import java.security.KeyStore
import java.security.PrivateKey
import java.util.regex.Matcher
import java.util.regex.Pattern

public class OAuthAuthenticationSpec extends Specification {

    String consumerKey = "L5BsiPgaF-O3qA36znUATgQXwJB6MRoMSdhjd7wt50c97279!50596e52466e3966546d434b7354584c4975693238513d3d"
    String alias = "test"
    String password = "password"
    static final keystoreName = "mcapi_sandbox_key.p12"

    static final Pattern AUTHORIZATION = Pattern.compile("\\s*(\\w*)\\s+(.*)");
    static final Pattern NVP = Pattern.compile("(\\S*)\\s*\\=\\s*\"([^\"]*)\"");
    static final String AUTH_SCHEME = "OAuth";

    def 'test constructor: OAuthAuthentication(String consumerKey, InputStream is, String alias, String password)' () {
        given:
        InputStream is = OAuthAuthentication.class.getClassLoader().getResourceAsStream(keystoreName)
        OAuthAuthentication authentication = new OAuthAuthentication(consumerKey, is, alias, password)

        is = OAuthAuthentication.class.getClassLoader().getResourceAsStream(keystoreName)
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(is, password.toCharArray());
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());

        expect:
        authentication.consumerKey == "L5BsiPgaF-O3qA36znUATgQXwJB6MRoMSdhjd7wt50c97279!50596e52466e3966546d434b7354584c4975693238513d3d"
        authentication.privateKey == privateKey
    }

    def 'test null inputstream throws IllegalArgumentException' () {
        when:
        new OAuthAuthentication(consumerKey, null, alias, password)

        then:
        def ex = thrown(SdkException)
        ex.message == "InputStream cannot null"
    }

    def 'test null consumerKey throws IllegalArgumentException' () {
        when:
        InputStream is = OAuthAuthentication.class.getClassLoader().getResourceAsStream(keystoreName)
        new OAuthAuthentication(null, is, alias, password)

        then:
        def ex = thrown(SdkException)
        ex.message == "ConsumerKey cannot null"
    }

    def 'test wrong key alias throws IllegalArgumentException' () {
        when:
        InputStream is = OAuthAuthentication.class.getClassLoader().getResourceAsStream(keystoreName)
        new OAuthAuthentication(consumerKey, is, "unknown", password)

        then:
        def ex = thrown(SdkException)
        ex.message == "No key found for alias [unknown]"
    }

    def 'test invalid password throws IOException' () {
        when:
        InputStream is = OAuthAuthentication.class.getClassLoader().getResourceAsStream(keystoreName)
        new OAuthAuthentication(consumerKey, is, alias, "invalid")

        then:
        thrown(SdkException)
    }

    def 'test sign' () {
        given:
        DefaultOAuthConsumer.metaClass.generateTimestamp = { -> TIMESTAMP }
        DefaultOAuthConsumer.metaClass.generateNonce = { -> NONCE }

        InputStream is = OAuthAuthentication.class.getClassLoader().getResourceAsStream(keystoreName)
        OAuthAuthentication authentication = new OAuthAuthentication(consumerKey, is, alias, password)

        URI uri = new URI("https://sandbox.api.mastercard.com/api/mock")
        HttpMethod post = HttpMethod.POST
        ContentType json = ContentType.APPLICATION_JSON
        String body = "{\"Account\":{\"Status\":\"true\",\"Listed\":\"true\",\"ReasonCode\":\"S\",\"Reason\":\"STOLEN\"}"

        HttpRequestBase message = new HttpPost(uri)
        HttpEntity createEntity = new StringEntity(body)
        ((HttpPost) message).setEntity(createEntity);

        when:
        HttpRequestBase response = authentication.sign(uri, post, json, body, message)

        Header header = response.getFirstHeader(OAuth.HTTP_AUTHORIZATION_HEADER);
        Map<String, String> tokens = decodeAuthorization(header.value)

        then:
        header.name == OAuth.HTTP_AUTHORIZATION_HEADER
        tokens."oauth_body_hash" == "ICyYzhlixhDaaOr7QCdIMvlw/Rq/8KhiGfVtrWk5xyY="
        tokens."oauth_consumer_key" == consumerKey
        tokens."oauth_signature_method" == "RSA-SHA256"
        tokens."oauth_version" == "1.0"

        tokens.containsKey("oauth_nonce")
        tokens.containsKey("oauth_signature")
        tokens.containsKey("oauth_timestamp")
    }

    private Map<String, String> decodeAuthorization(String authorization) {
        Map<String, String> map = new HashMap<>();

        if (authorization != null) {
            Matcher m = AUTHORIZATION.matcher(authorization);
            if (m.matches()) {
                if (AUTH_SCHEME.equalsIgnoreCase(m.group(1))) {
                    for (String nvp : m.group(2).split("\\s*,\\s*")) {
                        m = NVP.matcher(nvp);
                        if (m.matches()) {
                            String name = URLDecoder.decode(m.group(1), "UTF-8");
                            String value = URLDecoder.decode(m.group(2), "UTF-8");
                            map.put(name, value);
                        }
                    }
                }
            }
        }

        return map;
    }

}
