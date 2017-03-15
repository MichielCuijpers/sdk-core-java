package com.mastercard.api.core

import com.mastercard.api.core.functional.model.ResourceConfig
import com.mastercard.api.core.model.Action
import com.mastercard.api.core.model.Environment
import com.mastercard.api.core.model.OperationConfig
import com.mastercard.api.core.model.OperationMetadata
import com.mastercard.api.core.model.RequestMap
import com.mastercard.api.core.security.CryptographyInterceptor
import com.mastercard.api.core.security.jws.JwsAuthentication
import com.mastercard.api.core.security.mdes.MDESCryptography
import com.mastercard.api.core.security.oauth.OAuthAuthentication
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by eamondoyle on 16/02/2016.
 */
class ApiConfigSpec extends Specification {

    def 'test adding interceptors' () {
        setup:
        InputStream is2 = new FileInputStream("src/test/resources/mastercard_public.crt")
        InputStream is3 = new FileInputStream("src/test/resources/mastercard_private.key")
        CryptographyInterceptor interceptor = new MDESCryptography(is2, is3)

        is2 = new FileInputStream("src/test/resources/mastercard_public.crt")
        is3 = new FileInputStream("src/test/resources/mastercard_private.key")
        CryptographyInterceptor interceptor2 = new MDESCryptography(is2, is3)

        when:
        ApiConfig.addCryptographyInterceptor(interceptor)

        then:
        ApiConfig.cryptographyInterceptorSet.size() == 1

        when:

        ApiConfig.addCryptographyInterceptor(interceptor2)

        then:
        ApiConfig.cryptographyInterceptorSet.size() == 1

        cleanup:
        ApiConfig.cryptographyInterceptorSet.clear();


    }

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
        ApiConfig.setEnvironment(Environment.PRODUCTION_MTF)

        then:
        !ApiConfig.isSandbox()
        !ApiConfig.isProduction()
        ApiConfig.getEnvironment() == Environment.PRODUCTION_MTF

        when:
        ApiConfig.setSandbox(false)

        then:
        ApiConfig.getEnvironment() == Environment.PRODUCTION

        when:
        ApiConfig.setSandbox(true)

        then:
        ApiConfig.getEnvironment() == Environment.SANDBOX

    }

    @Unroll
    def "test Environment.parse(#envrironment) "() {
        given:
        Environment parsed = null;

        when:
        parsed = Environment.parse(envrironment)

        then:
        parsed == result

        where:
        envrironment       | result
        "production"       | Environment.PRODUCTION
        "SANDBOX"          | Environment.SANDBOX
        "sTaGe"            | Environment.STAGE



    }

    def 'test throws runtime error when setting a SDK environment which does not exist'(){
        setup:
        def config = ResourceConfig.getInstance();
        ApiConfig.registeredInstances.clear();
        ApiConfig.registerResourceConfig(config);

        when:
        ApiConfig.setEnvironment(Environment.OTHER)

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
