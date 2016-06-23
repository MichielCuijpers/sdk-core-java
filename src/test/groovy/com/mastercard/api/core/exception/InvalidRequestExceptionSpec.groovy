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
public class InvalidRequestExceptionSpec extends Specification {

    def 'test InvalidRequestException()' () {
        given:
        InvalidRequestException invalidRequestException = new InvalidRequestException()

        expect:
        invalidRequestException.getErrorCode() == null
        invalidRequestException.getErrors() == []
        invalidRequestException.getMessage() == null
        invalidRequestException.getStatus() == 400
        invalidRequestException.getCause() == null
    }

    def 'test InvalidRequestException(String s)' () {
        given:
        InvalidRequestException invalidRequestException = new InvalidRequestException("mock")

        expect:
        invalidRequestException.getErrorCode() == null
        invalidRequestException.getErrors() == []
        invalidRequestException.getMessage() == "mock"
        invalidRequestException.getStatus() == 400
        invalidRequestException.getCause() == null
    }

    def 'test InvalidRequestException(String s, Throwable cause)' () {
        given:
        Exception e = new Exception("mock1")
        InvalidRequestException invalidRequestException = new InvalidRequestException("mock2", e)

        expect:
        invalidRequestException.getErrorCode() == null
        invalidRequestException.getErrors() == []
        invalidRequestException.getMessage() == "mock2"
        invalidRequestException.getStatus() == 400
        invalidRequestException.getCause() == e
    }

    def 'test InvalidRequestException(Throwable cause)' () {
        given:
        Exception e = new Exception("mock")
        InvalidRequestException invalidRequestException = new InvalidRequestException(e)

        expect:
        invalidRequestException.getErrorCode() == null
        invalidRequestException.getErrors() == []
        invalidRequestException.getMessage() == "java.lang.Exception: mock"
        invalidRequestException.getStatus() == 400
        invalidRequestException.getCause() == e
    }

    def 'test InvalidRequestException(int status, Map<? extends String, ? extends Object> errorData)' () {
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
        InvalidRequestException invalidRequestException = new InvalidRequestException(errorData)

        then:
        invalidRequestException.getErrorCode() == "SYSTEM_ERROR"
        invalidRequestException.getErrors().size() == 1
        invalidRequestException.getErrors() == [errorData.Errors.Error]
        invalidRequestException.getMessage() == "Unknown Error"
        invalidRequestException.getStatus() == 400
        invalidRequestException.getCause() == null

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

        invalidRequestException = new InvalidRequestException(errorData)

        then:
        invalidRequestException.getErrorCode() == "SYSTEM_ERROR1"
        invalidRequestException.getErrors().size() == 2
        invalidRequestException.getErrors() == errorData.Errors.Error
        invalidRequestException.getMessage() == "Unknown Error1"
        invalidRequestException.getStatus() == 400
        invalidRequestException.getCause() == null

        when:
        errorData = [:]

        invalidRequestException = new InvalidRequestException(errorData)

        then:
        invalidRequestException.getErrorCode() == null
        invalidRequestException.getErrors().size() == 0
        invalidRequestException.getErrors() == []
        invalidRequestException.getMessage() == null
        invalidRequestException.getStatus() == 400
        invalidRequestException.getCause() == null
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
        InvalidRequestException invalidRequestException = new InvalidRequestException(errorData)

        when:
        String describe = invalidRequestException.describe()

        then:
        describe == "InvalidRequestException: \"Unknown Error\" (status: 400, error code: SYSTEM_ERROR)"
    }

}
