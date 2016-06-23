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
public class NotAllowedExceptionSpec extends Specification {

    def 'test NotAllowedException()' () {
        given:
        NotAllowedException notAllowedException = new NotAllowedException()

        expect:
        notAllowedException.getErrorCode() == null
        notAllowedException.getErrors() == []
        notAllowedException.getMessage() == null
        notAllowedException.getStatus() == 403
        notAllowedException.getCause() == null
    }

    def 'test NotAllowedException(String s)' () {
        given:
        NotAllowedException notAllowedException = new NotAllowedException("mock")

        expect:
        notAllowedException.getErrorCode() == null
        notAllowedException.getErrors() == []
        notAllowedException.getMessage() == "mock"
        notAllowedException.getStatus() == 403
        notAllowedException.getCause() == null
    }

    def 'test NotAllowedException(String s, Throwable cause)' () {
        given:
        Exception e = new Exception("mock1")
        NotAllowedException notAllowedException = new NotAllowedException("mock2", e)

        expect:
        notAllowedException.getErrorCode() == null
        notAllowedException.getErrors() == []
        notAllowedException.getMessage() == "mock2"
        notAllowedException.getStatus() == 403
        notAllowedException.getCause() == e
    }

    def 'test notAllowedException(Throwable cause)' () {
        given:
        Exception e = new Exception("mock")
        NotAllowedException notAllowedException = new NotAllowedException(e)

        expect:
        notAllowedException.getErrorCode() == null
        notAllowedException.getErrors() == []
        notAllowedException.getMessage() == "java.lang.Exception: mock"
        notAllowedException.getStatus() == 403
        notAllowedException.getCause() == e
    }

    def 'test NotAllowedException(int status, Map<? extends String, ? extends Object> errorData)' () {
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
        NotAllowedException notAllowedException = new NotAllowedException(errorData)

        then:
        notAllowedException.getErrorCode() == "SYSTEM_ERROR"
        notAllowedException.getErrors().size() == 1
        notAllowedException.getErrors() == [errorData.Errors.Error]
        notAllowedException.getMessage() == "Unknown Error"
        notAllowedException.getStatus() == 403
        notAllowedException.getCause() == null

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

        notAllowedException = new NotAllowedException(errorData)

        then:
        notAllowedException.getErrorCode() == "SYSTEM_ERROR1"
        notAllowedException.getErrors().size() == 2
        notAllowedException.getErrors() == errorData.Errors.Error
        notAllowedException.getMessage() == "Unknown Error1"
        notAllowedException.getStatus() == 403
        notAllowedException.getCause() == null

        when:
        errorData = [:]

        notAllowedException = new NotAllowedException(errorData)

        then:
        notAllowedException.getErrorCode() == null
        notAllowedException.getErrors().size() == 0
        notAllowedException.getErrors() == []
        notAllowedException.getMessage() == null
        notAllowedException.getStatus() == 403
        notAllowedException.getCause() == null
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
        NotAllowedException notAllowedException = new NotAllowedException(errorData)

        when:
        String describe = notAllowedException.describe()

        then:
        describe == "NotAllowedException: \"Unknown Error\" (status: 403, error code: SYSTEM_ERROR)"
    }

}
