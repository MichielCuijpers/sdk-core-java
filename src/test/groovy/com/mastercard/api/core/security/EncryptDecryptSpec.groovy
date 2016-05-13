package com.mastercard.api.core.security
import groovy.json.JsonBuilder
import spock.lang.Specification

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.*
import java.security.cert.X509Certificate

/**
 * Created by eamondoyle on 16/02/2016.
 */
class EncryptDecryptSpec extends Specification {

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
        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] ivBytes = new byte[16]; //cipher.getBlockSize()
        randomSecureRandom.nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        then:
        iv != null

        when: 'create a random private key (SK)'
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();

        then:
        secretKey != null

        when: "create a ciper for PKCS5 padding"
        Cipher encryptDataCyper = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // Initialize the Cipher with key and parameters
        encryptDataCyper.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        then:
        encryptDataCyper != null
        encryptDataCyper.algorithm == "AES/CBC/PKCS5Padding"


        when: "encrypt the stripped json"
        byte[] ecryptedData = encryptDataCyper.doFinal(cardInfoJsonEscape.getBytes());

        then:
        ecryptedData != null


        when: "load issuer public key"
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(this.class.getClassLoader().getResourceAsStream("certificate.p12"), "".toCharArray());
        Key privateKey = ks.getKey("1", "".toCharArray());
        PublicKey publicKey = null;
        if (privateKey instanceof PrivateKey) {
            X509Certificate cert = ks.getCertificate("1");
            publicKey = cert.getPublicKey();
        }


        then:
        publicKey != null


        when: "create a cipher for the RSA";
        Cipher encryptKeyCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
        encryptKeyCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        then:
        encryptKeyCipher != null
        encryptKeyCipher.algorithm == "RSA/ECB/OAEPWithSHA-1AndMGF1Padding"


        when: "encrypt secret key"
        byte[] encrypedKey = encryptKeyCipher.doFinal(secretKey.getEncoded())

        then:
        encrypedKey != null

        when: "create a decipher for RSA (decryption)"
        Cipher descryptKeyCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
        descryptKeyCipher.init(Cipher.DECRYPT_MODE, privateKey);

        then:
        descryptKeyCipher != null
        descryptKeyCipher.algorithm == "RSA/ECB/OAEPWithSHA-1AndMGF1Padding"

        when: "extract secret key from encryptedKey"
        byte[] decrypedKeyByteArray = descryptKeyCipher.doFinal(encrypedKey);
        SecretKey originalKey = new SecretKeySpec(decrypedKeyByteArray, 0, decrypedKeyByteArray.length, "AES");

        then:
        originalKey.algorithm == "AES"


        when: "create a deciper for PKCS5 padding"
        // Decryption cipher
        Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // Initialize PBE Cipher with key and parameters
        decryptCipher.init(Cipher.DECRYPT_MODE, originalKey, iv);

        then:
        decryptCipher != null
        decryptCipher.algorithm == "AES/CBC/PKCS5Padding"

        when: "descrypt text"
        byte[] decryptedBytesArray = decryptCipher.doFinal(ecryptedData);

        then: "check if decrypted text matches the input text"
        cardInfoJsonEscape == new String(decryptedBytesArray);

    }


}
