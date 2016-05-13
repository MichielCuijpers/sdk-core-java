package com.mastercard.api.core.security
import com.mastercard.api.core.security.util.CryptUtil
import com.mastercard.api.core.security.util.KeyType
import groovy.json.JsonBuilder
import spock.lang.Specification

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.PrivateKey
import java.security.PublicKey
/**
 * Created by eamondoyle on 16/02/2016.
 */
class MDESSpec extends Specification {

    def 'Test MDES token creation' () {

        when: 'serilized content payload and serialize json'
        Map cardInfo = [ accountNumber: "5123456789012345",
                         expiryMonth: "12",
                         expiryYear: "15",
                         source: "CARD_ON_FILE",
                         cardholderName: "John Doe",
                         securityCode: "123" ];

        String cardInfoJson = new JsonBuilder(cardInfo).toPrettyString();
        System.out.println(cardInfoJson);

        then:
        cardInfoJson != null

        when: 'stripping json'
        String cardInfoJsonEscape = cardInfoJson.replaceAll("\n", "").replaceAll("\r", "");
        System.out.println(cardInfoJsonEscape);

        then:
        cardInfoJsonEscape.contains("\n") == false

        when: 'creating iv'
        IvParameterSpec iv = CryptUtil.generateIv();

        then:
        iv != null

        when: 'create a random private key (SK)'
        SecretKey secretKey = CryptUtil.generateSecretKey("AES")

        then:
        secretKey != null

        when: "encryptData"
        byte[] encryptedData = CryptUtil.crypt(Cipher.ENCRYPT_MODE, "AES/CBC/PKCS5Padding", secretKey, iv, cardInfoJsonEscape.getBytes());

        then:
        encryptedData != null


        when: "load issuer public key"



        PublicKey publicKey = CryptUtil.loadKey(KeyType.PUBLIC, "PKCS12", this.class.getClassLoader().getResourceAsStream("certificate.p12"), "1", "");
        PrivateKey privateKey = CryptUtil.loadKey(KeyType.PRIVATE, "PKCS12", this.class.getClassLoader().getResourceAsStream("certificate.p12"), "1", "");

        then:
        publicKey != null


        when: "encryptKey";
        byte[] encryptedSecretKey = CryptUtil.crypt(Cipher.ENCRYPT_MODE, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding", publicKey, secretKey.getEncoded());

        then:
        encryptedSecretKey != null

        when: "decryptKey"
        byte[] decryptedKeyByteArray = CryptUtil.crypt(Cipher.DECRYPT_MODE, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding", privateKey, encryptedSecretKey);
        SecretKey originalKey = new SecretKeySpec(decryptedKeyByteArray, 0, decryptedKeyByteArray.length, "AES");

        then:
        originalKey != null
        originalKey.algorithm == "AES"


        when: "decryptData"
        byte[] decryptedBytesArray = CryptUtil.crypt(Cipher.DECRYPT_MODE, "AES/CBC/PKCS5Padding", originalKey, iv, encryptedData);

        then: "check if decrypted text matches the input text"
        cardInfoJsonEscape == new String(decryptedBytesArray);

    }


}
