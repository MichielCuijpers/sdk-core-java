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
import com.mastercard.api.core.functional.model.Insights
import com.mastercard.api.core.model.RequestMap
import com.mastercard.api.core.security.Authentication
import com.mastercard.api.core.security.oauth.OAuthAuthentication
import spock.lang.Specification

public class InsightsSpec extends Specification {

    public static final String clientId = "gVaoFbo86jmTfOB4NUyGKaAchVEU8ZVPalHQRLTxeaf750b6!414b543630362f426b4f6636415a5973656c33735661383d";

    def setupSpec() {
        ApiConfig.setDebug(true)
        ApiConfig.setSandbox(true)

        try {
            InputStream is = new FileInputStream("src/test/resources/prod_key.p12")
            Authentication authentication = new OAuthAuthentication(clientId, is, "test", "password")
            ApiConfig.setAuthentication(authentication)
        }
        catch (Exception e) {
            e.printStackTrace()
        }

    }

    def 'test_example_insights'()  {
        given: 
        RequestMap map = new RequestMap()
        map.set("CurrentRow", "1")
        map.set("Country", "US")
        map.set("Offset", "25")

        when:
        Insights response = Insights.query(map)

        then:
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[0].SalesIndexValue").toString().equalsIgnoreCase("7146577.851")
        response.get("SectorRecordList.Count").toString().equalsIgnoreCase("70")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[2].EndDate").toString().equalsIgnoreCase("11/1/2014")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[2].Country").toString().equalsIgnoreCase("US")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[3].AverageTicketIndex").toString().equalsIgnoreCase("-0.00577838")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[3].BeginDate").toString().equalsIgnoreCase("9/7/2014")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[0].Country").toString().equalsIgnoreCase("US")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[3].Country").toString().equalsIgnoreCase("US")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[3].SalesIndexValue").toString().equalsIgnoreCase("4716899.304")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[1].AverageTicketIndex").toString().equalsIgnoreCase("-0.007884916")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[2].BeginDate").toString().equalsIgnoreCase("10/5/2014")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[2].AverageTicketIndex").toString().equalsIgnoreCase("-0.010073866")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[1].EndDate").toString().equalsIgnoreCase("11/29/2014")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[2].Sector").toString().equalsIgnoreCase("U.S. Natural and Organic Grocery Stores")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[2].Ecomm").toString().equalsIgnoreCase("NO")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[3].Sector").toString().equalsIgnoreCase("U.S. Natural and Organic Grocery Stores")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[3].Ecomm").toString().equalsIgnoreCase("NO")
        response.get("SectorRecordList.Message").toString().equalsIgnoreCase("Success")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[0].Sector").toString().equalsIgnoreCase("U.S. Natural and Organic Grocery Stores")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[0].Ecomm").toString().equalsIgnoreCase("NO")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[1].Ecomm").toString().equalsIgnoreCase("NO")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[1].Period").toString().equalsIgnoreCase("Monthly")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[0].Period").toString().equalsIgnoreCase("Monthly")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[2].SalesIndexValue").toString().equalsIgnoreCase("4776139.381")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[1].Sector").toString().equalsIgnoreCase("U.S. Natural and Organic Grocery Stores")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[1].BeginDate").toString().equalsIgnoreCase("11/2/2014")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[3].Period").toString().equalsIgnoreCase("Monthly")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[0].AverageTicketIndex").toString().equalsIgnoreCase("-0.029602284")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[3].SalesIndex").toString().equalsIgnoreCase("0.089992028")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[1].SalesIndexValue").toString().equalsIgnoreCase("5390273.888")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[3].EndDate").toString().equalsIgnoreCase("10/4/2014")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[2].Period").toString().equalsIgnoreCase("Monthly")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[0].SalesIndex").toString().equalsIgnoreCase("0.049201983")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[1].SalesIndex").toString().equalsIgnoreCase("0.074896863")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[0].BeginDate").toString().equalsIgnoreCase("11/30/2014")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[1].Country").toString().equalsIgnoreCase("US")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[0].EndDate").toString().equalsIgnoreCase("1/3/2015")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[2].SalesIndex").toString().equalsIgnoreCase("0.077937282")
    }

}


