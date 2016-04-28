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

import com.mastercard.api.core.exception.MessageSignerException;
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

public class OAuthAuthentication implements Authentication {

    private String clientId;
    private PrivateKey privateKey;

    public OAuthAuthentication(String clientId, InputStream is, String alias, String password) throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        if(clientId == null) {
            throw new IllegalArgumentException("ClientId cannot null");
        }

        if(is == null) {
            throw new IllegalArgumentException("InputStream cannot null");
        }

        this.clientId = clientId;
        setP12(is, alias, password);
    }

    private void setP12(InputStream is, String alias, String password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(is, password.toCharArray());
        this.privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());

        if (this.privateKey == null) {
            throw new IllegalArgumentException("No key found for alias ["+ alias +"]");
        }
    }

    @Override
    public HttpRequestBase sign(URI uri, HttpMethod httpMethod, ContentType contentType, Object body, HttpRequestBase message) throws MessageSignerException {
        // Set the OAuthRequest
        OAuthRequest request = new OAuthRequest();
        request.setMethod(httpMethod);
        request.setRequestUrl(uri.toString());
        request.setContentType(contentType);
        request.setBody((String) body);

        // Set the additional OAuth Parameters
        HttpParameters params = new HttpParameters();
        try {
            params.put(OAuthConstants.OAUTH_BODY_HASH, request.getOauthBodyHash(), true);
        }
        catch (Exception e) {
            throw new MessageSignerException(e);
        }

        // Create Signer
        OAuthSigner oAuthSigner = new OAuthSigner(privateKey);

        // Create OAuthConsumer
        OAuthConsumer oAuthConsumer = new DefaultOAuthConsumer(clientId, "");
        oAuthConsumer.setMessageSigner(oAuthSigner);
        oAuthConsumer.setAdditionalParameters(params);

        try {
            oAuthConsumer.sign(request);
        }
        catch (Exception e) {
            throw new MessageSignerException(e);
        }

        // Set the oauth authorization header
        String authorization = request.getHeader(OAuth.HTTP_AUTHORIZATION_HEADER);
        message.setHeader(OAuth.HTTP_AUTHORIZATION_HEADER, authorization);

        return message;
    }
}
