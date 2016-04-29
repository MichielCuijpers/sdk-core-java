package com.mastercard.api.core.functional;


import com.mastercard.api.core.ApiConfig
import com.mastercard.api.core.functional.model.Parameters
import com.mastercard.api.core.model.RequestMap
import com.mastercard.api.core.security.Authentication
import com.mastercard.api.core.security.oauth.OAuthAuthentication
import spock.lang.Specification
/**
 * Created by andrearizzini on 12/04/2016.
 */
class ParametersSpec extends Specification {

    public static final String clientId = "gVaoFbo86jmTfOB4NUyGKaAchVEU8ZVPalHQRLTxeaf750b6!414b543630362f426b4f6636415a5973656c33735661383d";

    def setupSpec() {
        ApiConfig.setDebug(true);
        ApiConfig.setSandbox(true);

        try {
            InputStream is = new FileInputStream("src/test/resources/prod_key.p12");
            Authentication authentication = new OAuthAuthentication(clientId, is, "test", "password");
            ApiConfig.setAuthentication(authentication);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }










    def 'test_example_parameters'() {

        when:
        RequestMap map = new RequestMap();
        map.set("CurrentRow", "1");
        map.set("Offset", "25");


        Parameters response = Parameters.query(map);


        then: 
        response.get("ParameterList.ParameterArray.Parameter[1].Ecomm").toString().equalsIgnoreCase("NO")
        response.get("ParameterList.ParameterArray.Parameter[1].Period").toString().equalsIgnoreCase("Quarterly")
        response.get("ParameterList.ParameterArray.Parameter[2].Ecomm").toString().equalsIgnoreCase("NO")
        response.get("ParameterList.ParameterArray.Parameter[0].Ecomm").toString().equalsIgnoreCase("NO")
        response.get("ParameterList.ParameterArray.Parameter[0].Sector").toString().equalsIgnoreCase("U.S. Natural and Organic Grocery Stores")
        response.get("ParameterList.ParameterArray.Parameter[1].Sector").toString().equalsIgnoreCase("U.S. Natural and Organic Grocery Stores")
        response.get("ParameterList.ParameterArray.Parameter[2].Sector").toString().equalsIgnoreCase("U.S. Natural and Organic Grocery Stores")
        response.get("ParameterList.ParameterArray.Parameter[0].Period").toString().equalsIgnoreCase("Monthly")
        response.get("ParameterList.Message").toString().equalsIgnoreCase("Success")
        response.get("ParameterList.Count").toString().equalsIgnoreCase("3")
        response.get("ParameterList.ParameterArray.Parameter[0].Country").toString().equalsIgnoreCase("US")
        response.get("ParameterList.ParameterArray.Parameter[2].Period").toString().equalsIgnoreCase("Weekly")
        response.get("ParameterList.ParameterArray.Parameter[1].Country").toString().equalsIgnoreCase("US")
        response.get("ParameterList.ParameterArray.Parameter[2].Country").toString().equalsIgnoreCase("US")


    }



}
