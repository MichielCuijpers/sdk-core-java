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
import java.util.HashMap;
import java.util.Map;



public class User extends BaseObject {

    private static Map<String, OperationConfig> operationConfigs;

    static {
        operationConfigs = new HashMap<>();

        operationConfigs.put("4f7d3051-9774-4df1-b826-2a275e42e169", new OperationConfig("/mock_crud_server/users", Action.list, Arrays.asList(""), Arrays.asList("")));
        operationConfigs.put("c749141f-fa78-4bcd-b63c-5a499c40ddac", new OperationConfig("/mock_crud_server/users", Action.create, Arrays.asList(""), Arrays.asList("")));
        operationConfigs.put("14665be1-d9ff-4d1b-be26-44c913acbf05", new OperationConfig("/mock_crud_server/users/{id}", Action.read, Arrays.asList(""), Arrays.asList("")));
        operationConfigs.put("32b223c4-165d-4f56-a199-97b38c4741c1", new OperationConfig("/mock_crud_server/users/{id}", Action.update, Arrays.asList(""), Arrays.asList("")));
        operationConfigs.put("8bc7d3c4-fbe9-4f10-8c57-2734da24cd71", new OperationConfig("/mock_crud_server/users/{id}", Action.delete, Arrays.asList(""), Arrays.asList("")));
        operationConfigs.put("ec489c56-f8e7-470e-b9c0-74836b69c5ed", new OperationConfig("/mock_crud_server/users200/{id}", Action.delete, Arrays.asList(""), Arrays.asList("")));
        operationConfigs.put("efa98230-cc3d-4e87-a964-dd08d2819273", new OperationConfig("/mock_crud_server/users204/{id}", Action.delete, Arrays.asList(""), Arrays.asList("")));


    }

    public User() {
    }

    public User(BaseObject o) {
        putAll(o);
    }

    public User(RequestMap requestMap) {
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
        return new OperationMetadata(SDKConfig.getVersion(), SDKConfig.getHost());
    }





