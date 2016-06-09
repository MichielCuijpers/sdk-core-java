package com.mastercard.api.core.security.mdes;

import com.mastercard.api.core.security.CryptographyInterceptor;
import com.mastercard.api.core.security.util.CryptUtil;
import org.apache.commons.codec.DecoderException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.simple.JSONValue;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

/**
 * Created by andrearizzini on 13/05/2016.
 */
public class MDESCryptography implements CryptographyInterceptor {

    private Certificate issuerCertificate;
    private PrivateKey privateKey;
    private List<String> fieldsToHide = Arrays.asList("publicKeyFingerprint","oaepHashingAlgorithm","iv","encryptedData","encryptedKey");
    public  final String triggeringPath = "/tokenize";



    public MDESCryptography(InputStream issuerKeyInputStream, InputStream privateKeyInputStream)
            throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException,
            KeyStoreException, IOException, NoSuchProviderException, InvalidKeySpecException {
        this.issuerCertificate = CryptUtil.loadCertificate("X.509", BouncyCastleProvider.PROVIDER_NAME, issuerKeyInputStream);
        this.privateKey = CryptUtil.loadPrivateKey("RSA", BouncyCastleProvider.PROVIDER_NAME, privateKeyInputStream);
    }

    @Override
    public String getTriggeringPath() {
        return triggeringPath;
    }

    @Override public Map<String,Object> encrypt(Map<String,Object> map) throws NoSuchAlgorithmException, InvalidKeyException, CertificateEncodingException, InvalidAlgorithmParameterException, NoSuchPaddingException, BadPaddingException, UnsupportedEncodingException, NoSuchProviderException, IllegalBlockSizeException {

        if(map.containsKey("cardInfo")) {
            // 1) extract the encryptedData from map
            Map<String,Object> encryptedDataMap =  (Map<String,Object>)  map.remove("cardInfo");

            // 2) create json string
            String payload = JSONValue.toJSONString(encryptedDataMap);
            // 3) escaping the string
            payload = CryptUtil.sanitizeJson(payload);

            // 4) generate random iv
            IvParameterSpec iv = CryptUtil.generateIv();
            String hexIv = CryptUtil.byteArrayToHexString(iv.getIV());

            // 5) generate AES SecretKey
            SecretKey secretKey = CryptUtil.generateSecretKey("AES", BouncyCastleProvider.PROVIDER_NAME, 256);

            // 6) encrypt payload
            byte[] encryptedData = CryptUtil.crypt(Cipher.ENCRYPT_MODE, "AES/CBC/PKCS7Padding", BouncyCastleProvider.PROVIDER_NAME, secretKey, iv, payload.getBytes("UTF8"));
            String hexEncryptedData = CryptUtil.byteArrayToHexString(encryptedData);

            // 7) encrypt secretKey with issuer key
            byte[] encryptedSecretKey = CryptUtil.wrap("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", BouncyCastleProvider.PROVIDER_NAME, this.issuerCertificate.getPublicKey(), secretKey);
            String hexEncryptedKey = CryptUtil.byteArrayToHexString(encryptedSecretKey);

            byte[] certificateFingerprint = CryptUtil.generateFingerprint("SHA-1", this.issuerCertificate);
            String fingerprintHexString = CryptUtil.byteArrayToHexString(certificateFingerprint);

            HashMap encryptedMap = new HashMap();
            encryptedMap.put("publicKeyFingerprint", fingerprintHexString);
            encryptedMap.put("encryptedKey", hexEncryptedKey);
            encryptedMap.put("oaepHashingAlgorithm", "SHA256");
            encryptedMap.put("iv", hexIv);
            encryptedMap.put("encryptedData", hexEncryptedData);
            map.put("cardInfo", encryptedMap);

        }

        return map;

    }

    @Override
    public Map<String,Object> decrypt(Map<String,Object> map) throws DecoderException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException {


        if (map.containsKey("token")) {
            Map<String,Object> tokenMap = (Map<String,Object>) map.get("token");

            if (tokenMap.containsKey("encryptedData") && tokenMap.containsKey("encryptedKey")) {

                //need to read the key
                String encryptedKey = (String) tokenMap.remove("encryptedKey");
                byte[] encryptedKeyByteArray = CryptUtil.hexStringToByteArray(encryptedKey);

                //need to unwrap key with RSA
                SecretKey secretKey = (SecretKey) CryptUtil.unwrap(Cipher.DECRYPT_MODE, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding", BouncyCastleProvider.PROVIDER_NAME, this.privateKey, encryptedKeyByteArray, "AES", Cipher.SECRET_KEY);

                //need to read the iv
                String ivString = (String) tokenMap.remove("iv");
                byte[] ivByteArray = CryptUtil.hexStringToByteArray(ivString);
                IvParameterSpec iv = new IvParameterSpec(ivByteArray);


                //need to decrypt the data
                String encryptedData = (String) tokenMap.remove("encryptedData");
                byte[] encryptedDataByteArray = CryptUtil.hexStringToByteArray(encryptedData);

                byte[] decryptedDataArray = CryptUtil.crypt(Cipher.DECRYPT_MODE, "AES/CBC/PKCS7Padding", BouncyCastleProvider.PROVIDER_NAME, secretKey, iv, encryptedDataByteArray);
                String decryptedDataString = new String(decryptedDataArray);

                // remove the field that are not required in the map
                for(String toHide : fieldsToHide) {
                    tokenMap.remove(toHide);
                }

                // add the decrypted data map to the token.
                Map<String,Object> decryptedDataMap = (Map<String,Object>) JSONValue.parse(decryptedDataString);
                for(Map.Entry<String,Object> entry : decryptedDataMap.entrySet()) {
                    tokenMap.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return map;

    }


    public boolean equals(MDESCryptography o) {
        if (this.issuerCertificate.getType().compareTo(o.issuerCertificate.getType()) == 0) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public int hashCode() {
        return new StringBuilder(). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(this.issuerCertificate.getType()).hashCode();
    }
}
