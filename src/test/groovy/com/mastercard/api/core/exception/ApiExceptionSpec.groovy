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

import org.json.simple.JSONValue
import spock.lang.Specification

/**
 * Base class for all API exceptions.
 */
public class ApiExceptionSpec extends Specification {

    def 'test ApiException()' () {
        given:
        ApiException ApiException = new ApiException()

        expect:
        ApiException.getReasonCode() == null
        ApiException.getErrors() == []
        ApiException.getMessage() == null
        ApiException.getHttpStatus() == 0
        ApiException.getCause() == null
    }

    def 'test ApiException(String s)' () {
        given:
        ApiException ApiException = new ApiException("mock")

        expect:
        ApiException.getReasonCode() == null
        ApiException.getErrors() == []
        ApiException.getMessage() == "mock"
        ApiException.getHttpStatus() == 0
        ApiException.getCause() == null
    }

    def 'test ApiException(String s, Throwable cause)' () {
        given:
        Exception e = new Exception("mock1")
        ApiException ApiException = new ApiException("mock2", e)

        expect:
        ApiException.getReasonCode() == null
        ApiException.getErrors() == []
        ApiException.getMessage() == "mock2"
        ApiException.getHttpStatus() == 0
        ApiException.getCause() == e
    }

    def 'test ApiException(Throwable cause)' () {
        given:
        Exception e = new Exception("mock")
        ApiException ApiException = new ApiException(e)

        expect:
        ApiException.getReasonCode() == null
        ApiException.getErrors() == []
        ApiException.getMessage() == "java.lang.Exception: mock"
        ApiException.getHttpStatus() == 0
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
        ApiException exception = new ApiException(500,errorData)

        then:
        exception.getReasonCode() == "SYSTEM_ERROR"
        exception.getErrors().size() == 1
        exception.getErrors() == [errorData.Errors.Error]
        exception.getMessage() == "Unknown Error"
        exception.getHttpStatus() == 500
        exception.getCause() == null
        exception.getRawErrorData().get("Errors.Error.Description") == "Unknown Error"



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

        exception = new ApiException(500,errorData)

        then:
        exception.getReasonCode() == "SYSTEM_ERROR"
        exception.getErrors().size() == 1
        exception.getErrors() == [errorData.errors.error]
        exception.getMessage() == "Unknown Error"
        exception.getHttpStatus() == 500
        exception.getCause() == null
        exception.getRawErrorData().get("errors.error.description") == "Unknown Error"

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

        exception = new ApiException(500, errorData)

        then:
        exception.getReasonCode() == "SYSTEM_ERROR1"
        exception.getErrors().size() == 2
        exception.getErrors() == errorData.Errors.Error
        exception.getMessage() == "Unknown Error1"
        exception.getHttpStatus() == 500
        exception.getCause() == null
        exception.getRawErrorData().get("errors.Error[0].Description") == "Unknown Error1"


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

        exception = new ApiException(500, errorData)

        then:
        exception.getReasonCode() == "SYSTEM_ERROR1"
        exception.getErrors().size() == 2
        exception.getErrors() == errorData.errors.error
        exception.getMessage() == "Unknown Error1"
        exception.getHttpStatus() == 500
        exception.getCause() == null

        exception.parseError(1)
        exception.getReasonCode() == "SYSTEM_ERROR2"
        exception.getErrors() == errorData.errors.error
        exception.getMessage() == "Unknown Error2"

        exception.getRawErrorData().get("errors.error[0].description") == "Unknown Error1"
        exception.getRawErrorData().get("errors.error[1].description") == "Unknown Error2"



        //{"errors":[{"source":"OpenAPIClientId","reasonCode":"AUTHORIZATION_FAILED","key":"050007","description":"Unauthorized Access","recoverable":false,"requestId":null,"details":{"details":[{"name":"ErrorDetailCode","value":"050007"}]}}]}

        when:
        errorData =
                [
                        "errors":
                                [
                                        [
                                                "source":"OpenAPIClientId",
                                                "reasonCode":"AUTHORIZATION_FAILED",
                                                "key":"050007",
                                                "description":"Unauthorized Access",
                                                "recoverable":false,
                                                "requestId":null,

                                        ]
                                ]
                ]

        exception = new ApiException(500, errorData)

        then:
        exception.getReasonCode() == "AUTHORIZATION_FAILED"
        exception.getErrors().size() == 1
        exception.getErrors() == errorData.errors
        exception.getMessage() == "Unauthorized Access"
        exception.getSource() == "OpenAPIClientId"
        exception.getHttpStatus() == 500
        exception.getCause() == null
        exception.getRawErrorData().get("errors[0].description") == "Unauthorized Access"

        when:
        List<Map<String, Object>> errorDataList = [

                    [
                        "source":"OpenAPIClientId",
                        "reasonCode":"AUTHORIZATION_FAILED",
                        "key":"050007",
                        "description":"Unauthorized Access",
                        "recoverable":false,
                        "requestId":null,

                    ]
                    ]


        exception = new ApiException(500, errorDataList)

        then:
        exception.getReasonCode() == "AUTHORIZATION_FAILED"
        exception.getErrors().size() == 1
        exception.getErrors() == errorData.errors
        exception.getMessage() == "Unauthorized Access"
        exception.getSource() == "OpenAPIClientId"
        exception.getHttpStatus() == 500
        exception.getCause() == null
        exception.getRawErrorData().get("description") == "Unauthorized Access"

        when:
        errorData = [:]

        exception = new ApiException(500, errorData)

        then:
        exception.getReasonCode() == null
        exception.getErrors().size() == 0
        exception.getErrors() == []
        exception.getMessage() == null
        exception.getHttpStatus() == 500
        exception.getCause() == null


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
        describe == "ApiException: \"Unknown Error\" (httpStatus: 500, reasonCode: SYSTEM_ERROR, source: System)"
    }


    def 'test STP error' () {
        given:
        String stpJsonError = "{\n" +
                "                \"logref\": \"05bf08cb-b18f-42be-bb23-7e17518611dd\",\n" +
                "                \"message\": \"Error - [ICA] element is missing\",\n" +
                "                \"_links\": {\n" +
                "                  \"about\": {\n" +
                "                    \"href\": \"https://api.mastercard.com/stp-api/v1/payments\"\n" +
                "                  }\n" +
                "                }\n" +
                "              }"
        Map<String,Object> errorMap = (Map<String,Object>) JSONValue.parse(stpJsonError)

        when:
        ApiException apiException = new ApiException(400, errorMap)

        then:
        apiException.getRawErrorData().get("logref") == "05bf08cb-b18f-42be-bb23-7e17518611dd"
        apiException.getRawErrorData().get("message") == "Error - [ICA] element is missing"
        apiException.getRawErrorData().get("_links.about.href") == "https://api.mastercard.com/stp-api/v1/payments"

    }

}