    /**
     * Retrieve a list of <code>User</code> objects
     *
     * @return      a ResourceList<User> object which holds the list of User objects and the total
     *              number of User objects available.
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
    public static ResourceList<User> list()
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.executeListOperation(null, "4f7d3051-9774-4df1-b826-2a275e42e169", new User(), null);
    }

    /**
     * Retrieve a list of <code>User</code> objects
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     *
     * @return      a ResourceList<User> object which holds the list of User objects and the total
     *              number of User objects available.
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
    public static ResourceList<User> list(Authentication auth)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.executeListOperation(auth, "4f7d3051-9774-4df1-b826-2a275e42e169", new User(), null);
    }

    /**
     * Retrieve a list of <code>User</code> objects
     *
     * @param       criteria a map of additional criteria parameters
     *
     * @return      a ResourceList<User> object which holds the list of User objects based on the
     *              <code>criteria</code> provided  and the total number of User objects available.
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
    public static ResourceList<User> list(RequestMap criteria)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.executeListOperation(null, "4f7d3051-9774-4df1-b826-2a275e42e169", new User(), criteria);
    }

    /**
     * Retrieve a list of <code>User</code> objects
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     * @param       criteria a map of additional criteria parameters
     *
     * @return      a ResourceList<User> object which holds the list of User objects based on the
     *              <code>criteria</code> provided and the total number of User objects available.
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
    public static ResourceList<User> list(Authentication auth, RequestMap criteria)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.executeListOperation(auth, "4f7d3051-9774-4df1-b826-2a275e42e169", new User(), criteria);
    }







    /**
     * Creates a <code>User</code> object
     *
     * @param       map a map of parameters to create a <code>User</code> object
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User create(RequestMap map)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return create(null, map);
    }

    /**
     * Creates a <code>User</code> object
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     * @param       map a map of parameters to create a <code>User</code> object
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User create(Authentication auth, RequestMap map)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return new User(BaseObject.executeOperation(auth, "c749141f-fa78-4bcd-b63c-5a499c40ddac", new User(map)));
    }












    /**
     * Retrieve a <code>User</code> object
     *
     * @param       id the id of the <code>User</code> object to retrieve
     *
     * @return      a User object
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User read(String id)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return read(null, id, null);
    }

    /**
     * Retrieve a <code>User</code> object
     *
     * @param       id the id of the <code>User</code> object to retrieve
     * @param       map a map of additional parameters
     *
     * @return      a User object
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User read(String id, RequestMap map)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return read(null, id, map);
    }

    /**
     * Retrieve a <code>User</code> object
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     * @param       id the id of the <code>User</code> object to retrieve
     *
     * @return      a User object
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User read(Authentication auth, String id)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return read(auth, id, null);
    }

    /**
     * Retrieve a <code>User</code> object
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     * @param       id the id of the <code>User</code> object to retrieve
     * @param       map a map of additional parameters
     *
     * @return      a User object
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User read(Authentication auth, String id, RequestMap map)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        User val = new User();
        if (id != null) val.put("id", id);
        if (map != null)  val.putAll(map);
        return new User(BaseObject.executeOperation(auth, "14665be1-d9ff-4d1b-be26-44c913acbf05", val));
    }





    /**
     * Update a <code>User</code> object.
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public User update()
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = BaseObject.executeOperation(null, "32b223c4-165d-4f56-a199-97b38c4741c1", this);
        this.putAll(object);
        return this;
    }

    /**
     * Update a <code>User</code> object.
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public User update(Authentication auth)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = BaseObject.executeOperation(auth, "32b223c4-165d-4f56-a199-97b38c4741c1", this);
        this.putAll(object);
        return this;
    }










    /**
     * Deletes a <code>User</code> object.
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public User delete()
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = BaseObject.executeOperation(null, "8bc7d3c4-fbe9-4f10-8c57-2734da24cd71", this);
        this.clear();
        this.putAll(object);
        return this;
    }

    /**
     * Deletes a <code>User</code> object.
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public User delete(Authentication auth)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = BaseObject.executeOperation(auth, "8bc7d3c4-fbe9-4f10-8c57-2734da24cd71", this);
        this.clear();
        this.putAll(object);
        return this;
    }

    /**
     * Deletes a <code>User</code> object.
     *
     * @param       id the id of the object to delete
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User delete(String id)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return delete(null, id);
    }

    /**
     * Deletes a <code>User</code> object
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     * @param       id  the id of the object to delete
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User delete(Authentication auth, String id)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        User object = new User(new RequestMap("id", id));
        return object.delete(auth);
    }

    /**
     * Deletes a <code>User</code> object
     *
     * @param       id the id of the object to delete
     * @param       map a map of additional parameters
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User delete(String id, RequestMap map)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return delete(null, id, map);
    }

    /**
     * Deletes a <code>User</code> object
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     * @param       id  the id of the object to delete
     * @param       map a map of additional parameters
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User delete(Authentication auth, String id, RequestMap map)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        User object = new User(new RequestMap("id", id));
        if (map != null)  object.putAll(map);
        return object.delete(auth);
    }







    /**
     * Deletes a <code>User</code> object.
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public User delete200()
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = BaseObject.executeOperation(null, "ec489c56-f8e7-470e-b9c0-74836b69c5ed", this);
        this.clear();
        this.putAll(object);
        return this;
    }

    /**
     * Deletes a <code>User</code> object.
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public User delete200(Authentication auth)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = BaseObject.executeOperation(auth, "ec489c56-f8e7-470e-b9c0-74836b69c5ed", this);
        this.clear();
        this.putAll(object);
        return this;
    }

    /**
     * Deletes a <code>User</code> object.
     *
     * @param       id the id of the object to delete
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User delete200(String id)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return delete200(null, id);
    }

    /**
     * Deletes a <code>User</code> object
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     * @param       id  the id of the object to delete
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User delete200(Authentication auth, String id)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        User object = new User(new RequestMap("id", id));
        return object.delete200(auth);
    }

    /**
     * Deletes a <code>User</code> object
     *
     * @param       id the id of the object to delete
     * @param       map a map of additional parameters
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User delete200(String id, RequestMap map)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return delete200(null, id, map);
    }

    /**
     * Deletes a <code>User</code> object
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     * @param       id  the id of the object to delete
     * @param       map a map of additional parameters
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User delete200(Authentication auth, String id, RequestMap map)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        User object = new User(new RequestMap("id", id));
        if (map != null)  object.putAll(map);
        return object.delete200(auth);
    }







    /**
     * Deletes a <code>User</code> object.
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public User delete204()
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = BaseObject.executeOperation(null, "efa98230-cc3d-4e87-a964-dd08d2819273", this);
        this.clear();
        this.putAll(object);
        return this;
    }

    /**
     * Deletes a <code>User</code> object.
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public User delete204(Authentication auth)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = BaseObject.executeOperation(auth, "efa98230-cc3d-4e87-a964-dd08d2819273", this);
        this.clear();
        this.putAll(object);
        return this;
    }

    /**
     * Deletes a <code>User</code> object.
     *
     * @param       id the id of the object to delete
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User delete204(String id)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return delete204(null, id);
    }

    /**
     * Deletes a <code>User</code> object
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     * @param       id  the id of the object to delete
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User delete204(Authentication auth, String id)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        User object = new User(new RequestMap("id", id));
        return object.delete204(auth);
    }

    /**
     * Deletes a <code>User</code> object
     *
     * @param       id the id of the object to delete
     * @param       map a map of additional parameters
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User delete204(String id, RequestMap map)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return delete204(null, id, map);
    }

    /**
     * Deletes a <code>User</code> object
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     * @param       id  the id of the object to delete
     * @param       map a map of additional parameters
     *
     * @return      a User object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static User delete204(Authentication auth, String id, RequestMap map)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        User object = new User(new RequestMap("id", id));
        if (map != null)  object.putAll(map);
        return object.delete204(auth);
    }
    
    
}


