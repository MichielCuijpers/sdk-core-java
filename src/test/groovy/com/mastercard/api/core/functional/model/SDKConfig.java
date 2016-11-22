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

import com.mastercard.api.core.model.Environment;
import com.mastercard.api.core.model.SDKConfigInterface;

import java.util.HashMap;
import java.util.Map;

public class SDKConfig implements SDKConfigInterface {

    private String override = "http://localhost:8081";
    private String host = null;
    private String context = null;

    private static final Map<Environment,String[]> environmentMap = new HashMap() {{
        this.put(Environment.PRODUCTION, new String[] { "https://api.mastercard.com", null});
        this.put(Environment.SANDBOX, new String[] { "https://sandbox.api.mastercard.com", null});
        this.put(Environment.STAGE, new String[] { "https://stage.api.mastercard.com", null});
        this.put(Environment.DEV, new String[] { "https://dev.api.mastercard.com", null});
        this.put(Environment.MTF, new String[] { "https://sandbox.api.mastercard.com", "mtf"});
        this.put(Environment.ITF, new String[] { "https://sandbox.api.mastercard.com", "itf"});
    }};


    public String getContext() {
        return context;
    }

    public String getHost() {
        return  (override!= null) ? override : host;
    }

    public String getVersion() {
        return "0.0.1";
    }

    @Override
    public void setEnvironment(Environment environment) {
        if (environmentMap.containsKey(environment)) {
            String[] config = environmentMap.get(environment);
            this.host = config[0];
            this.context = config[1];
        } else {
            throw new RuntimeException("Environment: "+environment.name()+" not found for sdk:"+this.getClass().getName());
        }
    }

    @Override
    public void setEnvironment(String host, String context) {
        this.context = context;
        this.host = host;
    }


    //this is only used for testing.
    public void clearOverride() {
        override = null;
    }
}