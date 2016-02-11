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


public abstract class BaseObject extends BaseMap {

    protected Authentication authentication;

    protected static BaseObject findObject(final Authentication authentication, final String type, BaseObject value)
            throws ApiCommunicationException, AuthenticationException, ObjectNotFoundException,
            InvalidRequestException, NotAllowedException, SystemException, MessageSignerException {

        ApiController apiController = new ApiController();

        Map<? extends String, ? extends Object> response = apiController.execute(authentication, type, "show", value);

        BaseObject paymentsObject = new BaseObject() {
            @Override
            protected String getObjectType() {
                return type;
            }
        };

        paymentsObject.authentication = authentication;
        paymentsObject.putAll(response);

        return paymentsObject;
    }

    protected static <T extends BaseObject> ResourceList<T> listObjects(final Authentication authentication, T template)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            NotAllowedException, SystemException, MessageSignerException {
        return listObjects(authentication, template, null);
    }

    protected static <T extends BaseObject> ResourceList<T> listObjects(final Authentication authentication, T template, Map criteria)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            NotAllowedException, SystemException, MessageSignerException {

        ResourceList<T> listResults = new ResourceList<T>();

        try {
            Map<? extends String, ? extends Object> response = new ApiController().execute(authentication, template.getObjectType(), "list", criteria);
            listResults.putAll(response);


            List<T> val = null;
            if (listResults.containsKey("list")) {
                List<Map<String, Object>> rawList = (List<Map<String, Object>>) listResults.get("list");

                val = new ArrayList<T>(((List) rawList).size());
                for (Object o : (List) rawList) {
                    if (o instanceof Map) {
                        T item = (T) template.clone();
                        item.authentication = authentication;
                        item.putAll((Map<? extends String, ? extends Object>) o);
                        val.add(item);
                    }
                }
            } else {
                val = new ArrayList<T>();
            }
            listResults.put("list", val);
            return listResults;

        } catch (ObjectNotFoundException e) {
            throw new IllegalStateException("ObjectNotFoundException not expected", e);
        }
    }

    protected static BaseObject createObject(final Authentication authentication, final BaseObject paymentsObject)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            NotAllowedException, SystemException, MessageSignerException {

        ApiController apiController = new ApiController();

        paymentsObject.authentication = authentication;

        try {
            Map<? extends String, ? extends Object> response = apiController.execute(authentication, paymentsObject.getObjectType(), "create", paymentsObject);
            BaseObject object = new BaseObject() {
                @Override
                protected String getObjectType() {
                    return paymentsObject.getObjectType();
                }
            };

            object.authentication = authentication;

            // Response can be null (204)
            if (response != null) {
                object.putAll(response);
            }

            return object;

        } catch (ObjectNotFoundException e) {
            throw new IllegalStateException("ObjectNotFoundException not expected", e);
        }
    }

    protected abstract String getObjectType();

    public Authentication getAuthentication() {
        return authentication;
    }

    protected BaseObject updateObject(final BaseObject paymentsObject)
            throws ApiCommunicationException, AuthenticationException, ObjectNotFoundException,
            InvalidRequestException, NotAllowedException, SystemException, MessageSignerException {

        ApiController apiController = new ApiController();

        Map<? extends String, ? extends Object> response = apiController.execute(authentication, paymentsObject.getObjectType(), "update", paymentsObject);
        BaseObject object = new BaseObject() {
            @Override
            protected String getObjectType() {
                return paymentsObject.getObjectType();
            }
        };

        // Response can be null (204)
        if (response != null) {
            object.putAll(response);
        }

        return object;
    }

    protected BaseObject updateObject(final Authentication authentication, final BaseObject paymentsObject)
            throws ApiCommunicationException, AuthenticationException, ObjectNotFoundException,
            InvalidRequestException, NotAllowedException, SystemException, MessageSignerException {

        ApiController apiController = new ApiController();

        paymentsObject.authentication = authentication;

        Map<? extends String, ? extends Object> response = apiController.execute(authentication, paymentsObject.getObjectType(), "update", paymentsObject);
        BaseObject object = new BaseObject() {
            @Override
            protected String getObjectType() {
                return paymentsObject.getObjectType();
            }
        };

        // Response can be null (204)
        if (response != null) {
            object.putAll(response);
        }

        return object;
    }

    protected BaseObject deleteObject(final BaseObject paymentsObject)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            ObjectNotFoundException, NotAllowedException, SystemException, MessageSignerException {

        ApiController apiController = new ApiController();
        Map<? extends String, ? extends Object> response = apiController.execute(authentication, getObjectType(), "delete", paymentsObject);

        BaseObject object = new BaseObject() {
            @Override
            protected String getObjectType() {
                return paymentsObject.getObjectType();
            }
        };
        // Response can be null (204)
        if (response != null) {
            object.putAll(response);
        }

        return object;
    }

    protected BaseObject deleteObject(final Authentication authentication, final BaseObject paymentsObject)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            ObjectNotFoundException, NotAllowedException, SystemException, MessageSignerException {

        ApiController apiController = new ApiController();
        Map<? extends String, ? extends Object> response = apiController.execute(authentication, getObjectType(), "delete", paymentsObject);

        BaseObject object = new BaseObject() {
            @Override
            protected String getObjectType() {
                return paymentsObject.getObjectType();
            }
        };

        // Response can be null (204)
        if (response != null) {
            object.putAll(response);
        }

        return object;
    }
}
