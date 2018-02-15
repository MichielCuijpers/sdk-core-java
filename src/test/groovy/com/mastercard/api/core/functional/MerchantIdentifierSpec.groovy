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
import com.mastercard.api.core.functional.model.MerchantIdentifier
import com.mastercard.api.core.model.RequestMap
import com.mastercard.api.core.security.Authentication
import com.mastercard.api.core.security.oauth.OAuthAuthentication
import org.junit.Assert
import spock.lang.Specification

class MerchantIdentifierSpec extends Specification {


    public static final String consumerKey = "sLDddGV2GijXzVaTZxqC9kKTYDwGdFp3pq2ci3-de0b9a383!9354e490ed5a4be6a18406d172ad59040000000000000000";


    def setup() {
        ApiConfig.setDebug(true);

        try {
            InputStream is = new FileInputStream("src/test/resources/test_prod4-production.p12");
            Authentication authentication = new OAuthAuthentication(consumerKey, is, "test_prod4", "test_prod4");
            ApiConfig.setAuthentication(authentication);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    
    def 'test merchant identifier with UTF-8 (local-apigw)' () {


        when:
        //MerchantIdentifier.setHost("http://dev.api.mastercard.com:8016/mosp")
        //MerchantIdentifier.setHost("https://services.mastercard.com")
        RequestMap request = new RequestMap();
        request.set("Type", "FuzzyMatch");
        request.set("MerchantId", "A LA CARTE EXPRESSMONTREALQC");
        MerchantIdentifier response = MerchantIdentifier.query(request);

        then:
        response.isEmpty() == false;
        Assert.assertEquals("MONTRÉAL", response.get("MerchantIds.ReturnedMerchants.Merchant.Address.City"));

    }


}


