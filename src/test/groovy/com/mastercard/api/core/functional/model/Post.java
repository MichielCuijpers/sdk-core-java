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

import com.mastercard.api.core.model.BaseObject;
import com.mastercard.api.core.exception.*;
import com.mastercard.api.core.model.*;
import com.mastercard.api.core.security.*;
import java.util.Arrays;
import java.util.Map;



public class Post extends BaseObject {

    private static ResourceConfig config = new ResourceConfig();
    public Post() {
    }

    public Post(BaseObject o) {
        putAll(o);
    }

    public Post(Map m) {
        putAll(m);
    }


    @Override protected final OperationConfig getOperationConfig(String operationUUID) throws IllegalArgumentException{
        switch (operationUUID) {
        case "list":
            return new OperationConfig("/mock_crud_server/posts", Action.list, Arrays.asList(""), Arrays.asList(""));
        case "create":
            return new OperationConfig("/mock_crud_server/posts", Action.create, Arrays.asList(""), Arrays.asList(""));
        case "read":
            return new OperationConfig("/mock_crud_server/posts/{id}", Action.read, Arrays.asList(""), Arrays.asList(""));
        case "update":
            return new OperationConfig("/mock_crud_server/posts/{id}", Action.update, Arrays.asList(""), Arrays.asList(""));
        case "delete":
            return new OperationConfig("/mock_crud_server/posts/{id}", Action.delete, Arrays.asList(""), Arrays.asList(""));
        default:
            throw new IllegalArgumentException("Invalid operationUUID supplied: " + operationUUID);
        }

    }

