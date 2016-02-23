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

/**
 * Thrown to indicate that the request message could not be signed.
 *
 * @see ApiException
 */
public class MessageSignerException extends ApiException {

    /**
     * Constructs a <code>MessageSignerException</code> with the specified detail message.
     *
     * @param s the detail message.
     */
    public MessageSignerException(String s) {
        super(s);
    }

    /**
     * Constructs a <code>MessageSignerException</code> with the specified cause.
     *
     * @param cause the detail message.
     */
    public MessageSignerException(Throwable cause) {
        super(cause);
    }

    @Override
    public int getStatus() {
        return 0;
    }

}
