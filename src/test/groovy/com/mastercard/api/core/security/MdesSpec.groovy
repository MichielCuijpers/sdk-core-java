package com.mastercard.api.core.security
import com.mastercard.api.core.model.RequestMap
import com.mastercard.api.core.security.util.CryptUtil
import groovy.json.JsonBuilder
import spock.lang.Specification

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.MGF1ParameterSpec
/**
 * Created by eamondoyle on 16/02/2016.
 */
class MdesSpec extends Specification {

    def 'Test MDES token validation' () {

        when: 'serilized content payload and serialize json'
        RequestMap requestMap = new RequestMap();
        requestMap.set("requestId", "123456");
        requestMap.set("tokenRequestorId", "98765432110" );

        requestMap.set("cardInfo.accountNumber", "5123456789012345");
        requestMap.set("cardInfo.expiryMonth", "12");
        requestMap.set("cardInfo.expiryYear", "16");
        requestMap.set("cardInfo.securityCode", "123");
        requestMap.set("cardInfo.billingAddress.line", "100 1st Street");
        requestMap.set("cardInfo.billingAddress.line2", "Apt. 4B");
        requestMap.set("cardInfo.billingAddress.city", "St. Louis");
        requestMap.set("cardInfo.billingAddress.line2", "MO");
        requestMap.set("cardInfo.billingAddress.countrySubdivision", "Apt. 4B");
        requestMap.set("cardInfo.billingAddress.postalCode", "61000");
        requestMap.set("cardInfo.billingAddress.country", "USA");
        requestMap.set("cardInfo.billingAddress.dataValidUntilTimestamp", "2015-12-12T14:40:12+07:00")

        String cardInfoJson = new JsonBuilder(requestMap).toPrettyString();
        System.out.println(cardInfoJson);

        then:
        cardInfoJson != null

        when: 'stripping json'
        String cardInfoJsonEscape = cardInfoJson.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "").replaceAll(" ", "");
        System.out.println(cardInfoJsonEscape);

        then:
        cardInfoJsonEscape.contains("\n") == false

        when: 'creating iv'
        String storedIv = "1dc2621ccd180770d017db3095674fcd";
        IvParameterSpec iv = new IvParameterSpec(CryptUtil.hexStringToByteArray(storedIv));

        then:
        iv != null

        when: 'load secret key'
        PublicKey issuerKey = (PublicKey) CryptUtil.loadPublicKey("X.509", this.class.getClassLoader().getResourceAsStream("secretKey.crt")); //public.pem

        then:
        issuerKey != null;


//        when: 'load secret key as AES'
//        byte[] secretKeyBytes = CryptUtil.getBytesFromInputStream(this.class.getClassLoader().getResourceAsStream("secretKey.crt"));
//        X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(secretKeyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("AES");
//        PublicKey bobPubKey = keyFactory.generatePublic(keyFactory);
//
//        then:
//        bobPubKey != null



        when: 'load private key'
        PrivateKey privateKey = CryptUtil.loadPrivateKey("RSA", this.class.getClassLoader().getResourceAsStream("private.key"));

        then:
        privateKey != null


        when: 'decript the encryptedKey'
//        String one = "6cba237c7dcb4cb896812f816cbbe0eaa2f84c38bb9b0709627f2d467296471c33d15c7042ce817178b74c2429525a9595f31ce930696670f6c07ec2ebcf19812f7e5b4d566971846b5e2a0f15c9b78758ffe04ed9c74d1a670c216657f5644614922885e3045256a651d69e46fa49dae2a0cc30c69d2d240925f164d4ffc1ded00310fcd29aa27aa01fb8d531343118bf508ac9998fba65632b44e76177b5f0ede733ad4dc30bef3fc8bc019da6f5776e09247bfc3e46b3b07c28b5711d1f04dfa319f58115386f91328a984877ed96b9dfea53339393b5ff90c23a93381d13bb0c05dbc2019f462b42bf293d00c86795891d286d184a7fc01ac4e674bf5836";
        String two = "3d58f9d5289780c986962e722fa71360820a5cc0f04313febd903c109064e23e8679757cb6a02e3ebcc6573300132c620fe8f5489ccb5e7a566f3eec3baa3831f8294ab77516aaf8a7f2b0e775a489a5c27ec43e0bc3cf2d54bb374a7b6e69a1eadda310d0a5c0fb4734abe88305d451322e6cd724ab97775d1d58563aed7c69c5781f180a0efeeb73d42a9be9be98f3e8c5bb6d3b5cf9f6eb697fb64e77f9f7168ad608527443c72661ba247ca3682314971183cc7a4a53d0f2aa8445c84bfceacb9411299db96eabe002c0c7e4b2d5234089b988185ba0570b8063173b4b954345a5dfcfe9af1afd84f165757a21ed84e013f96aa4d83d8015cf5f4e09c814";
        byte[] encryptedKeyByteArray = CryptUtil.hexStringToByteArray(two);
        byte[] decryptedKeyByteArray = CryptUtil.crypt(Cipher.DECRYPT_MODE, "RSA/ECB/OAEPWithSHA-256FAndMGF1Padding", privateKey, encryptedKeyByteArray );
//        Cipher currentCipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        //currentCipher.init(Cipher.UNWRAP_MODE, privateKey, new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256,  PSource.PSpecified.DEFAULT));
        //currentCipher.init(Cipher.UNWRAP_MODE, privateKey);
        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
        currentCipher.init(Cipher.UNWRAP_MODE, privateKey, oaepParams);
        SecretKey originalKey = currentCipher.unwrap(encryptedKeyByteArray, "AES", Cipher.SECRET_KEY);

        then:
        originalKey != null
        originalKey.algorithm == "AES"

    }


}
