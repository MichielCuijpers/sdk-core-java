/*
 * Copyright 2015 MasterCard International.
 *
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of 
 * conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other materials 
 * provided with the distribution.
 * Neither the name of the MasterCard International Incorporated nor the names of its 
 * contributors may be used to endorse or promote products derived from this software 
 * without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF 
 * SUCH DAMAGE.
 *
 */

package com.mastercard.api.core.exception

import spock.lang.Specification

/**
 * Base class for all API exceptions.
 */
public class AuthenticationExceptionSpec extends Specification {

    def 'test AuthenticationException()' () {
        given:
        AuthenticationException authenticationException = new AuthenticationException()

        expect:
        authenticationException.getErrorCode() == null
        authenticationException.getErrors() == []
        authenticationException.getMessage() == null
        authenticationException.getStatus() == 401
        authenticationException.getCause() == null
    }

    def 'test AuthenticationException(String s)' () {
        given:
        AuthenticationException authenticationException = new AuthenticationException("mock")

        expect:
        authenticationException.getErrorCode() == null
        authenticationException.getErrors() == []
        authenticationException.getMessage() == "mock"
        authenticationException.getStatus() == 401
        authenticationException.getCause() == null
    }

    def 'test AuthenticationException(String s, Throwable cause)' () {
        given:
        Exception e = new Exception("mock1")
        AuthenticationException authenticationException = new AuthenticationException("mock2", e)

        expect:
        authenticationException.getErrorCode() == null
        authenticationException.getErrors() == []
        authenticationException.getMessage() == "mock2"
        authenticationException.getStatus() == 401
        authenticationException.getCause() == e
    }

    def 'test AuthenticationException(Throwable cause)' () {
        given:
        Exception e = new Exception("mock")
        AuthenticationException authenticationException = new AuthenticationException(e)

        expect:
        authenticationException.getErrorCode() == null
        authenticationException.getErrors() == []
        authenticationException.getMessage() == "java.lang.Exception: mock"
        authenticationException.getStatus() == 401
        authenticationException.getCause() == e
    }

    def 'test AuthenticationException(int status, Map<? extends String, ? extends Object> errorData)' () {
        given:
        Map<String, Object> errorData =
        [
            "Errors":
            [
                "Error":
                [
                    "Source":"System",
                    "ReasonCode":"SYSTEM_ERROR",
                    "Description":"Unknown Error",
                    "Recoverable":"false"
                ]
            ]
        ]

        when:
        AuthenticationException authenticationException = new AuthenticationException(errorData)

        then:
        authenticationException.getErrorCode() == "SYSTEM_ERROR"
        authenticationException.getErrors().size() == 1
        authenticationException.getErrors() == [errorData.Errors.Error]
        authenticationException.getMessage() == "Unknown Error"
        authenticationException.getStatus() == 401
        authenticationException.getCause() == null

        when:
        errorData =
        [
            "Errors":
            [
                "Error":
                [
                    [
                        "Source":"System",
                        "ReasonCode":"SYSTEM_ERROR1",
                        "Description":"Unknown Error1",
                        "Recoverable":"false"
                    ],
                    [
                        "Source":"System",
                        "ReasonCode":"SYSTEM_ERROR2",
                        "Description":"Unknown Error2",
                        "Recoverable":"false"
                    ]
                ]
            ]
        ]

        authenticationException = new AuthenticationException(errorData)

        then:
        authenticationException.getErrorCode() == "SYSTEM_ERROR1"
        authenticationException.getErrors().size() == 2
        authenticationException.getErrors() == errorData.Errors.Error
        authenticationException.getMessage() == "Unknown Error1"
        authenticationException.getStatus() == 401
        authenticationException.getCause() == null

        when:
        errorData = [:]

        authenticationException = new AuthenticationException(errorData)

        then:
        authenticationException.getErrorCode() == null
        authenticationException.getErrors().size() == 0
        authenticationException.getErrors() == []
        authenticationException.getMessage() == null
        authenticationException.getStatus() == 401
        authenticationException.getCause() == null
    }

    def 'test describe' () {
        given:
        Map<String, Object> errorData =
        [
            "Errors":
            [
                "Error":
                [
                    "Source":"System",
                    "ReasonCode":"SYSTEM_ERROR",
                    "Description":"Unknown Error",
                    "Recoverable":"false"
                ]
            ]
        ]
        AuthenticationException authenticationException = new AuthenticationException(errorData)

        when:
        String describe = authenticationException.describe()

        then:
        describe == "AuthenticationException: \"Unknown Error\" (status: 401, error code: SYSTEM_ERROR)"
    }

}
