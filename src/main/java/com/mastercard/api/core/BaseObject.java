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

package com.mastercard.api.core;

import com.mastercard.api.core.exception.*;
import com.mastercard.api.core.model.ResourceList;
import com.mastercard.api.core.security.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public abstract class BaseObject extends RequestMap {

    private static final ApiControllerFactory apiControllerFactory = new ApiControllerFactory();

    protected abstract String getBasePath();

    protected abstract String getObjectType();

    protected abstract List<String> getHeaderParams();

    protected static BaseObject readObject(final Authentication authentication, final String type,
            final BaseObject value)
            throws ApiCommunicationException, AuthenticationException, ObjectNotFoundException,
            InvalidRequestException, NotAllowedException, SystemException, MessageSignerException {

        ApiController apiController = apiControllerFactory.createApiController(value.getBasePath());

        Map<? extends String, ? extends Object> response = apiController
                .execute(authentication, type, "read", value, value.getHeaderParams());

        BaseObject requestObject = createResponseBaseObject(value);
        requestObject.putAll(response);

        return requestObject;
    }

    protected static <T extends BaseObject> ResourceList<T> listObjects(final Authentication authentication,
            T template) throws ApiCommunicationException, AuthenticationException, ObjectNotFoundException,
            InvalidRequestException, NotAllowedException, SystemException, MessageSignerException {

        return listObjects(authentication, template, null);
    }

    protected static <T extends BaseObject> ResourceList<T> listObjects(final Authentication authentication,
            T template, Map criteria)
            throws ApiCommunicationException, AuthenticationException, ObjectNotFoundException,
            InvalidRequestException, NotAllowedException, SystemException, MessageSignerException {

        ResourceList<T> listResults = new ResourceList<T>();

        Map<? extends String, ? extends Object> response = apiControllerFactory
                .createApiController(template.getBasePath())
                .execute(authentication, template.getObjectType(), "list", criteria, new ArrayList<String>());

        listResults.putAll(response);

        List<Map<String, Object>> rawList = (List<Map<String, Object>>) listResults.get("list");

        List<T> val = new ArrayList<T>(((List) rawList).size());

        for (Object o : (List) rawList) {
            if (o instanceof Map) {
                T item = (T) template.clone();
                item.putAll((Map<? extends String, ? extends Object>) o);
                val.add(item);
            }
        }

        listResults.put("list", val);

        return listResults;
    }

    protected static BaseObject createObject(final Authentication authentication,
            final BaseObject requestObject)
            throws ApiCommunicationException, AuthenticationException, ObjectNotFoundException,
            InvalidRequestException, NotAllowedException, SystemException, MessageSignerException {

        return execute(authentication, requestObject.getObjectType(), "create", requestObject);
    }

    protected BaseObject updateObject(final BaseObject requestObject)
            throws ApiCommunicationException, AuthenticationException, ObjectNotFoundException,
            InvalidRequestException, NotAllowedException, SystemException, MessageSignerException {

        return updateObject(null, requestObject);
    }

    protected BaseObject updateObject(final Authentication authentication, final BaseObject requestObject)
            throws ApiCommunicationException, AuthenticationException, ObjectNotFoundException,
            InvalidRequestException, NotAllowedException, SystemException, MessageSignerException {

        return execute(authentication, requestObject.getObjectType(), "update", requestObject);
    }

    protected BaseObject deleteObject(final BaseObject requestObject)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            ObjectNotFoundException, NotAllowedException, SystemException, MessageSignerException {

        return deleteObject(null, requestObject);
    }

    protected BaseObject deleteObject(final Authentication authentication, final BaseObject requestObject)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            ObjectNotFoundException, NotAllowedException, SystemException, MessageSignerException {

        return execute(authentication, getObjectType(), "delete", requestObject);
    }

    private static BaseObject execute(Authentication authentication, String objectType, String action,
            BaseObject requestObject)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            ObjectNotFoundException, NotAllowedException, SystemException, MessageSignerException {

        ApiController apiController = apiControllerFactory.createApiController(requestObject.getBasePath());

        Map<? extends String, ? extends Object> response = apiController
                .execute(authentication, objectType, action, requestObject, requestObject.getHeaderParams());

        BaseObject responseObject = createResponseBaseObject(requestObject);

        // Response can be null (204)
        if (response != null) {
            responseObject.putAll(response);
        }

        return responseObject;
    }

    /**
     * Create an instance of Base Object for Response
     *
     * @param bo
     * @return
     */
    private static BaseObject createResponseBaseObject(final BaseObject bo) {
        return new BaseObject() {
            @Override protected String getObjectType() {
                return bo.getObjectType();
            }

            @Override protected String getBasePath() {
                return bo.getBasePath();
            }

            @Override protected List<String> getHeaderParams() {
                return bo.getHeaderParams();
            }
        };
    }
}
