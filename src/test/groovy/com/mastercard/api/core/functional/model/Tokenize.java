/*
 * Copyright 2016 MasterCard International.
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

package com.mastercard.api.core.functional.model;

import com.mastercard.api.core.exception.*;
import com.mastercard.api.core.model.*;
import com.mastercard.api.core.security.Authentication;

import java.util.Arrays;
import java.util.Map;



public class Tokenize extends BaseObject {

    public Tokenize() {
    }

    public Tokenize(BaseObject o) {
        putAll(o);
    }

    public Tokenize(Map m) {
        putAll(m);
    }


    @Override protected final OperationConfig getOperationConfig(String operationUUID) throws IllegalArgumentException{
        switch (operationUUID) {
        case "uuid":
            return new OperationConfig("/mdes/static/digitization/1/0/tokenize", Action.create, Arrays.asList(""), Arrays.asList(""));
        default:
            throw new IllegalArgumentException("Invalid operationUUID supplied: " + operationUUID);
        }

    }

    @Override protected OperationMetadata getOperationMetadata() throws IllegalArgumentException {
        return new OperationMetadata("0.0.1", "https://sandbox.api.mastercard.com", null);
    }


    
    
    /**
     * Creates an <code>TokenActivate</code> object using the static properties <code>ApiController.PUBLIC_KEY</code> and
     * <code>ApiController.PRIVATE_KEY</code> as the default public and private API keys respectively.
     *
     * @param       map  a map of parameters, valid keys and types:<dl style="padding-left:10px;"><#list method.visibleFlattenedInputs as input>
     *              <dt><code>${input.type} ${input.fullName}</code></dt>    <dd>${input.message} <#if input.required == true><strong>required </strong></#if></dd></#list></dl>
     *
     * @return      a TokenActivate object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */


    public static Tokenize create(RequestMap map)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return create(null, map);
    }


    /**
     * Creates an <code>TokenActivate</code> object
     *
     * @param       auth Authentication object representing overrides for <code>ApiController.PRIVATE_KEY</code> and <code>ApiController.PUBLIC_KEY</code> and/or passing an access token in for operations using OAuth.
     * @param       map  a map of parameters, valid keys and types: <dl style="padding-left:10px;"><#list method.visibleFlattenedInputs as input>
     *              <dt><code>${input.type} ${input.fullName}</code></dt> <dd>${input.message} <#if input.required == true><strong>required </strong></#if></dd></#list></dl>
     *
     * @return      a TokenActivate object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static Tokenize create(Authentication auth, RequestMap map)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return new Tokenize(BaseObject.executeOperation(auth, "uuid", new Tokenize(map)));
    }

    
    
    
    
    
    
}


