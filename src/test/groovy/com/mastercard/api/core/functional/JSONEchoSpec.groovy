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
import com.mastercard.api.core.functional.model.JSONEcho
import com.mastercard.api.core.model.RequestMap
import com.mastercard.api.core.security.Authentication
import com.mastercard.api.core.security.oauth.OAuthAuthentication
import org.junit.Ignore
import spock.lang.Specification

@Ignore
public class JSONEchoSpec extends Specification {


    public static final String consumerKey = "sLDddGV2GijXzVaTZxqC9kKTYDwGdFp3pq2ci3-de0b9a383!c02f47460b624d4a9ffb1afdccf9e5730000000000000000";

    def setupSpec() {
        ApiConfig.setDebug(true);
        ApiConfig.setSandbox(true);

        try {
            InputStream is = new FileInputStream("src/test/resources/alias-production.p12");
            Authentication authentication = new OAuthAuthentication(consumerKey, is, "alias", "password");
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

    def 'test json echo with UTF-8' () {


        when:

        JSONEcho.setHost("http://sandbox.api.mastercard.com")
        String utf8 = "мảŝťễřÇāŕď Ľẵвš ạאָđ мãśţēяĈẫřđ ĀקÏ ŕồçҝş...";
        RequestMap request = new RequestMap();
        request.set("JSONEcho.string", utf8);
        JSONEcho response = JSONEcho.create(request);

        then:
        response.get("JSONEcho.string").toString().equalsIgnoreCase(utf8);

    }

    def 'test json echo with UTF-8 to geteway' () {


        when:
        String utf8 = "мảŝťễřÇāŕď Ľẵвš ạאָđ мãśţēяĈẫřđ ĀקÏ ŕồçҝş...";
        RequestMap request = new RequestMap();
        request.set("JSONEcho.string", utf8);
        JSONEcho response = JSONEcho.create(request);

        then:
        response.get("JSONEcho.string").toString().equalsIgnoreCase(utf8);

    }


}


