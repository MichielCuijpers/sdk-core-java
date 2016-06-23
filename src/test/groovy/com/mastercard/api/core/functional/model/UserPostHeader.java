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
import com.mastercard.api.core.security.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



public class UserPostHeader extends BaseObject  {

    public UserPostHeader() {
    }

    public UserPostHeader(BaseObject o) {
        putAll(o);
    }

    public UserPostHeader(Map m) {
        putAll(m);
    }


    @Override
    protected String getResourcePath(Action action) throws IllegalArgumentException {
        if (action == null) {
            throw new IllegalArgumentException("Action cannot be null");
        }
        if (action == Action.list) {
           return "/mock_crud_server/users/posts";
        }
        throw new IllegalArgumentException("Invalid action supplied: " + action);
    }


    @Override
    protected List<String> getHeaderParams(Action action) throws IllegalArgumentException {
        if (action == null) {
            throw new IllegalArgumentException("Action cannot be null");
        }
        if (action == Action.list) {
           return Arrays.asList("user_id");
        }
        throw new IllegalArgumentException("Invalid action supplied: " + action);
    }

    @Override protected String getApiVersion() {
        return "0.0.1";
    }

    // userId:(query:, param:, header:true, cookie:, body:)
    
    
    
    /**
     * Retrieve <code>UserPostHeader</code> objects using the static properties <code>ApiController.PUBLIC_KEY</code> and
     * <code>ApiController.PRIVATE_KEY</code> as the default public and private API keys respectively and the default
     * criteria (max 20, offset 0, default sorting and no filtering).
     *
     * @return      a ResourceList<UserPostHeader> object which holds the list of UserPostHeader objects and the total
     *              number of UserPostHeader objects available.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     *
     * @see ResourceList
     */
    public static ResourceList<UserPostHeader> list()
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.listObjects(null, new UserPostHeader(), null);
    }

    /**
     * Retrieve <code>UserPostHeader</code> objects using the default criteria (max 20, offset 0, default sorting and no filtering).
     *
     * @param       auth Authentication object representing overrides for <code>ApiController.PRIVATE_KEY</code> and <code>ApiController.PUBLIC_KEY</code> and/or passing an access token in for operations using OAuth.
     *
     * @return      a ResourceList<UserPostHeader> object which holds the list of UserPostHeader objects and the total
     *              number of UserPostHeader objects available.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     *
     * @see ResourceList
     */
    public static ResourceList<UserPostHeader> list(Authentication auth)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.listObjects(auth, new UserPostHeader(), null);
    }

    /**
     * Retrieve <code>UserPostHeader</code> objects using the specified criteria and using the static properties
     * <code>ApiController.PUBLIC_KEY</code> and <code>ApiController.PRIVATE_KEY</code> as the default public and private
     * API keys respectively.
     *
     * @param       criteria a map of parameters; valid keys and types are:<dl style="padding-left:10px;"><#list method.visibleFlattenedInputs as input>
     *              <dt><code>${input.type} ${input.fullName}</code></dt>    <dd>${input.message} <#if input.required == true><strong>required </strong></#if><#if input.fullName == 'sorting'>The value maps properties to the sort direction (either <code>asc</code> for ascending or <code>desc</code> for descending).  Sortable properties are: <#list sortableProperties as input><code>${input}</code> </#list>.</#if></dd></#list></dl>
     *
     * @return      a ResourceList<UserPostHeader> object which holds the list of UserPostHeader objects and the total
     *              number of UserPostHeader objects available.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     *
     * @see ResourceList
     */
    public static ResourceList<UserPostHeader> list(Map criteria)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.listObjects(null, new UserPostHeader(), criteria);
    }

    /**
     * Retrieve <code>UserPostHeader</code> objects using the specified criteria.
     *
     * @param       auth Authentication object representing overrides for <code>ApiController.PRIVATE_KEY</code> and <code>ApiController.PUBLIC_KEY</code> and/or passing an access token in for operations using OAuth.
     * @param       criteria a map of parameters; valid keys and types are: <dl style="padding-left:10px;"><#list method.visibleFlattenedInputs as input>
     *              <dt><code>${input.type} ${input.fullName}</code></dt> <dd>${input.message} <#if input.required == true><strong>required </strong></#if><#if input.fullName == 'sorting'>The value maps properties to the sort direction (either <code>asc</code> for ascending or <code>desc</code> for descending). Sortable properties are: <#list sortableProperties as input> <code>${input}</code> </#list>.</#if></dd></#list></dl>
     *
     * @return      a ResourceList<UserPostHeader> object which holds the list of UserPostHeader objects and the total
     *              number of UserPostHeader objects available.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     *
     * @see ResourceList
     */
    public static ResourceList<UserPostHeader> list(Authentication auth, Map criteria)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.listObjects(auth, new UserPostHeader(), criteria);
    }


    
    
    
}


