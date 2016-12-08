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

public class ActivateStatementCreditOffer extends BaseObject {

    private static Map<String, OperationConfig> operationConfigs;

    static {
        operationConfigs = new HashMap<String, OperationConfig>();
        
        operationConfigs.put("c2f9d3fa-0733-48eb-8ba2-615cd87a11b5", new OperationConfig("/plo/v1/activatestatementcreditoffer", Action.create, Arrays.asList("FId","UserToken","OfferId","RedemptionMode"), Arrays.asList("")));
        
        
    }

    public ActivateStatementCreditOffer() {
    }

    public ActivateStatementCreditOffer(BaseObject o) {
        putAll(o);
    }

    public ActivateStatementCreditOffer(RequestMap requestMap) {
        putAll(requestMap);
    }

    @Override
    protected final OperationConfig getOperationConfig(String operationUUID) throws IllegalArgumentException {
        OperationConfig operationConfig = operationConfigs.get(operationUUID);

        if(operationConfig == null) {
            throw new IllegalArgumentException("Invalid operationUUID supplied: " + operationUUID);
        }

        return operationConfig;
    }

    @Override
    protected OperationMetadata getOperationMetadata() throws IllegalArgumentException {
        return new OperationMetadata("0.0.1", "https://sandbox.api.mastercard.com");
    }

    
    
    /**
     * Creates a <code>ActivateStatementCreditOffer</code> object
     *
     * @param       map a map of parameters to create a <code>ActivateStatementCreditOffer</code> object
     *
     * @return      a ActivateStatementCreditOffer object.
     *
     * @throws ApiCommunicationException
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws MessageSignerException
     * @throws NotAllowedException
     * @throws ObjectNotFoundException
     * @throws SystemException
     */
    public static ActivateStatementCreditOffer activateCredit(RequestMap map)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return activateCredit(null, map);
    }

    /**
     * Creates a <code>ActivateStatementCreditOffer</code> object
     *
     * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
     * @param       map a map of parameters to create a <code>ActivateStatementCreditOffer</code> object
     *
     * @return      a ActivateStatementCreditOffer object.
     *
     * @throws ApiCommunicationException
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws MessageSignerException
     * @throws NotAllowedException
     * @throws ObjectNotFoundException
     * @throws SystemException
     */
    public static ActivateStatementCreditOffer activateCredit(Authentication auth, RequestMap map)
        throws ApiCommunicationException, AuthenticationException, InvalidRequestException,
            MessageSignerException, NotAllowedException, ObjectNotFoundException, SystemException {

        return new ActivateStatementCreditOffer(BaseObject.executeOperation(auth, "c2f9d3fa-0733-48eb-8ba2-615cd87a11b5", new ActivateStatementCreditOffer(map)));
    }

    
    
    
    
    
    
}


