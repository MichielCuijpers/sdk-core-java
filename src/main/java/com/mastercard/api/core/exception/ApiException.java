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

package com.mastercard.api.core.exception;

import com.mastercard.api.core.model.map.CaseInsensitiveSmartMap;

import java.util.*;

/**
 * Base class for all API exceptions.
 */
public class    ApiException extends Exception {



    private String source;
    private String reasonCode;
    private String description;
    private int httpStatus;
    private CaseInsensitiveSmartMap rawErrorData;
    private CaseInsensitiveSmartMap error;
    private List<CaseInsensitiveSmartMap> errors = new ArrayList<CaseInsensitiveSmartMap>();

    /**
     * Constructs an <code>ApiException</code> with no detail description.
     */
    public ApiException() {
        super();
    }

    /**
     * Constructs an <code>ApiException</code> with the specified detail description.
     *
     * @param s the detail description.
     */
    public ApiException(String s) {
        super(s);
    }

    /**
     * Constructs an <code>ApiException</code> with the specified detail description
     * and cause.
     *
     * @param s     the detail description.
     * @param cause the detail description.
     */
    public ApiException(String s, Throwable cause) {
        super(s, cause);
    }

    /**
     * Constructs an <code>ApiCommunicationException</code> with the specified cause.
     *
     * @param cause the detail description.
     */
    public ApiException(Throwable cause) {
        super(cause);
    }



    /**
     * Constructs an <code>ApiException</code> with the specified details httpStatus
     * and error data.
     *
     * @param httpStatus    the HTTP httpStatus code
     * @param errorData a map representing the error details returned by the API.  The map is
     *                  expected to contain <code>String</code> value for the key  <code>"reference"</code> and
     *                  a map containing the detailed error data for the key <code>"key"</code>.  This map in turn
     *                  is expected to contain <code>String</code> values for the keys
     *                  <code>"code"</code> and <code>"description"</code>.
     */
    public ApiException(int httpStatus, Object errorData) {
        super();

        this.httpStatus = httpStatus;

        parseRawError(errorData);
        parseErrors(errorData);
        parseError(0);
    }

    /**
     * Returns a SmartMap of the error object which is returned from the response.
     * @return
     */
    public CaseInsensitiveSmartMap getError() {
        return this.error;
    }

    /**
     * Parse the error object if the response returns a list
     * To check the error list size see getErrorSize()
     * @param index of the list to parse error object
     */
    public void parseError(int index) {
        if (index >= 0 && index < errors.size()) {
            this.error = errors.get(index);
            parseErrorToMemberVariables();
        }
    }

    private void parseRawError(Object response) {

        if (response instanceof Map) {
            rawErrorData = new CaseInsensitiveSmartMap((Map<String,Object>) response);
        } else if (response instanceof List && ((List) response).isEmpty() == false) {
            Object listItem = ((List) response).get(0);
            if (listItem instanceof Map) {
                rawErrorData =  new CaseInsensitiveSmartMap ((Map<String,Object>) listItem);
            }
        }
    }

    /**
     * Return the error list sise
     * @return
     */
    public int getErrorSize() {
        return this.errors.size();
    }


    private void parseErrors(Object response) {

        List<Map<String,Object>> tmpList = new ArrayList<Map<String, Object>>();

        if (response instanceof List && ((List) response).isEmpty() == false) {
            if (((List<Map<String,Object>>) response).get(0) instanceof Map) {
                tmpList.addAll((List<Map<String,Object>>) response);
            }
        } else if (response instanceof Map) {
            tmpList.add((Map<String,Object>) response);
        }

        for (Map<String,Object> tmpErrorMap : tmpList) {
            CaseInsensitiveSmartMap tmpCaseInsensitiveMap = new CaseInsensitiveSmartMap(tmpErrorMap);
            try {
                if (tmpCaseInsensitiveMap.containsKey("Errors.Error.ReasonCode")) {
                    //errors object with a list of error object
                    Map<String,Object> tmpErrorObj = (Map<String,Object>) tmpCaseInsensitiveMap.get("Errors.Error");
                    addError(tmpErrorObj);
                    continue;
                }
            } catch (Exception e) {

            }

            try {
                if (tmpCaseInsensitiveMap.containsKey("Errors.Error[0].ReasonCode")) {
                    //errors object with a list of error object
                    List<Map<String,Object>> tmpErrorList = (List<Map<String,Object>>) tmpCaseInsensitiveMap.get("Errors.Error");
                    addError(tmpErrorList);
                    continue;
                }
            } catch (Exception e) {

            }

            try {
                if (tmpCaseInsensitiveMap.containsKey("Errors[0].ReasonCode")) {
                    List<Map<String,Object>> tmpErrorList = (List<Map<String,Object>>) tmpCaseInsensitiveMap.get("Errors");
                    addError(tmpErrorList);
                    continue;
                }
            } catch (Exception e) {

            }

            try {

                if (tmpCaseInsensitiveMap.containsKey("ReasonCode")) {
                    addError(tmpCaseInsensitiveMap);
                    continue;
                }
            } catch (Exception e) {

            }
        }
    }

    private void addError(List<Map<String,Object>> errorList) {
        for (Map<String,Object> errorObj : errorList) {
            addError(errorObj);
        }
    }

    private void addError(Map<String,Object> errorMap) {
        errors.add(new CaseInsensitiveSmartMap(errorMap));
    }

    private void parseErrorToMemberVariables() {
        if (error != null) {

            if (error.get("Source") != null) {
                source = error.get("Source").toString();
            }
            if (error.get("ReasonCode") != null) {
                reasonCode = error.get("ReasonCode").toString();
            }
            if (error.get("Description") != null) {
                description = error.get("Description").toString();
            }
        }
    }

    /**
     * Returns the error code for this exception.
     *
     * @return a string representing the API error code (which may be <code>null</code>).
     */
    public String getReasonCode() {
        return reasonCode;
    }

    /**
     * Returns the HTTP httpStatus code for this exception.
     *
     * @return an integer representing the HTTP httpStatus code for this API error (or 0 if there is no httpStatus)
     */
    public int getHttpStatus() {
        return httpStatus;
    }

    /**
     * Returns the Error source
     * @return
     */
    public String getSource() {
        return source;
    }

    public List<CaseInsensitiveSmartMap> getErrors() {
        return errors;
    }

    /**
     * This now returns the raw error object
     * IF you were using this before now use getError() instead
     * @return
     */
    public CaseInsensitiveSmartMap getRawErrorData() {
        return rawErrorData;
    }

    /**
     * Returns the string detail description for this exception.
     *
     * @return a string representing the API error code or the description detail used to construct
     * the exception (which may be <code>null</code>).
     */
    @Override
    public String getMessage() {
        if (description == null) {
            return super.getMessage();
        }

        return description;
    }

    /**
     * Returns a string describing the exception.
     *
     * @return a string describing the exception.
     */
    public String describe() {
        StringBuilder sb = new StringBuilder();
        return sb.append(getClass().getSimpleName())
                .append(": \"")
                .append(getMessage())
                .append("\" (httpStatus: ")
                .append(getHttpStatus())
                .append(", reasonCode: ")
                .append(getReasonCode())
                .append(", source: ")
                .append(getSource())
                .append(")").toString();
    }
}
