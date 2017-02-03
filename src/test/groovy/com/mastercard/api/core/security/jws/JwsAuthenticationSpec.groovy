package com.mastercard.api.core.security.jws

import com.mastercard.api.core.exception.SdkException;
import spock.lang.Specification;

public class JwsAuthenticationSpec extends Specification {

    def 'test constructor: JwsAuthentication()' () {
        when:
        JwsAuthentication authentication = new JwsAuthentication()

        then:
        authentication.publicKey == null
        authentication.privateKey == null
        authentication.accessToken == null
    }

    def 'test constructor: JwsAuthentication(String accessToken)' () {
        when:
        JwsAuthentication authentication = new JwsAuthentication("c")

        then:
        authentication.publicKey == null
        authentication.privateKey == null
        authentication.accessToken == "c"
    }

    def 'test constructor: JwsAuthentication(String publicKey, String privateKey)' () {
        when:
        JwsAuthentication authentication = new JwsAuthentication("a", "b")

        then:
        authentication.publicKey == "a"
        authentication.privateKey == "b"
        authentication.accessToken == null
    }

    def 'test constructor: JwsAuthentication(String publicKey, String privateKey, String accessToken)' () {
        when:
        JwsAuthentication authentication = new JwsAuthentication("a", "b", "c")

        then:
        authentication.publicKey == "a"
        authentication.privateKey == "b"
        authentication.accessToken == "c"
    }

    def 'test setters' () {
        given:
        JwsAuthentication authentication = new JwsAuthentication()

        when:
        authentication.setPublicKey("a")
        authentication.setPrivateKey("b")
        authentication.setAccessToken("c")

        then:
        authentication.publicKey == "a"
        authentication.privateKey == "b"
        authentication.accessToken == "c"
    }

    def 'test sign throws unsupported operation' () {
        given:
        JwsAuthentication authentication = new JwsAuthentication("a", "b", "c")

        when:
        authentication.sign(null, null, null, null, null)

        then:
        thrown(SdkException)
    }

}
