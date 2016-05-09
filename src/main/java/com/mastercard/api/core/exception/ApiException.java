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

import com.mastercard.api.core.model.RequestMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Base class for all API exceptions.
 */
public abstract class ApiException extends Exception {

    private String errorCode;
    private String message;
    private int status;

    private List<Map<? extends String, ? extends Object>> errors = new ArrayList<>();

    /**
     * Constructs an <code>ApiException</code> with no detail message.
     */
    public ApiException() {
        super();
    }

    /**
     * Constructs an <code>ApiException</code> with the specified detail message.
     *
     * @param s the detail message.
     */
    public ApiException(String s) {
        super(s);
    }

    /**
     * Constructs an <code>ApiException</code> with the specified detail message
     * and cause.
     *
     * @param s     the detail message.
     * @param cause the detail message.
     */
    public ApiException(String s, Throwable cause) {
        super(s, cause);
    }

    /**
     * Constructs an <code>ApiCommunicationException</code> with the specified cause.
     *
     * @param cause the detail message.
     */
    public ApiException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an <code>ApiException</code> with the specified details status
     * and error data.
     *
     * @param status    the HTTP status code
     * @param errorData a map representing the error details returned by the API.  The map is
     *                  expected to contain <code>String</code> value for the key  <code>"reference"</code> and
     *                  a map containing the detailed error data for the key <code>"key"</code>.  This map in turn
     *                  is expected to contain <code>String</code> values for the keys
     *                  <code>"code"</code> and <code>"message"</code>.
     */
    public ApiException(int status, Map<? extends String, ? extends Object> errorData) {
        super();

        this.status = status;

        // Use RequestMap for easy traversing
        RequestMap requestMap = new RequestMap((Map<String, Object>) errorData);

        if (!requestMap.containsKey("Errors.Error"))
            return;

        Object o = requestMap.get("Errors.Error");

        if (o instanceof Map) {
            errors.add((Map<? extends String, ? extends Object>) o);
        }
        else if (o instanceof List) {
            errors = (List<Map<? extends String, ? extends Object>>) o;
        }

        // Use the first error
        if (errors.size() > 0) {
            Map<? extends String, ? extends Object> error = errors.get(0);
            errorCode = (String) error.get("ReasonCode");
            message = (String) error.get("Description");
        }
    }

    /**
     * Returns the error code for this exception.
     *
     * @return a string representing the API error code (which may be <code>null</code>).
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the HTTP status code for this exception.
     *
     * @return an integer representing the HTTP status code for this API error (or 0 if there is no status)
     */
    public abstract int getStatus();

    public List<Map<? extends String, ? extends Object>> getErrors() {
        return errors;
    }

    /**
     * Returns the string detail message for this exception.
     *
     * @return a string representing the API error code or the message detail used to construct
     * the exception (which may be <code>null</code>).
     */
    @Override
    public String getMessage() {
        if (message == null) {
            return super.getMessage();
        }

        return message;
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
                .append("\" (status: ")
                .append(getStatus())
                .append(", error code: ")
                .append(getErrorCode())
                .append(")").toString();
    }
}
