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
        ApiConfig.setDebug(false)

        then:
        !ApiConfig.isDebug()

        when:
        ApiConfig.setDebug(true)

        then:
        ApiConfig.isDebug()


    }

    def 'test settings sandbox' () {
        when:
        ApiConfig.setSandbox(true)

        then:
        ApiConfig.isSandbox()
        !ApiConfig.isProduction()

        when:
        ApiConfig.setSandbox(false);

        then:
        !ApiConfig.isSandbox()
        ApiConfig.isProduction()
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

}
