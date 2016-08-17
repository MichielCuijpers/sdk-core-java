package com.mastercard.api.core

import com.mastercard.api.core.security.jws.JwsAuthentication
import com.mastercard.api.core.security.oauth.OAuthAuthentication
import spock.lang.Specification

/**
 * Created by eamondoyle on 16/02/2016.
 */
class ApiConfigSpec extends Specification {

    def 'test default values' () {
        expect:
        ApiConfig.authentication == null
        !ApiConfig.debug
        ApiConfig.sandbox
    }

    def 'test settings debug' () {
        when:
        ApiConfig.setDebug(true)

        then:
        ApiConfig.isDebug()

        when:
        ApiConfig.setDebug(false)

        then:
        !ApiConfig.isDebug()
    }

    def 'test settings sandbox' () {
        when:
        ApiConfig.setSandbox(true)

        then:
        ApiConfig.isSandbox()

        when:
        ApiConfig.setSandbox(false)

        then:
        !ApiConfig.isSandbox()
    }

    def 'test setting oauth authentication' () {
        given:
        OAuthAuthentication authentication = Mock(OAuthAuthentication)

        when:
        ApiConfig.setAuthentication(authentication)

        then:
        ApiConfig.getAuthentication() != null
        ApiConfig.getAuthentication() instanceof OAuthAuthentication
    }

    def 'test setting jws authentication' () {
        given:
        JwsAuthentication authentication = Mock(JwsAuthentication)

        when:
        ApiConfig.setAuthentication(authentication)

        then:
        ApiConfig.getAuthentication() != null
        ApiConfig.getAuthentication() instanceof JwsAuthentication
    }


    def 'test setHostOverride' () {
        when:
        ApiConfig.setHostOverride("https://api.mastercard.com");

        then:
        ApiConfig.getHostOverride() == "https://api.mastercard.com"

        when:
        ApiConfig.setHostOverride("http://api.mastercard.com");

        then:
        ApiConfig.getHostOverride() == "http://api.mastercard.com"

        when:
        ApiConfig.setHostOverride("blablalb");

        then:
        thrown(MalformedURLException)

        when:
        ApiConfig.setHostOverride(null)

        then:
        ApiConfig.getHostOverride() == null

    }

}
