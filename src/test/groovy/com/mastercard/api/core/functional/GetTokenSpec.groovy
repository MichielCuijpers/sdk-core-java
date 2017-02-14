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
import com.mastercard.api.core.functional.model.GetToken
import com.mastercard.api.core.functional.model.Tokenize
import com.mastercard.api.core.model.RequestMap
import com.mastercard.api.core.security.Authentication
import com.mastercard.api.core.security.mdes.MDESCryptography
import com.mastercard.api.core.security.oauth.OAuthAuthentication
import spock.lang.Specification

public class GetTokenSpec extends Specification {


    public static final String consumerKey = "L5BsiPgaF-O3qA36znUATgQXwJB6MRoMSdhjd7wt50c97279!50596e52466e3966546d434b7354584c4975693238513d3d";

    def setupSpec() {
        ApiConfig.setDebug(true);
        ApiConfig.setSandbox(true);

        try {
            InputStream is = new FileInputStream("src/test/resources/mcapi_sandbox_key.p12");
            Authentication authentication = new OAuthAuthentication(consumerKey, is, "test", "password");
            ApiConfig.setAuthentication(authentication);

            InputStream is2 = new FileInputStream("src/test/resources/mastercard_public.crt");
            InputStream is3 = new FileInputStream("src/test/resources/mastercard_private.key");
            MDESCryptography interceptor = new MDESCryptography(is2, is3);
            ApiConfig.addCryptographyInterceptor(interceptor);
            //ApiConfig.addCryptographyInterceptor(new MDES())
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    def 'get token request (orginal keys)'() {

        when:

        RequestMap map = new RequestMap();
        map.set("requestId", "123456");
        map.set("paymentAppInstanceId", "123456789");
        map.set("tokenUniqueReference", "DWSPMC000000000132d72d4fcb2f4136a0532d3093ff1a45");
        map.set("includeTokenDetail", "true");
        GetToken response = GetToken.create(map);


        then:
        response.get("responseId").toString().equalsIgnoreCase("123456");
        response.get("token.tokenUniqueReference").toString().equalsIgnoreCase("DWSPMC000000000132d72d4fcb2f4136a0532d3093ff1a45");
        response.get("token.status").toString().equalsIgnoreCase("ACTIVE");
        response.get("token.productConfig.brandLogoAssetId").toString().equalsIgnoreCase("800200c9-629d-11e3-949a-0739d27e5a67");
        response.get("token.productConfig.isCoBranded").toString().equalsIgnoreCase("true");
        response.get("token.productConfig.foregroundColor").toString().equalsIgnoreCase("000000");
        response.get("token.productConfig.issuerName").toString().equalsIgnoreCase("Issuing Bank");
        response.get("token.productConfig.shortDescription").toString().equalsIgnoreCase("Bank Rewards MasterCard");
        response.get("token.productConfig.longDescription").toString().equalsIgnoreCase("Bank Rewards MasterCard with the super duper rewards program");
        response.get("token.productConfig.customerServiceUrl").toString().equalsIgnoreCase("https://bank.com/customerservice");
        response.get("token.productConfig.termsAndConditionsUrl").toString().equalsIgnoreCase("https://bank.com/termsAndConditions");
        response.get("token.productConfig.privacyPolicyUrl").toString().equalsIgnoreCase("https://bank.com/privacy");
        response.get("token.productConfig.issuerProductConfigCode").toString().equalsIgnoreCase("123456");
        response.get("token.tokenInfo.tokenPanSuffix").toString().equalsIgnoreCase("1234");
        response.get("token.tokenInfo.accountPanSuffix").toString().equalsIgnoreCase("5675");
        response.get("token.tokenInfo.tokenExpiry").toString().equalsIgnoreCase("1018");
        response.get("token.tokenInfo.dsrpCapable").toString().equalsIgnoreCase("true");
        response.get("token.tokenInfo.tokenAssuranceLevel").toString().equalsIgnoreCase("1");

    }



}


