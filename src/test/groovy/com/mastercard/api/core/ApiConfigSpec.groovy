package com.mastercard.api.core

import com.mastercard.api.core.functional.model.ResourceConfig
import com.mastercard.api.core.model.Environment
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


    def 'test setting sandbox && mft' () {
        when:
        ApiConfig.setEnvironment(Environment.MTF)

        then:
        !ApiConfig.isSandbox()
        !ApiConfig.isProduction()
        ApiConfig.getEnvironment() == Environment.MTF

        when:
        ApiConfig.setSandbox(false)

        then:
        ApiConfig.getEnvironment() == Environment.PRODUCTION

        when:
        ApiConfig.setSandbox(true)

        then:
        ApiConfig.getEnvironment() == Environment.SANDBOX

    }

    def 'test throws runtime error when setting a SDK environment which does not exist'(){
        setup:
        def config = ResourceConfig.getInstance();
        ApiConfig.clearResourceConfig();
        ApiConfig.registerResourceConfig(config);

        when:
        ApiConfig.setEnvironment(Environment.OTHER1)

        then: "IllegalStateException is thrown"
        thrown(RuntimeException)
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