    @Override protected OperationMetadata getOperationMetadata() throws IllegalArgumentException {
        return new OperationMetadata(config.getVersion(), config.getHost(), config.getContext());
    }

    
    /**
     * Retrieve <code>Post</code> objects using the static properties <code>ApiController.PUBLIC_KEY</code> and
     * <code>ApiController.PRIVATE_KEY</code> as the default public and private API keys respectively and the default
     * criteria (max 20, offset 0, default sorting and no filtering).
     *
     * @return      a ResourceList<Post> object which holds the list of Post objects and the total
     *              number of Post objects available.
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
    public static ResourceList<Post> list()
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.executeListOperation(null, "list", new Post(), null);
    }

    /**
     * Retrieve <code>Post</code> objects using the default criteria (max 20, offset 0, default sorting and no filtering).
     *
     * @param       auth Authentication object representing overrides for <code>ApiController.PRIVATE_KEY</code> and <code>ApiController.PUBLIC_KEY</code> and/or passing an access token in for operations using OAuth.
     *
     * @return      a ResourceList<Post> object which holds the list of Post objects and the total
     *              number of Post objects available.
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
    public static ResourceList<Post> list(Authentication auth)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.executeListOperation(auth, "list", new Post(), null);
    }

    /**
     * Retrieve <code>Post</code> objects using the specified criteria and using the static properties
     * <code>ApiController.PUBLIC_KEY</code> and <code>ApiController.PRIVATE_KEY</code> as the default public and private
     * API keys respectively.
     *
     * @param       criteria a map of parameters; valid keys and types are:<dl style="padding-left:10px;"><#list method.visibleFlattenedInputs as input>
     *              <dt><code>${input.type} ${input.fullName}</code></dt>    <dd>${input.message} <#if input.required == true><strong>required </strong></#if><#if input.fullName == 'sorting'>The value maps properties to the sort direction (either <code>asc</code> for ascending or <code>desc</code> for descending).  Sortable properties are: <#list sortableProperties as input><code>${input}</code> </#list>.</#if></dd></#list></dl>
     *
     * @return      a ResourceList<Post> object which holds the list of Post objects and the total
     *              number of Post objects available.
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
    public static ResourceList<Post> list(Map criteria)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.executeListOperation(null, "list", new Post(), criteria);
    }

    /**
     * Retrieve <code>Post</code> objects using the specified criteria.
     *
     * @param       auth Authentication object representing overrides for <code>ApiController.PRIVATE_KEY</code> and <code>ApiController.PUBLIC_KEY</code> and/or passing an access token in for operations using OAuth.
     * @param       criteria a map of parameters; valid keys and types are: <dl style="padding-left:10px;"><#list method.visibleFlattenedInputs as input>
     *              <dt><code>${input.type} ${input.fullName}</code></dt> <dd>${input.message} <#if input.required == true><strong>required </strong></#if><#if input.fullName == 'sorting'>The value maps properties to the sort direction (either <code>asc</code> for ascending or <code>desc</code> for descending). Sortable properties are: <#list sortableProperties as input> <code>${input}</code> </#list>.</#if></dd></#list></dl>
     *
     * @return      a ResourceList<Post> object which holds the list of Post objects and the total
     *              number of Post objects available.
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
    public static ResourceList<Post> list(Authentication auth, Map criteria)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.executeListOperation(auth, "list", new Post(), criteria);
    }


    
    
    // body:(query:, param:, header:, cookie:, body:true)
    
    /**
     * Creates an <code>Post</code> object using the static properties <code>ApiController.PUBLIC_KEY</code> and
     * <code>ApiController.PRIVATE_KEY</code> as the default public and private API keys respectively.
     *
     * @param       map  a map of parameters, valid keys and types:<dl style="padding-left:10px;"><#list method.visibleFlattenedInputs as input>
     *              <dt><code>${input.type} ${input.fullName}</code></dt>    <dd>${input.message} <#if input.required == true><strong>required </strong></#if></dd></#list></dl>
     *
     * @return      a Post object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static Post create(RequestMap map)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return create(null, map);
    }

    /**
     * Creates an <code>Post</code> object
     *
     * @param       auth Authentication object representing overrides for <code>ApiController.PRIVATE_KEY</code> and <code>ApiController.PUBLIC_KEY</code> and/or passing an access token in for operations using OAuth.
     * @param       privateKey Private API key. If null, the value of static <code>ApiController.PRIVATE_KEY</code> will be used
     * @param       map  a map of parameters, valid keys and types: <dl style="padding-left:10px;"><#list method.visibleFlattenedInputs as input>
     *              <dt><code>${input.type} ${input.fullName}</code></dt> <dd>${input.message} <#if input.required == true><strong>required </strong></#if></dd></#list></dl>
     *
     * @return      a Post object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static Post create(Authentication auth, RequestMap map)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return new Post(BaseObject.executeOperation(auth, "create", new Post(map)));
    }

    
    
    
    
    // id:(query:, param:true, header:, cookie:, body:)
    
    
    
    
    
    /**
     * Retrieve a <code>Post</code> object using the static properties <code>ApiController.PUBLIC_KEY</code> and
     * <code>ApiController.PRIVATE_KEY</code> as the default public and private API keys respectively.
     *
     * @param       id  the id of the Post object to retrieve
     *
     * @return      a Post object
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static Post read(String id)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return read(null, id, null);
    }

    /**
     * Retrieve a <code>Post</code> object using the static properties <code>ApiController.PUBLIC_KEY</code> and
     * <code>ApiController.PRIVATE_KEY</code> as the default public and private API keys respectively.
     *
     * @param       id  the id of the Post object to retrieve
     * @param       query a map of additional query parameters
     *
     * @return      a Post object
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static Post read(String id, Map query)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return read(null, id, query);
    }

    /**
     * Retrieve a <code>Post</code> object.
     *
     * @param       auth Authentication object representing overrides for <code>ApiController.PRIVATE_KEY</code> and <code>ApiController.PUBLIC_KEY</code> and/or passing an access token in for operations using OAuth.
     * @param       id  the id of the <code>Post</code> object to retrieve
     *
     * @return      a Post object
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static Post read(Authentication auth, String id)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return read(auth, id, null);
    }

    /**
     * Retrieve a <code>Post</code> object.
     *
     * @param       auth Authentication object representing overrides for <code>ApiController.PRIVATE_KEY</code> and <code>ApiController.PUBLIC_KEY</code> and/or passing an access token in for operations using OAuth.
     * @param       id  the id of the <code>Post</code> object to retrieve
     * @param       query a map of additional query parameters
     *
     * @return      a Post object
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static Post read(Authentication auth, String id, Map query)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        Post val = new Post();
        if (id != null) val.put("id", id);
        if (query != null)  val.putAll(query);
        return new Post(BaseObject.executeOperation(auth, "read", val));
    }

    // id:(query:, param:true, header:, cookie:, body:)// body:(query:, param:, header:, cookie:, body:true)
    
    
    /**
     * Updates an <code>Post</code> object.
     *
     * The properties that can be updated:
     * <ul><#list method.visibleFlattenedInputs as input>
     * <#if input.fullName != "id"><li>${input.name} <#if input.required == true><strong>(required)</strong></#if></li></#if>
     * </#list></ul>
     *
     * @return      a Post object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public Post update()
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = this.executeOperation(null, "update", this);
        this.putAll(object);
        return this;
    }

    /**
     * Updates an <code>Post</code> object.
     *
     * The properties that can be updated:
     * <ul><#list method.visibleFlattenedInputs as input>
     * <#if input.fullName != "id"><li>${input.name} <#if input.required == true><strong>(required)</strong></#if></li></#if>
     * </#list></ul>
     *
     * @param       auth Authentication object representing overrides for <code>ApiController.PRIVATE_KEY</code> and <code>ApiController.PUBLIC_KEY</code> and/or passing an access token in for operations using OAuth.
     *
     * @return      a Post object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public Post update(Authentication auth)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = this.executeOperation(auth, "update", this);
        this.putAll(object);
        return this;
    }

    
    
    
    // id:(query:, param:true, header:, cookie:, body:)
    
    
    
    
    /**
     * Deletes an <code>Post</code> object.
     *
     * @return      a Post object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public Post delete()
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = this.executeOperation(null, "delete", this);
        this.clear();
        this.putAll(object);
        return this;
    }

    /**
     * Deletes an <code>Post</code> object.
     *
     * @return      a Post object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public Post delete(Authentication auth)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = this.executeOperation(auth, "delete", this);
        this.clear();
        this.putAll(object);
        return this;
    }

    /**
     * Deletes an <code>Post</code> object using the static properties <code>ApiController.PUBLIC_KEY</code> and
     * <code>ApiController.PRIVATE_KEY</code> as the default public and private API keys respectively.
     *
     * @param       id  the id of the object to delete
     *
     * @return      a Post object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static Post delete(String id)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return delete(null, id);
    }

    /**
     * Deletes an <code>Post</code> object
     * @param       auth Authentication object representing overrides for <code>ApiController.PRIVATE_KEY</code> and <code>ApiController.PUBLIC_KEY</code> and/or passing an access token in for operations using OAuth.
     * @param       id  the id of the object to delete
     *
     * @return      a Post object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static Post delete(Authentication auth, String id)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
        MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        Post object = new Post(new RequestMap("id", id));
        return object.delete(auth);
    }
    
    
}


