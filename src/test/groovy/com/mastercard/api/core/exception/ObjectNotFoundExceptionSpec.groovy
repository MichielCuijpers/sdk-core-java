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
public class ObjectNotFoundExceptionSpec extends Specification {

    def 'test ObjectNotFoundException()' () {
        given:
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException()

        expect:
        objectNotFoundException.getErrorCode() == null
        objectNotFoundException.getErrors() == []
        objectNotFoundException.getMessage() == null
        objectNotFoundException.getStatus() == 404
        objectNotFoundException.getCause() == null
    }

    def 'test ObjectNotFoundException(String s)' () {
        given:
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("mock")

        expect:
        objectNotFoundException.getErrorCode() == null
        objectNotFoundException.getErrors() == []
        objectNotFoundException.getMessage() == "mock"
        objectNotFoundException.getStatus() == 404
        objectNotFoundException.getCause() == null
    }

    def 'test ObjectNotFoundException(String s, Throwable cause)' () {
        given:
        Exception e = new Exception("mock1")
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException("mock2", e)

        expect:
        objectNotFoundException.getErrorCode() == null
        objectNotFoundException.getErrors() == []
        objectNotFoundException.getMessage() == "mock2"
        objectNotFoundException.getStatus() == 404
        objectNotFoundException.getCause() == e
    }

    def 'test ObjectNotFoundException(Throwable cause)' () {
        given:
        Exception e = new Exception("mock")
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException(e)

        expect:
        objectNotFoundException.getErrorCode() == null
        objectNotFoundException.getErrors() == []
        objectNotFoundException.getMessage() == "java.lang.Exception: mock"
        objectNotFoundException.getStatus() == 404
        objectNotFoundException.getCause() == e
    }

    def 'test ObjectNotFoundException(int status, Map<? extends String, ? extends Object> errorData)' () {
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
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException(errorData)

        then:
        objectNotFoundException.getErrorCode() == "SYSTEM_ERROR"
        objectNotFoundException.getErrors().size() == 1
        objectNotFoundException.getErrors() == [errorData.Errors.Error]
        objectNotFoundException.getMessage() == "Unknown Error"
        objectNotFoundException.getStatus() == 404
        objectNotFoundException.getCause() == null

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

        objectNotFoundException = new ObjectNotFoundException(errorData)

        then:
        objectNotFoundException.getErrorCode() == "SYSTEM_ERROR1"
        objectNotFoundException.getErrors().size() == 2
        objectNotFoundException.getErrors() == errorData.Errors.Error
        objectNotFoundException.getMessage() == "Unknown Error1"
        objectNotFoundException.getStatus() == 404
        objectNotFoundException.getCause() == null

        when:
        errorData = [:]

        objectNotFoundException = new ObjectNotFoundException(errorData)

        then:
        objectNotFoundException.getErrorCode() == null
        objectNotFoundException.getErrors().size() == 0
        objectNotFoundException.getErrors() == []
        objectNotFoundException.getMessage() == null
        objectNotFoundException.getStatus() == 404
        objectNotFoundException.getCause() == null
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
        ObjectNotFoundException objectNotFoundException = new ObjectNotFoundException(errorData)

        when:
        String describe = objectNotFoundException.describe()

        then:
        describe == "ObjectNotFoundException: \"Unknown Error\" (status: 404, error code: SYSTEM_ERROR)"
    }

}
