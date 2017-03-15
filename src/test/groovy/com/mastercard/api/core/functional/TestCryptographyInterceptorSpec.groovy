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

import com.mastercard.api.core.model.RequestMap
import com.mastercard.api.core.security.CryptographyInterceptor
import com.mastercard.api.core.security.fle.Config
import com.mastercard.api.core.security.installments.InstallmentCryptography
import com.mastercard.api.core.security.mdes.MDESCryptography
import org.json.simple.JSONValue
import spock.lang.Specification

public class TestCryptographyInterceptorSpec extends Specification {


    public static final String consumerKey = "L5BsiPgaF-O3qA36znUATgQXwJB6MRoMSdhjd7wt50c97279!50596e52466e3966546d434b7354584c4975693238513d3d";

    def 'test encrypt map payload'() {
        setup:
        InputStream is2 = new FileInputStream("src/test/resources/mastercard_public.crt");
        InputStream is3 = new FileInputStream("src/test/resources/mastercard_private.key");
        CryptographyInterceptor interceptor = new MDESCryptography(is2, is3);

        when:

        RequestMap requestMap = new RequestMap();
        requestMap.set("tokenRequestorId", "12345678901" );
        requestMap.set("requestId", "123456");
        requestMap.set("tokenType", "CLOUD");
        requestMap.set("taskId", "123456");
        requestMap.set("cardInfo.encryptedData.source", "CARD_ON_FILE");
        requestMap.set("cardInfo.encryptedData.accountNumber", "5123456789012345");
        requestMap.set("cardInfo.encryptedData.expiryMonth", "12");
        requestMap.set("cardInfo.encryptedData.expiryYear", "18");
        requestMap.set("cardInfo.encryptedData.securityCode", "123");
        requestMap.set("cardInfo.encryptedData.billingAddress.line", "100 1st Street");
        requestMap.set("cardInfo.encryptedData.billingAddress.line2", "Apt. 4B");
        requestMap.set("cardInfo.encryptedData.billingAddress.city", "St. Louis");
        requestMap.set("cardInfo.encryptedData.billingAddress.countrySubdivision", "MO");
        requestMap.set("cardInfo.encryptedData.billingAddress.postalCode", "61000");
        requestMap.set("cardInfo.encryptedData.billingAddress.country", "USA");

        RequestMap encryptedMap = interceptor.encrypt(requestMap)


        then:
        encryptedMap.containsKey("cardInfo.publicKeyFingerprint")
        encryptedMap.containsKey("cardInfo.oaepHashingAlgorithm")
        encryptedMap.containsKey("cardInfo.iv")
        encryptedMap.containsKey("cardInfo.encryptedKey")
        encryptedMap.containsKey("cardInfo.encryptedData")

    }

    //{"tokenRequestorId":"12345678901","requestId":"123456","tokenType":"CLOUD","taskId":"123456","cardInfo":{"publicKeyFingerprint":"3e3ff1c50fd4046b9a80c39d3d077f7313b92ea01462744bfe50b62769dbef68","oaepHashingAlgorithm":"SHA512","iv":"87489dfc009c27813ee2e02fc18c25f5","encryptedKey":"314b9445ebf025a2db1fa08a9d2c050595183820673c61ac2914a3c6cee5f8075b2e03661709bf6a75731b00e3586dabac42871e4f7f8afef1d642e1080cf01a008d42dc7ffec8dfbfec35a2c0df2503f85d076e850a5a00151eb4805f27f3dfe32fe13ac0f2325243d4e2cd722787b2b96b2cd7b234f73cfca9a35cac63cbd67278bd56471274e1d8af92d46b56017b94c7765f263f4d31241d3513dfebc8d1ea7e60bf4a8df4d0aca1080a108bc2213d68e522d50eaab3bbbfa737b4ae665df58cde13263590fe0e78f62592e988f3c8e93be839c881e65ce328512a226a1637e7b6d260e73adef45a544db42e9b28f296206eec7cc997448b56167a41d6ca","encryptedData":"cb7086bb13929186fdc2153f4fce3df847b35d3546daabbc0b4a440a24a96e2947febb01e4ba212cda946cc53929ff7c1352d625b9d5d6779829be8bf12480da41b543eb02c68f6cfa97bc7c828fcf2b58aba664c78fba3459611c08930726781fb6d39795dcc1cd32994f9fc97a92a4258e496d6b87b01902ab7413624c52fb27ad30b3403bf34350f7b7fe02bf9a3c02f4d461c8aa34bdf0f9fa1594d1cc4d3ee7302fa731e1477b4e1d0c95cebe9556993bf8fa3ababa465fb5a34b7b3ba7e09345e732281800018a17fc606522a030a36712e27d0fbc1468d1c25927bd48e1d5bb976711c7175c84e69c723673993bde7b6ff84a03eb796e96632176e1bb4bd577e4dd2699876fc3fb9ae9a289d4"}}

