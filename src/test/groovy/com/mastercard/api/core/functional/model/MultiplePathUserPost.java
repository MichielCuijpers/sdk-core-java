package com.mastercard.api.core.functional.model;

/**
 * Created by e049519 on 11/1/16.
 */import com.mastercard.api.core.exception.*;
import com.mastercard.api.core.model.*;
import com.mastercard.api.core.security.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiplePathUserPost extends BaseObject  {

    private static Map<String, OperationConfig> operationConfigs;
    private static SDKConfig config = new SDKConfig();

    static {
        operationConfigs = new HashMap<>();

        operationConfigs.put("38074ade-a5e8-4e62-9e6a-4a71726470a6", new OperationConfig("/mock_crud_server/users/{user_id}/post/{post_id}", Action.list, Arrays.asList(""), Arrays.asList("")));
        operationConfigs.put("ff2f8c50-03d4-48cd-aed2-6eb2d2023e42", new OperationConfig("/mock_crud_server/users/{user_id}/post/{post_id}", Action.update, Arrays.asList("testQuery"), Arrays.asList("")));
        operationConfigs.put("69c50952-b9a1-4d1d-a6eb-84e2dc3a09a5", new OperationConfig("/mock_crud_server/users/{user_id}/post/{post_id}", Action.delete, Arrays.asList(""), Arrays.asList("")));


    }

    public MultiplePathUserPost() {
    }

    public MultiplePathUserPost(BaseObject o) {
        putAll(o);
    }

    public MultiplePathUserPost(RequestMap requestMap) {
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
     * Retrieve a list of <code>MultiplePathUserPost</code> objects
     *
     * @return      a ResourceList<MultiplePathUserPost> object which holds the list of MultiplePathUserPost objects and the total
     *              number of MultiplePathUserPost objects available.
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
    public static ResourceList<MultiplePathUserPost> list()
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.executeListOperation(null, "38074ade-a5e8-4e62-9e6a-4a71726470a6", new MultiplePathUserPost(), null);
    }

    /**
     * Retrieve a list of <code>MultiplePathUserPost</code> objects
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     *
     * @return      a ResourceList<MultiplePathUserPost> object which holds the list of MultiplePathUserPost objects and the total
     *              number of MultiplePathUserPost objects available.
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
    public static ResourceList<MultiplePathUserPost> list(Authentication auth)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.executeListOperation(auth, "38074ade-a5e8-4e62-9e6a-4a71726470a6", new MultiplePathUserPost(), null);
    }

    /**
     * Retrieve a list of <code>MultiplePathUserPost</code> objects
     *
     * @param       criteria a map of additional criteria parameters
     *
     * @return      a ResourceList<MultiplePathUserPost> object which holds the list of MultiplePathUserPost objects based on the
     *              <code>criteria</code> provided  and the total number of MultiplePathUserPost objects available.
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
    public static ResourceList<MultiplePathUserPost> list(RequestMap criteria)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.executeListOperation(null, "38074ade-a5e8-4e62-9e6a-4a71726470a6", new MultiplePathUserPost(), criteria);
    }

    /**
     * Retrieve a list of <code>MultiplePathUserPost</code> objects
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     * @param       criteria a map of additional criteria parameters
     *
     * @return      a ResourceList<MultiplePathUserPost> object which holds the list of MultiplePathUserPost objects based on the
     *              <code>criteria</code> provided and the total number of MultiplePathUserPost objects available.
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
    public static ResourceList<MultiplePathUserPost> list(Authentication auth, RequestMap criteria)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return BaseObject.executeListOperation(auth, "38074ade-a5e8-4e62-9e6a-4a71726470a6", new MultiplePathUserPost(), criteria);
    }








    /**
     * Update a <code>MultiplePathUserPost</code> object.
     *
     * @return      a MultiplePathUserPost object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public MultiplePathUserPost update()
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = BaseObject.executeOperation(null, "ff2f8c50-03d4-48cd-aed2-6eb2d2023e42", this);
        this.putAll(object);
        return this;
    }

    /**
     * Update a <code>MultiplePathUserPost</code> object.
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     *
     * @return      a MultiplePathUserPost object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public MultiplePathUserPost update(Authentication auth)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = BaseObject.executeOperation(auth, "ff2f8c50-03d4-48cd-aed2-6eb2d2023e42", this);
        this.putAll(object);
        return this;
    }










    /**
     * Deletes a <code>MultiplePathUserPost</code> object.
     *
     * @return      a MultiplePathUserPost object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public MultiplePathUserPost delete()
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = BaseObject.executeOperation(null, "69c50952-b9a1-4d1d-a6eb-84e2dc3a09a5", this);
        this.clear();
        this.putAll(object);
        return this;
    }

    /**
     * Deletes a <code>MultiplePathUserPost</code> object.
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     *
     * @return      a MultiplePathUserPost object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public MultiplePathUserPost delete(Authentication auth)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        BaseObject object = BaseObject.executeOperation(auth, "69c50952-b9a1-4d1d-a6eb-84e2dc3a09a5", this);
        this.clear();
        this.putAll(object);
        return this;
    }

    /**
     * Deletes a <code>MultiplePathUserPost</code> object.
     *
     * @param       id the id of the object to delete
     *
     * @return      a MultiplePathUserPost object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static MultiplePathUserPost delete(String id)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return delete(null, id);
    }

    /**
     * Deletes a <code>MultiplePathUserPost</code> object
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     * @param       id  the id of the object to delete
     *
     * @return      a MultiplePathUserPost object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static MultiplePathUserPost delete(Authentication auth, String id)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        MultiplePathUserPost object = new MultiplePathUserPost(new RequestMap("id", id));
        return object.delete(auth);
    }

    /**
     * Deletes a <code>MultiplePathUserPost</code> object
     *
     * @param       id the id of the object to delete
     * @param       map a map of additional parameters
     *
     * @return      a MultiplePathUserPost object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static MultiplePathUserPost delete(String id, RequestMap map)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return delete(null, id, map);
    }

    /**
     * Deletes a <code>MultiplePathUserPost</code> object
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     * @param       id  the id of the object to delete
     * @param       map a map of additional parameters
     *
     * @return      a MultiplePathUserPost object.
     *
     * @throws      ApiCommunicationException
     * @throws      AuthenticationException
     * @throws      InvalidRequestException
     * @throws      MessageSignerException
     * @throws      NotAllowedException
     * @throws      ObjectNotFoundException
     * @throws      SystemException
     */
    public static MultiplePathUserPost delete(Authentication auth, String id, RequestMap map)
            throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        MultiplePathUserPost object = new MultiplePathUserPost(new RequestMap("id", id));
        if (map != null)  object.putAll(map);
        return object.delete(auth);
    }



}


