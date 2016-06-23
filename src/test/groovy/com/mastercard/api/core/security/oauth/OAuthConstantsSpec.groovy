package com.mastercard.api.core.security.oauth

import spock.lang.Specification

public class OAuthConstantsSpec extends Specification {

    def 'test constants' () {
        given:
        new OAuthConstants()

        expect:
        OAuthConstants.OAUTH_BODY_HASH == "oauth_body_hash"
        OAuthConstants.SHA1 == "SHA-1"
        OAuthConstants.UTF_8 == "UTF-8"
    }
}
