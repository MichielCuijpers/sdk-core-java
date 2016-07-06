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

import java.util.Map;

/**
 * Thrown to indicate that a request was not allowed.
 * HTTP 403
 *
 * @see com.mastercard.api.core.exception.ApiException
 */
public class NotAllowedException extends ApiException {

    /**
     * Constructs an <code>NotAllowedException</code> with no detail message.
     */
    public NotAllowedException() {
        super();
    }

    /**
     * Constructs an <code>NotAllowedException</code> with the specified detail message.
     *
     * @param s the detail message.
     */
    public NotAllowedException(String s) {
        super(s);
    }

    /**
     * Constructs an <code>NotAllowedException</code> with the specified detail message
     * and cause.
     *
     * @param s     the detail message.
     * @param cause the detail message.
     */
    public NotAllowedException(String s, Throwable cause) {
        super(s, cause);
    }

    /**
     * Constructs an <code>NotAllowedException</code> with the specified cause.
     *
     * @param cause the detail message.
     */
    public NotAllowedException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an <code>NotAllowedException</code> with the specified status
     * and error data.
     *
     * @param errorData a map representing the error details returned by the API.  The map is
     *                  expected to contain <code>String</code> value for the key  <code>"reference"</code> and
     *                  a map containing the detailed error data for the key <code>"key"</code>.  This map in turn
     *                  is expected to contain <code>String</code> values for the keys
     *                  <code>"code"</code> and <code>"message"</code>.
     * @see com.mastercard.api.core.exception.ApiException
     */
    public NotAllowedException(Map<? extends String, ? extends Object> errorData) {
        super(403, errorData);
    }

    @Override
    public int getStatus() {
        return 403;
    }
}

