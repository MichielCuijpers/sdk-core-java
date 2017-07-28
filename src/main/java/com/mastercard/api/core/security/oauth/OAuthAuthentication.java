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

package com.mastercard.api.core.security.oauth;

import com.mastercard.api.core.exception.SdkException;
import com.mastercard.api.core.security.Authentication;
import com.mastercard.api.core.model.HttpMethod;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.http.HttpParameters;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Implementation of OAuth security
 */
public class OAuthAuthentication implements Authentication {

    private String consumerKey;
    private PrivateKey privateKey;

    /**
     *
     * @param consumerKey consumer key for your project
     * @param is InputStream for the .p12 private key file
     * @param alias alias of the private key
     * @param password password for private key
     *
     */
    public OAuthAuthentication(String consumerKey, InputStream is, String alias, String password) throws SdkException {
        if(consumerKey == null) {
            throw new SdkException("ConsumerKey cannot null");
        }

        if(is == null) {
            throw new SdkException("InputStream cannot null");
        }

        this.consumerKey = consumerKey;
        setP12(is, alias, password);
    }



    /**
     *
     * @param consumerKey consumer key for your project
     * @param privateKey PrivateKey loaded from whatever
     *
     */
    public OAuthAuthentication(String consumerKey, PrivateKey privateKey) throws SdkException {
        if(consumerKey == null) {
            throw new SdkException("ConsumerKey cannot null");
        }

        if(privateKey == null) {
            throw new SdkException("PrivateKet cannot null");
        }

        this.consumerKey = consumerKey;
        this.privateKey = privateKey;
    }

    private void setP12(InputStream is, String alias, String password) throws SdkException {
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(is, password.toCharArray());
            this.privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
            if (this.privateKey == null) {
                throw new SdkException("No key found for alias ["+ alias +"]");
            }
        } catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }

    }

    @Override
    public HttpRequestBase sign(URI uri, HttpMethod httpMethod, ContentType contentType, Object body, HttpRequestBase message) throws SdkException {
        // Set the OAuthRequest
        OAuthRequest request = new OAuthRequest();
        request.setMethod(httpMethod);
        request.setRequestUrl(uri.toString());
        request.setContentType(contentType);

        // Set the additional OAuth Parameters
        HttpParameters params = new HttpParameters();
        if (body != null) {
            try {
                request.setBody((String) body);
                params.put(OAuthConstants.OAUTH_BODY_HASH, request.getOauthBodyHash(), true);
            }
            catch (Exception e) {
                throw new SdkException(e.getMessage(), e);
            }
        }


        // Create Signer
        OAuthSigner oAuthSigner = new OAuthSigner(privateKey);

        // Create CustomOAuthConsumer
        OAuthConsumer oAuthConsumer = new CustomOAuthConsumer(consumerKey, "");
        oAuthConsumer.setMessageSigner(oAuthSigner);
        oAuthConsumer.setAdditionalParameters(params);

        try {
            oAuthConsumer.sign(request);
        }
        catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }

        // Set the oauth authorization header
        String authorization = request.getHeader(OAuth.HTTP_AUTHORIZATION_HEADER);
        message.setHeader(OAuth.HTTP_AUTHORIZATION_HEADER, authorization);

        return message;
    }
}
