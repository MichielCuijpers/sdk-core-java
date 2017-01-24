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
public class ApiExceptionSpec extends Specification {

    def 'test ApiException()' () {
        given:
        ApiException ApiException = new ApiException()

        expect:
        ApiException.getErrorCode() == null
        ApiException.getErrors() == []
        ApiException.getMessage() == null
        ApiException.getStatus() == 0
        ApiException.getCause() == null
    }

    def 'test ApiException(String s)' () {
        given:
        ApiException ApiException = new ApiException("mock")

        expect:
        ApiException.getErrorCode() == null
        ApiException.getErrors() == []
        ApiException.getMessage() == "mock"
        ApiException.getStatus() == 0
        ApiException.getCause() == null
    }

    def 'test ApiException(String s, Throwable cause)' () {
        given:
        Exception e = new Exception("mock1")
        ApiException ApiException = new ApiException("mock2", e)

        expect:
        ApiException.getErrorCode() == null
        ApiException.getErrors() == []
        ApiException.getMessage() == "mock2"
        ApiException.getStatus() == 0
        ApiException.getCause() == e
    }

    def 'test ApiException(Throwable cause)' () {
        given:
        Exception e = new Exception("mock")
        ApiException ApiException = new ApiException(e)

        expect:
        ApiException.getErrorCode() == null
        ApiException.getErrors() == []
        ApiException.getMessage() == "java.lang.Exception: mock"
        ApiException.getStatus() == 0
        ApiException.getCause() == e
    }

    def 'test ApiException(int status, Map<? extends String, ? extends Object> errorData)' () {
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
        ApiException ApiException = new ApiException(500,errorData)

        then:
        ApiException.getErrorCode() == "SYSTEM_ERROR"
        ApiException.getErrors().size() == 1
        ApiException.getErrors() == [errorData.Errors.Error]
        ApiException.getMessage() == "Unknown Error"
        ApiException.getStatus() == 500
        ApiException.getCause() == null

        when:
        errorData =
                [
                        "errors":
                                [
                                        "error":
                                                [
                                                        "source":"System",
                                                        "reasonCode":"SYSTEM_ERROR",
                                                        "description":"Unknown Error",
                                                        "recoverable":"false"
                                                ]
                                ]
                ]

        ApiException = new ApiException(500,errorData)

        then:
        ApiException.getErrorCode() == "SYSTEM_ERROR"
        ApiException.getErrors().size() == 1
        ApiException.getErrors() == [errorData.errors.error]
        ApiException.getMessage() == "Unknown Error"
        ApiException.getStatus() == 500
        ApiException.getCause() == null

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

        ApiException = new ApiException(500, errorData)

        then:
        ApiException.getErrorCode() == "SYSTEM_ERROR1"
        ApiException.getErrors().size() == 2
        ApiException.getErrors() == errorData.Errors.Error
        ApiException.getMessage() == "Unknown Error1"
        ApiException.getStatus() == 500
        ApiException.getCause() == null


        when:
        errorData =
            [
                "errors":
                    [
                        "error":
                            [
                                [
                                        "source":"System",
                                        "reasonCode":"SYSTEM_ERROR1",
                                        "description":"Unknown Error1",
                                        "recoverable":"false"
                                ],
                                [
                                        "source":"System",
                                        "reasonCode":"SYSTEM_ERROR2",
                                        "description":"Unknown Error2",
                                        "recoverable":"false"
                                ]
                            ]
                    ]
            ]

        ApiException = new ApiException(500, errorData)

        then:
        ApiException.getErrorCode() == "SYSTEM_ERROR1"
        ApiException.getErrors().size() == 2
        ApiException.getErrors() == errorData.errors.error
        ApiException.getMessage() == "Unknown Error1"
        ApiException.getStatus() == 500
        ApiException.getCause() == null

        when:
        errorData = [:]

        ApiException = new ApiException(500, errorData)

        then:
        ApiException.getErrorCode() == null
        ApiException.getErrors().size() == 0
        ApiException.getErrors() == []
        ApiException.getMessage() == null
        ApiException.getStatus() == 500
        ApiException.getCause() == null
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
        ApiException ApiException = new ApiException(500, errorData)

        when:
        String describe = ApiException.describe()

        then:
        describe == "ApiException: \"Unknown Error\" (status: 500, error code: SYSTEM_ERROR)"
    }

}
