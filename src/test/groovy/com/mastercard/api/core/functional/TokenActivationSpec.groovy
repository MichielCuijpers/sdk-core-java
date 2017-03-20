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

package com.mastercard.api.core.functional
import com.mastercard.api.core.ApiConfig
import com.mastercard.api.core.functional.model.Tokenize
import com.mastercard.api.core.model.RequestMap
import com.mastercard.api.core.security.Authentication
import com.mastercard.api.core.security.CryptographyInterceptor
import com.mastercard.api.core.security.mdes.MDESCryptography
import com.mastercard.api.core.security.oauth.OAuthAuthentication
import spock.lang.Specification


public class TokenActivationSpec extends Specification {


    public static final String consumerKey = "L5BsiPgaF-O3qA36znUATgQXwJB6MRoMSdhjd7wt50c97279!50596e52466e3966546d434b7354584c4975693238513d3d";

    def setupSpec() {
        ApiConfig.setDebug(true);
        ApiConfig.setSandbox(true);

        try {
            InputStream is = new FileInputStream("src/test/resources/mcapi_sandbox_key.p12");
            Authentication authentication = new OAuthAuthentication(consumerKey, is, "test", "password");
            ApiConfig.setAuthentication(authentication);


            //ApiConfig.addCryptographyInterceptor(new MDES())
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    def 'send tokenization request (orginal keys)'() {
        setup:
        InputStream is2 = new FileInputStream("src/test/resources/mastercard_public.crt");
        InputStream is3 = new FileInputStream("src/test/resources/mastercard_private.key");
        CryptographyInterceptor interceptor = new MDESCryptography(is2, is3);
        ApiConfig.addCryptographyInterceptor(interceptor);

        when:

        RequestMap requestMap = new RequestMap();
        requestMap.set("tokenRequestorId", "12345678901" );
        requestMap.set("requestId", "123456");
        requestMap.set("tokenType", "CLOUD");
        requestMap.set("taskId", "123456");

        requestMap.set("cardInfo.encryptedData.source", "CARD_ON_FILE");
        requestMap.set("cardInfo.encryptedData.accountNumber", "5123456789012345");
        requestMap.set("cardInfo.encryptedData.expiryMonth", "12");
        requestMap.set("cardInfo.encryptedData.expiryYear", "18");
        requestMap.set("cardInfo.encryptedData.securityCode", "123");
        requestMap.set("cardInfo.encryptedData.billingAddress.line", "100 1st Street");
        requestMap.set("cardInfo.encryptedData.billingAddress.line2", "Apt. 4B");
        requestMap.set("cardInfo.encryptedData.billingAddress.city", "St. Louis");
        requestMap.set("cardInfo.encryptedData.billingAddress.countrySubdivision", "MO");
        requestMap.set("cardInfo.encryptedData.billingAddress.postalCode", "61000");
        requestMap.set("cardInfo.encryptedData.billingAddress.country", "USA");

        Tokenize response = new Tokenize(requestMap).create(requestMap);

        then:
        response.get("decision").toString().equalsIgnoreCase("APPROVED")
        response.get("responseId").toString().equalsIgnoreCase("123456")
        response.get("tokenInfo.accountPanSuffix").toString().equalsIgnoreCase("2345")
        response.get("tokenInfo.tokenExpiry").toString().equalsIgnoreCase("1218")

    }


    def 'send tokenization request (p12 keys)'() {
        setup:
        InputStream is2 = new FileInputStream("src/test/resources/mastercard_public.crt");
        InputStream is3 = new FileInputStream("src/test/resources/mastercard_private.p12");
        MDESCryptography interceptor = new MDESCryptography(is2, is3, "", "MIIEpAIBAAKCAQ");
        ApiConfig.addCryptographyInterceptor(interceptor);

        when:

        RequestMap requestMap = new RequestMap();
        requestMap.set("tokenRequestorId", "12345678901" );
        requestMap.set("requestId", "123456");
        requestMap.set("tokenType", "CLOUD");
        requestMap.set("taskId", "123456");

        requestMap.set("cardInfo.encryptedData.source", "CARD_ON_FILE");
        requestMap.set("cardInfo.encryptedData.accountNumber", "5123456789012345");
        requestMap.set("cardInfo.encryptedData.expiryMonth", "12");
        requestMap.set("cardInfo.encryptedData.expiryYear", "18");
        requestMap.set("cardInfo.encryptedData.securityCode", "123");
        requestMap.set("cardInfo.encryptedData.billingAddress.line", "100 1st Street");
        requestMap.set("cardInfo.encryptedData.billingAddress.line2", "Apt. 4B");
        requestMap.set("cardInfo.encryptedData.billingAddress.city", "St. Louis");
        requestMap.set("cardInfo.encryptedData.billingAddress.countrySubdivision", "MO");
        requestMap.set("cardInfo.encryptedData.billingAddress.postalCode", "61000");
        requestMap.set("cardInfo.encryptedData.billingAddress.country", "USA");

        Tokenize response = new Tokenize(requestMap).create(requestMap);

        then:
        response.get("decision").toString().equalsIgnoreCase("APPROVED")
        response.get("responseId").toString().equalsIgnoreCase("123456")
        response.get("tokenInfo.accountPanSuffix").toString().equalsIgnoreCase("2345")
        response.get("tokenInfo.tokenExpiry").toString().equalsIgnoreCase("1218")

    }
}


