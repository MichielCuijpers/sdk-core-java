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
public class SystemExceptionSpec extends Specification {

    def 'test SystemException()' () {
        given:
        SystemException systemException = new SystemException()

        expect:
        systemException.getErrorCode() == null
        systemException.getErrors() == []
        systemException.getMessage() == null
        systemException.getStatus() == 500
        systemException.getCause() == null
    }

    def 'test SystemException(String s)' () {
        given:
        SystemException systemException = new SystemException("mock")

        expect:
        systemException.getErrorCode() == null
        systemException.getErrors() == []
        systemException.getMessage() == "mock"
        systemException.getStatus() == 500
        systemException.getCause() == null
    }

    def 'test SystemException(String s, Throwable cause)' () {
        given:
        Exception e = new Exception("mock1")
        SystemException systemException = new SystemException("mock2", e)

        expect:
        systemException.getErrorCode() == null
        systemException.getErrors() == []
        systemException.getMessage() == "mock2"
        systemException.getStatus() == 500
        systemException.getCause() == e
    }

    def 'test SystemException(Throwable cause)' () {
        given:
        Exception e = new Exception("mock")
        SystemException systemException = new SystemException(e)

        expect:
        systemException.getErrorCode() == null
        systemException.getErrors() == []
        systemException.getMessage() == "java.lang.Exception: mock"
        systemException.getStatus() == 500
        systemException.getCause() == e
    }

    def 'test SystemException(int status, Map<? extends String, ? extends Object> errorData)' () {
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
        SystemException systemException = new SystemException(errorData)

        then:
        systemException.getErrorCode() == "SYSTEM_ERROR"
        systemException.getErrors().size() == 1
        systemException.getErrors() == [errorData.Errors.Error]
        systemException.getMessage() == "Unknown Error"
        systemException.getStatus() == 500
        systemException.getCause() == null

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

        systemException = new SystemException(errorData)

        then:
        systemException.getErrorCode() == "SYSTEM_ERROR1"
        systemException.getErrors().size() == 2
        systemException.getErrors() == errorData.Errors.Error
        systemException.getMessage() == "Unknown Error1"
        systemException.getStatus() == 500
        systemException.getCause() == null

        when:
        errorData = [:]

        systemException = new SystemException(errorData)

        then:
        systemException.getErrorCode() == null
        systemException.getErrors().size() == 0
        systemException.getErrors() == []
        systemException.getMessage() == null
        systemException.getStatus() == 500
        systemException.getCause() == null
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
        SystemException systemException = new SystemException(errorData)

        when:
        String describe = systemException.describe()

        then:
        describe == "SystemException: \"Unknown Error\" (status: 500, error code: SYSTEM_ERROR)"
    }

}