    def 'test decrypt map payload'() {
        setup:
        InputStream is2 = new FileInputStream("src/test/resources/mastercard_public.crt");
        InputStream is3 = new FileInputStream("src/test/resources/mastercard_private.key");
        CryptographyInterceptor interceptor = new MDESCryptography(is2, is3);

        when:

        String response = "{\"responseId\":\"123456\",\"responseHost\":\"site.1.sample.service.mastercard.com\",\"token\":{\"tokenUniqueReference\":\"DWSPMC000000000132d72d4fcb2f4136a0532d3093ff1a45\",\"status\":\"ACTIVE\",\"productConfig\":{\"longDescription\":\"Bank Rewards MasterCard with the super duper rewards program\",\"issuerProductConfigCode\":\"123456\",\"termsAndConditionsUrl\":\"https://bank.com/termsAndConditions\",\"issuerMobileApp\":{},\"issuerName\":\"Issuing Bank\",\"cardBackgroundCombinedAssetId\":\"739d27e5-629d-11e3-949a-0800200c9a66\",\"iconAssetId\":\"800d00c3-549a-41e3-223b-0739b35e5187\",\"foregroundColor\":\"000000\",\"issuerLogoAssetId\":\"629d00c9-549a-21e3-129c-0739b35e5a38\",\"shortDescription\":\"Bank Rewards MasterCard\",\"coBrandName\":\"Test CoBrand Name\",\"onlineBankingLoginUrl\":\"https://bank.com/online\",\"privacyPolicyUrl\":\"https://bank.com/privacy\",\"customerServiceEmail\":\"customerservice@bank.com\",\"customerServicePhoneNumber\":\"123-456-7898\",\"isCoBranded\":\"true\",\"brandLogoAssetId\":\"800200c9-629d-11e3-949a-0739d27e5a67\",\"customerServiceUrl\":\"https://bank.com/customerservice\",\"coBrandLogoAssetId\":\"Test coBrand Logo AssetId\"},\"tdsRegistrationUrl\":\"https://tds.mastercard.com/\",\"tokenInfo\":{\"tokenPanSuffix\":\"1234\",\"accountPanSuffix\":\"5675\",\"tokenExpiry\":\"1018\",\"dsrpCapable\":true,\"tokenAssuranceLevel\":1}},\"tokenDetail\":{\"tokenUniqueReference\":\"DWSPMC000000000132d72d4fcb2f4136a0532d3093ff1a45\",\"publicKeyFingerprint\":\"8fc11150a7508f14baca07285703392a399cc57c\",\"encryptedKey\":\"6dc0818665bd9a22b73839fa4a24687fc0fcc08797228804374bed9329107f9f92cf825ba4b9c10543c6302fc57a049fd3b686c15a62da5ff2198ad4a9e83f35ed4d78c360917500a919853972d030a7a6b47556d5c79b94a7a9fd01fe68ff7750f6c1de39e778fe5b8cc07c3eb9c5959fcb9113dc85caa976d2d637ae3d89ba2dc636b2514e2dbfeddcc77fcccdb085b73c38dbec0f63a182fa481a099c8626aff5c240a5f3bd5ad13b457f2b31b45f869a570e15cec4ace9d9d90a6406a374286d5172e0cd36d74fbe152cafef4a56ed55c92121f668780e5107cb4395832a6e1bfd4018893e054efa583626c397e896d66e21d1b99551aec140ebfa4a6855\",\"oaepHashingAlgorithm\":\"SHA256\",\"iv\":\"020269a0bff444afedcaabe15876d681\",\"encryptedData\":\"20c0c5f2a251fa08417df0b9447975a6cd884bcddf90a57a47f11d7e8ee1bbd413cbb4e65b66f6d180c815fd75b2293cf7b11550208626400d424e560cb7089f7e512a36b1d010dbf7785a2673ffc37b1a8ad0b7369635c9131e39274de0c0d1afa3732ff1c7cbd1d71c8c0aa35b6614\"}}";
        RequestMap requestMap = new RequestMap(response);
        RequestMap decryptedMap = interceptor.decrypt(requestMap)


        then:

        decryptedMap.containsKey("tokenDetail.tokenNumber");
        decryptedMap.containsKey("tokenDetail.expiryMonth");
        decryptedMap.containsKey("tokenDetail.expiryYear");
        decryptedMap.containsKey("tokenDetail.tokenUniqueReference");


    }



    def 'test encrypt string payload'() {
        setup:
        InputStream is2 = new FileInputStream("src/test/resources/mastercard_public.crt");
        InputStream is3 = new FileInputStream("src/test/resources/mastercard_private.key");
        CryptographyInterceptor interceptor = new InstallmentCryptography(is2, is3);


        when:

        RequestMap requestMap = new RequestMap();
        requestMap.set("calculatorReqData.primaryAccountNumber", "5555444444444444" );

        RequestMap encryptedMap = interceptor.encrypt(requestMap)


        then:
        encryptedMap.containsKey("calculatorReqData.iv")
        encryptedMap.containsKey("calculatorReqData.oaepHashingAlgorithm") == false
        encryptedMap.containsKey("calculatorReqData.wrappedKey")
        encryptedMap.containsKey("calculatorReqData.primaryAccountNumber")
        encryptedMap.containsKey("calculatorReqData.publicKeyFingerprint") == false

    }


    def 'test config from json'() {
        setup:
        def inputMap = [
                triggeringEndPath: ['test1', 'test2'],
                fieldsToEncrypt: ["fieldsToEncrypt"],
                fieldsToDecrypt: ["fieldsToDecrypt"],
                symmetricAlgorithm: "symmetricAlgorithm",
                symmetricCipher: "symmetricCipher",
                symmetricKeysize: "128",
                asymmetricCipher: "asymmetricCipher",
                oaepHashingAlgorithm: "oaepHashingAlgorithm",
                asymmetricCipher: "asymmetricCipher",
                oaepHashingAlgorithm: "oaepHashingAlgorithm",
                digestAlgorithm: "digestAlgorithm",
                ivFieldName: "ivFieldName",
                oaepHashingAlgorithmFieldName: "oaepHashingAlgorithmFieldName",
                encryptedKeyFiledName: "encryptedKeyFiledName",
                encryptedDataFieldName: "encryptedDataFieldName",
                publicKeyFingerprintFiledName: "publicKeyFingerprintFiledName",
                dataEncoding: "hex"
        ]

        when:

        Config tmpConfig = Config.parseFromJson(JSONValue.toJSONString(inputMap));


        then:
        tmpConfig != null;


    }
}


