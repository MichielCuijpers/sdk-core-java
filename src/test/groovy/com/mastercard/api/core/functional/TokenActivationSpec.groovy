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
import com.mastercard.api.core.security.CryptographyContext
import com.mastercard.api.core.security.mdes.MDESFieldLevelCryptography
import com.mastercard.api.core.security.oauth.OAuthAuthentication
import spock.lang.Specification

public class TokenActivationSpec extends Specification {


    public static final String clientId = "gVaoFbo86jmTfOB4NUyGKaAchVEU8ZVPalHQRLTxeaf750b6!414b543630362f426b4f6636415a5973656c33735661383d";

    def setupSpec() {
        ApiConfig.setDebug(true);
        ApiConfig.setSandbox(true);

        try {
            InputStream is = new FileInputStream("src/test/resources/prod_key.p12");
            InputStream is2 = new FileInputStream("src/test/resources/mastercard_public.crt");
            Authentication authentication = new OAuthAuthentication(clientId, is, "test", "password");
            ApiConfig.setAuthentication(authentication);

            MDESFieldLevelCryptography interceptor = new MDESFieldLevelCryptography(CryptographyContext.BODY, is2);
            ApiConfig.addCryptographyInterceptor("/mdes/tokenization/1/0/token", interceptor);
            //ApiConfig.addCryptographyInterceptor(new MDES())
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    def 'send tokenization request'() {
        when:

//        "accountNumber": "5123456789012345",
//        "expiryMonth": "12",
//        "expiryYear": "16",
//        "securityCode": "123",
//        "billingAddress": {
//            "line1": "100 1st Street",
//            "line2": "Apt. 4B",
//            "city": "St. Louis",
//            "countrySubdivision": "MO",
//            "postalCode": "61000",
//            "country": "USA"
//        }

        RequestMap requestMap = new RequestMap();
        requestMap.set("tokenRequestorId", "12345678901" );
        requestMap.set("requestId", "123456");

        requestMap.set("cardInfo.accountNumber", "5123456789012345");
        requestMap.set("cardInfo.expiryMonth", "12");
        requestMap.set("cardInfo.expiryYear", "16");
        requestMap.set("cardInfo.securityCode", "123");
        requestMap.set("cardInfo.billingAddress.line", "100 1st Street");
        requestMap.set("cardInfo.billingAddress.line2", "Apt. 4B");
        requestMap.set("cardInfo.billingAddress.city", "St. Louis");
        requestMap.set("cardInfo.billingAddress.line2", "MO");
        requestMap.set("cardInfo.billingAddress.countrySubdivision", "Apt. 4B");
        requestMap.set("cardInfo.billingAddress.postalCode", "61000");
        requestMap.set("cardInfo.billingAddress.country", "USA");

        Tokenize response = new Tokenize(requestMap).create(requestMap);

        then:
        response.get("Account.Listed").toString().equalsIgnoreCase("True")
        response.get("Account.ReasonCode").toString().equalsIgnoreCase("L")
        response.get("Account.Reason").toString().equalsIgnoreCase("LOST")

    }

}


