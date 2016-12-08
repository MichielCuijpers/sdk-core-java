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
import com.mastercard.api.core.functional.model.ActivateStatementCreditOffer
import com.mastercard.api.core.model.RequestMap
import com.mastercard.api.core.security.Authentication
import com.mastercard.api.core.security.oauth.OAuthAuthentication
import spock.lang.Ignore
import spock.lang.Specification

public class ActivateStatementCreditOfferSpec extends Specification {


    public static final String consumerKey = "rJWlVy-B-8Tfa5k0raxXy_BgKIfUx41sYT9CMdBod8885a33!50383044ee074864822d99b0a0295aa30000000000000000";

    def setupSpec() {
        ApiConfig.setDebug(true);
        ApiConfig.setSandbox(true);

        try {
            InputStream is = new FileInputStream("src/test/resources/sandbox9_sandbox.p12");
            Authentication authentication = new OAuthAuthentication(consumerKey, is, "sandbox9", "keystorepassword");
            ApiConfig.setAuthentication(authentication);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


//    @Unroll("test ASCII character number: #item")
//    def 'test_example_stolen' () {
//
//
//        expect:
//        String tmpString = ""+(char)item+"TEXT";
//        RequestMap request = new RequestMap();
//        request.set("AccountInquiry.AccountNumber", "5343434343434343");
//        println (tmpString);
//        request.set("AccountInquiry.AsciiText", tmpString);
//        AccountInquiry response = new AccountInquiry(request).update();
//        response != null
//
//        where:
//        item << (32..159)
//        //request.set("AccountInquiry.AsciiText", "!#%&'()*+-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~");
//
//
//
//    }

    @Ignore
    def 'test_activate_offer' () {
        when:
        RequestMap map = new RequestMap();
        map.set("fid", "999990");
        map.set("OfferId", "36cf5460-b3f6-3fba-815a-f3613f57d097");
        map.set("UserToken", "16118eb71800000");
        map.set("RedemptionMode", "POINTS");


        ActivateStatementCreditOffer response = ActivateStatementCreditOffer.activateCredit(map);

        then:
        response != null

    }


}


