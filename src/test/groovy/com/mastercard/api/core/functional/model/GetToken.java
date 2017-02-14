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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetToken extends BaseObject  {

	private static Map<String, OperationConfig> operationConfigs;

	static {
		operationConfigs = new HashMap<String, OperationConfig>();
		operationConfigs.put("6803bca2-d0e5-41d2-909e-3d6c457550c6", new OperationConfig("/mdes/digitization/#env/1/0/getToken", Action.create, Arrays.asList(""), Arrays.asList("")));
	}

	public GetToken() {
	}

	public GetToken(BaseObject o) {
		putAll(o);
	}

	public GetToken(RequestMap requestMap) {
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
		return new OperationMetadata("0.0.1", "https://sandbox.api.mastercard.com", "static");
	}

	/**
	 * Creates a <code>GetToken</code> object
	 *
	 * @param       map a map of parameters to create a <code>GetToken</code> object
	 *
	 * @return      a GetToken object.
	 *
	 * @throws      ApiException - which encapsulates the http status code and the error return by the server
	 */
	public static GetToken create(RequestMap map)
		throws ApiException {

		return create(null, map);
	}

	/**
	 * Creates a <code>GetToken</code> object
	 *
	 * @param       auth Authentication object overriding <code>ApiConfig.setAuthentication(authentication)</code>
	 * @param       map a map of parameters to create a <code>GetToken</code> object
	 *
	 * @return      a GetToken object.
	 *
	 * @throws      ApiException - which encapsulates the http status code and the error return by the server
	 */
	public static GetToken create(Authentication auth, RequestMap map)
		throws ApiException {

		return new GetToken(BaseObject.executeOperation(auth, "6803bca2-d0e5-41d2-909e-3d6c457550c6", new GetToken(map)));
	}






}


