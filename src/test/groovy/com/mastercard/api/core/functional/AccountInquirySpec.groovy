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
import com.mastercard.api.core.functional.model.AccountInquiry
import com.mastercard.api.core.model.RequestMap
import com.mastercard.api.core.security.Authentication
import com.mastercard.api.core.security.oauth.OAuthAuthentication
import spock.lang.Specification

public class AccountInquirySpec extends Specification {


    public static final String consumerKey = "L5BsiPgaF-O3qA36znUATgQXwJB6MRoMSdhjd7wt50c97279!50596e52466e3966546d434b7354584c4975693238513d3d";

    def setupSpec() {
        ApiConfig.setDebug(true);
        ApiConfig.setSandbox();

        try {
            InputStream is = new FileInputStream("src/test/resources/mcapi_sandbox_key.p12");
            Authentication authentication = new OAuthAuthentication(consumerKey, is, "test", "password");
            ApiConfig.setAuthentication(authentication);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    def 'test_example_stolen' () {
        when:
        RequestMap request = new RequestMap();
        request.set("AccountInquiry.AccountNumber", "5343434343434343");
        AccountInquiry response = new AccountInquiry(request).update();

        then:
        response.get("Account.Listed").toString().equalsIgnoreCase("True");
        response.get("Account.ReasonCode").toString().equalsIgnoreCase("S")
        response.get("Account.Reason").toString().equalsIgnoreCase("STOLEN")

    }

    def 'test_example_lost' () {
        when:
        RequestMap request = new RequestMap();
        request.set("AccountInquiry.AccountNumber", "5222222222222200");
        AccountInquiry response = new AccountInquiry(request).update();
        
        then:
        response.get("Account.Listed").toString().equalsIgnoreCase("True")
        response.get("Account.ReasonCode").toString().equalsIgnoreCase("L")
        response.get("Account.Reason").toString().equalsIgnoreCase("LOST")
        
    }

}


