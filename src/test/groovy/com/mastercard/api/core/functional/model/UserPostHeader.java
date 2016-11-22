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
import java.util.HashMap;
import java.util.Map;



public class UserPostHeader extends BaseObject {

    private static Map<String, OperationConfig> operationConfigs;
    private static SDKConfig config = new SDKConfig();

    static {
        operationConfigs = new HashMap<>();

        operationConfigs.put("378b52fa-47e7-4acc-b414-68fb2604ab39", new OperationConfig("/mock_crud_server/users/posts", Action.list, Arrays.asList(""), Arrays.asList("user_id")));


    }

    public UserPostHeader() {
    }

    public UserPostHeader(BaseObject o) {
        putAll(o);
    }

    public UserPostHeader(RequestMap requestMap) {
        putAll(requestMap);
    }

    @Override protected final OperationConfig getOperationConfig(String operationUUID) throws IllegalArgumentException{
        OperationConfig operationConfig = operationConfigs.get(operationUUID);

        if(operationConfig == null) {
            throw new IllegalArgumentException("Invalid operationUUID supplied: " + operationUUID);
        }

        return operationConfig;
    }

    @Override protected OperationMetadata getOperationMetadata() throws IllegalArgumentException {
        return new OperationMetadata(config.getVersion(), config.getHost(), config.getContext());
    }





    /**
     * Retrieve a list of <code>UserPostHeader</code> objects
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

        return BaseObject.executeListOperation(null, "378b52fa-47e7-4acc-b414-68fb2604ab39", new UserPostHeader(), null);
    }

    /**
     * Retrieve a list of <code>UserPostHeader</code> objects
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
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

        return BaseObject.executeListOperation(auth, "378b52fa-47e7-4acc-b414-68fb2604ab39", new UserPostHeader(), null);
    }

    /**
     * Retrieve a list of <code>UserPostHeader</code> objects
     *
     * @param       criteria a map of additional criteria parameters
     *
     * @return      a ResourceList<UserPostHeader> object which holds the list of UserPostHeader objects based on the
     *              <code>criteria</code> provided  and the total number of UserPostHeader objects available.
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
    public static ResourceList<UserPostHeader> list(RequestMap criteria)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.executeListOperation(null, "378b52fa-47e7-4acc-b414-68fb2604ab39", new UserPostHeader(), criteria);
    }

    /**
     * Retrieve a list of <code>UserPostHeader</code> objects
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     * @param       criteria a map of additional criteria parameters
     *
     * @return      a ResourceList<UserPostHeader> object which holds the list of UserPostHeader objects based on the
     *              <code>criteria</code> provided and the total number of UserPostHeader objects available.
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
    public static ResourceList<UserPostHeader> list(Authentication auth, RequestMap criteria)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.executeListOperation(auth, "378b52fa-47e7-4acc-b414-68fb2604ab39", new UserPostHeader(), criteria);
    }


    
    
    
}


