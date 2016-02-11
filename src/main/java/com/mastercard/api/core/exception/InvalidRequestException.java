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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Thrown to indicate that an error occured processing an API request.
 *
 * @see com.mastercard.api.core.exception.ApiException
 */
public class InvalidRequestException extends ApiException {


    private List<FieldError> fieldErrors = new ArrayList<FieldError>();

    /**
     * Constructs an <code>InvalidRequestException</code> with no detail message.
     */
    public InvalidRequestException() {
        super();
    }

    /**
     * Constructs an <code>InvalidRequestException</code> with the specified detail message.
     *
     * @param s the detail message.
     */
    public InvalidRequestException(String s) {
        super(s);
    }

    /**
     * Constructs an <code>InvalidRequestException</code> with the specified detail message
     * and cause.
     *
     * @param s     the detail message.
     * @param cause the detail message.
     */
    public InvalidRequestException(String s, Throwable cause) {
        super(s, cause);
    }

    /**
     * Constructs an <code>InvalidRequestException</code> with the specified cause.
     *
     * @param cause the detail message.
     */
    public InvalidRequestException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an <code>InvalidRequestException</code> with the specified status
     * and error data.
     *
     * @param status    the HTTP status code
     * @param errorData a map representing the error details returned by the API.  The map is
     *                  expected to contain <code>String</code> value for the key  <code>"reference"</code> and
     *                  a map containing the detailed error data for the key <code>"key"</code>.  This map in turn
     *                  is expected to contain <code>String</code> values for the keys
     *                  <code>"code"</code> and <code>"message"</code> and a list for the key <code>"fieldErrors"</code>
     *                  with each entry  containing error information for a particular field.
     * @see com.mastercard.api.core.exception.ApiException
     * @see com.mastercard.api.core.exception.InvalidRequestException$FieldError
     */
    public InvalidRequestException(int status, Map<? extends String, ? extends Object> errorData) {
        super(status, errorData);

        Map<? extends String, ? extends Object> error = (Map<? extends String, ? extends Object>) errorData.get("error");
        if (error != null) {

            List<Map<? extends String, ? extends Object>> errors = (List<Map<? extends String, ? extends Object>>) error.get("fieldErrors");
            if (errors != null) {
                for (Map<? extends String, ? extends Object> fieldData : errors) {
                    FieldError fe = new FieldError(fieldData);
                    fieldErrors.add(fe);
                }
            }
        }
    }

    /**
     * Returns a boolean indicating if this exception contains field errors.
     *
     * @return a boolean indicating if this exception contains field errors.
     */
    public boolean hasFieldErrors() {
        return fieldErrors.size() > 0;
    }

    /**
     * Returns the list of field errors for this exception.
     *
     * @return a list of <code>FieldError</code> objects (may be empty).
     * @see com.mastercard.api.core.exception.InvalidRequestException$FieldError
     */
    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    /**
     * Returns a string describing the exception.
     *
     * @return a string describing the exception.
     */
    public String describe() {
        StringBuilder sb = new StringBuilder(super.describe());
        for (FieldError fieldError : getFieldErrors()) {
            sb.append("\n").append(fieldError.toString());
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Class representing a single error on a field in a request sent to the API.
     */
    public class FieldError {


        private String errorCode;
        private String fieldName;
        private String message;

        /**
         * Constructs a <code>FieldError</code> object using the specified data.
         *
         * @param data a map containing <code>String</code> values for the keys <code>"code"</code>,
         *             <code>"field"</code> and <code>"message"</code>.
         */
        FieldError(Map<? extends String, ? extends Object> data) {
            errorCode = (String) data.get("code");
            fieldName = (String) data.get("field");
            message = (String) data.get("message");
        }

        /**
         * Returns the error code for this field error.
         *
         * @return a string error code (may be null).
         */
        public String getErrorCode() {
            return errorCode;
        }

        /**
         * Returns the field name for this field error.
         *
         * @return a string field name (may be null).
         */
        public String getFieldName() {
            return fieldName;
        }

        /**
         * Returns the erorr message for this field error.
         *
         * @return a string error message (may be null).
         */
        public String getMessage() {
            return message;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Field error: ")
                    .append(getFieldName())
                    .append("\" ")
                    .append(getMessage())
                    .append("\" (")
                    .append(getErrorCode())
                    .append(")");
            return sb.toString();
        }
    }


}
