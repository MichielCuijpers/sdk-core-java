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

        
        
        
        
        
        
                
    def 'test_example_insights'()  {

        when:
        RequestMap map = new RequestMap();
        map.set("Period", "");
        map.set("CurrentRow", "1");
        map.set("Sector", "");
        map.set("Offset", "25");
        map.set("Country", "US");
        map.set("Ecomm", "");
        Insights response = Insights.query(map);


        then:
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[1].YearBeforeEndDate").toString().equalsIgnoreCase("11/30/2013")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[16].SalesIndex").toString().equalsIgnoreCase("0.033862493")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[9].Sector").toString().equalsIgnoreCase("U.S. Natural and Organic Grocery Stores")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[25].YearBeforeEndDate").toString().equalsIgnoreCase("11/9/2013")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[22].YearBeforeEndDate").toString().equalsIgnoreCase("11/30/2013")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[1].TransactionsIndex").toString().equalsIgnoreCase("0.083439694")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[0].Period").toString().equalsIgnoreCase("Monthly")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[19].TransactionsIndex").toString().equalsIgnoreCase("0.064810496")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[25].BeginDate").toString().equalsIgnoreCase("11/2/2014")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[6].YearBeforeEndDate").toString().equalsIgnoreCase("7/13/2013")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[6].Ecomm").toString().equalsIgnoreCase("NO")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[4].BeginDate").toString().equalsIgnoreCase("8/10/2014")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[18].EndDate").toString().equalsIgnoreCase("12/27/2014")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[17].Period").toString().equalsIgnoreCase("Weekly")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[16].YearBeforeBeginDate").toString().equalsIgnoreCase("12/30/2012")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[12].SalesIndexValue").toString().equalsIgnoreCase("4666390.074")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[14].Country").toString().equalsIgnoreCase("US")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[1].BeginDate").toString().equalsIgnoreCase("11/2/2014")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[18].AverageTicketIndex").toString().equalsIgnoreCase("-0.003968331")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[14].SalesIndexValue").toString().equalsIgnoreCase("14586848.49")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[12].Sector").toString().equalsIgnoreCase("U.S. Natural and Organic Grocery Stores")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[25].TransactionsIndex").toString().equalsIgnoreCase("0.089399728")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[25].Period").toString().equalsIgnoreCase("Weekly")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[13].SalesIndex").toString().equalsIgnoreCase("0.070222262")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[17].Country").toString().equalsIgnoreCase("US")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[7].SalesIndexValue").toString().equalsIgnoreCase("4610930.63")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[9].BeginDate").toString().equalsIgnoreCase("3/23/2014")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[3].AverageTicketIndex").toString().equalsIgnoreCase("-0.00577838")
        response.get("SectorRecordList.SectorRecordArray.SectorRecord[21].YearBeforeEndDate").toString().equalsIgnoreCase("12/7/2013")
        response.get("SectorRecordList.Message").toString().equalsIgnoreCase("Success")
        

    }
    

        
    
        
    
}


