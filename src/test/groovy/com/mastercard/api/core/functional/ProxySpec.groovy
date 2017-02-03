package com.mastercard.api.core.functional

import com.mastercard.api.core.ApiConfig
import com.mastercard.api.core.ApiController
import com.mastercard.api.core.exception.ApiException
import com.mastercard.api.core.functional.model.*
import com.mastercard.api.core.http.HttpBuilder
import com.mastercard.api.core.mocks.MockAuthentication
import com.mastercard.api.core.mocks.MockBaseObject
import com.mastercard.api.core.model.RequestMap
import com.mastercard.api.core.model.ResourceList
import com.mastercard.api.core.security.Authentication
import com.mastercard.api.core.security.oauth.OAuthAuthentication
import org.apache.http.HttpHost
import org.apache.http.impl.client.CustomHttpClientBuilder
import org.junit.Ignore
import spock.lang.IgnoreIf
import spock.lang.Specification

/**
 * Created by andrearizzini on 12/04/2016.
 */


@Ignore
class ProxySpec extends Specification {


    public static final String consumerKey = "L5BsiPgaF-O3qA36znUATgQXwJB6MRoMSdhjd7wt50c97279!50596e52466e3966546d434b7354584c4975693238513d3d";

    def setupSpec() {
        ApiConfig.setDebug(true);
        ApiConfig.setSandbox(true);


        CustomHttpClientBuilder builder = HttpBuilder.getInstance();
        HttpHost proxy = new HttpHost("127.0.0.1", 9999)
        builder.setProxy(proxy);



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




}
