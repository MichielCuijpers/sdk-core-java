package com.mastercard.api.core

import spock.lang.Specification

public class ConstantsSpec extends Specification {

    def 'test constants' () {
        given:
        new Constants()

        expect:
        Constants.API_BASE_LIVE_URL == "https://api.mastercard.com"
        Constants.API_BASE_SANDBOX_URL == "https://sandbox.api.mastercard.com"
    }
}
