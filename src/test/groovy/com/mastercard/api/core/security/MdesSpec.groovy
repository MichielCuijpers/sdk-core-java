package com.mastercard.api.core.security
import com.mastercard.api.core.model.RequestMap
import com.mastercard.api.core.security.util.CryptUtil
import groovy.json.JsonBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import spock.lang.Specification

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import java.nio.charset.Charset
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.Certificate
import java.security.spec.MGF1ParameterSpec
/**
 * Created by eamondoyle on 16/02/2016.
 */
class MdesSpec extends Specification {

    def 'Test MDES token validation' () {

        when: 'serilized content payload and serialize json'
        RequestMap requestMap = new RequestMap();
        requestMap.set("name", "andrea");


        String payload = new JsonBuilder(requestMap).toPrettyString();
        System.out.println(payload);

        then:
        payload != null

        when: 'stripping json'
        String sanitizedPayload = CryptUtil.sanitizeJson(payload);
        System.out.println(sanitizedPayload);

        then:
        sanitizedPayload.contains("\n") == false

        when: 'creating iv'
        String storedIv = "4880af80260883f45d83f95917340f5b";
        IvParameterSpec iv = new IvParameterSpec(CryptUtil.hexStringToByteArray(storedIv));

        then:
        iv != null

        when: 'creating secretKey'
        SecretKey secretKey = CryptUtil.generateSecretKey("AES", BouncyCastleProvider.PROVIDER_NAME, 256)

        then:
        secretKey != null

        when: 'load cert'
        Certificate issuerCert = CryptUtil.loadCertificate("X.509", BouncyCastleProvider.PROVIDER_NAME, this.class.getClassLoader().getResourceAsStream("secretKey.crt")); //public.pem

        then:
        issuerCert != null;

        when: 'encrypt data'
        byte[] encryptedDataByteArray = CryptUtil.crypt(Cipher.ENCRYPT_MODE, "AES/CBC/PKCS7Padding", BouncyCastleProvider.PROVIDER_NAME, secretKey , iv, sanitizedPayload.getBytes("UTF8"));
        String encryptedData = CryptUtil.byteArrayToHexString(encryptedDataByteArray);

        then:
        encryptedData != null



    }


}
