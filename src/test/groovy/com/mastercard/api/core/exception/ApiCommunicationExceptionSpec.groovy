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
public class ApiCommunicationExceptionSpec extends Specification {

    def 'test ApiCommunicationException()' () {
        given:
        ApiCommunicationException e = new ApiCommunicationException()

        expect:
        e.getErrorCode() == null
        e.getErrors() == []
        e.getMessage() == null
        e.getStatus() == 503
        e.getCause() == null
    }

    def 'test ApiCommunicationException(String s)' () {
        given:
        ApiCommunicationException e = new ApiCommunicationException("mock")

        expect:
        e.getErrorCode() == null
        e.getErrors() == []
        e.getMessage() == "mock"
        e.getStatus() == 503
        e.getCause() == null
    }

    def 'test ApiCommunicationException(String s, Throwable cause)' () {
        given:
        Exception exception = new Exception("mock1")
        ApiCommunicationException e = new ApiCommunicationException("mock2", exception)

        expect:
        e.getErrorCode() == null
        e.getErrors() == []
        e.getMessage() == "mock2"
        e.getStatus() == 503
        e.getCause() == exception
    }

    def 'test ApiCommunicationException(Throwable cause)' () {
        given:
        Exception exception = new Exception("mock")
        ApiCommunicationException e = new ApiCommunicationException(exception)

        expect:
        e.getErrorCode() == null
        e.getErrors() == []
        e.getMessage() == "java.lang.Exception: mock"
        e.getStatus() == 503
        e.getCause() == exception
    }

    def 'test ApiCommunicationException(int status, Map<? extends String, ? extends Object> errorData)' () {
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
        ApiCommunicationException apiCommunicationException = new ApiCommunicationException(errorData)

        then:
        apiCommunicationException.getErrorCode() == "SYSTEM_ERROR"
        apiCommunicationException.getErrors().size() == 1
        apiCommunicationException.getErrors() == [errorData.Errors.Error]
        apiCommunicationException.getMessage() == "Unknown Error"
        apiCommunicationException.getStatus() == 503
        apiCommunicationException.getCause() == null

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

        apiCommunicationException = new ApiCommunicationException(errorData)

        then:
        apiCommunicationException.getErrorCode() == "SYSTEM_ERROR1"
        apiCommunicationException.getErrors().size() == 2
        apiCommunicationException.getErrors() == errorData.Errors.Error
        apiCommunicationException.getMessage() == "Unknown Error1"
        apiCommunicationException.getStatus() == 503
        apiCommunicationException.getCause() == null

        when:
        errorData = [:]

        apiCommunicationException = new ApiCommunicationException(errorData)

        then:
        apiCommunicationException.getErrorCode() == null
        apiCommunicationException.getErrors().size() == 0
        apiCommunicationException.getErrors() == []
        apiCommunicationException.getMessage() == null
        apiCommunicationException.getStatus() == 503
        apiCommunicationException.getCause() == null
    }

    def 'test describe' () {
        given:
        Exception exception = new Exception("mock1")
        ApiCommunicationException e = new ApiCommunicationException("mock2", exception)

        when:
        String describe = e.describe()

        then:
        describe == "ApiCommunicationException: \"mock2\" (status: 503, error code: null)"
    }

}
