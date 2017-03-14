package com.mastercard.api.core.security.mdes;

import com.mastercard.api.core.exception.SdkException;
import com.mastercard.api.core.model.RequestMap;
import com.mastercard.api.core.security.CryptographyInterceptor;
import com.mastercard.api.core.security.util.CryptUtil;
import com.mastercard.api.core.security.util.KeyType;
import org.json.simple.JSONValue;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.util.*;

/**
 * Created by andrearizzini on 13/05/2016.
 */
public class MDESCryptography implements CryptographyInterceptor {

    private final Certificate publicCertificate;
    private final PrivateKey privateKey;
    private final List<String> triggeringEndPath;
    private final List<String> objectsToEncrypt;
    private final List<String> objectsToDecrypt;

    public MDESCryptography(InputStream publicCertificate, InputStream keystore, String privateKeyAlias, String privateKeyPassword)
            throws SdkException {
        try {
            this.publicCertificate = CryptUtil.loadCertificate("X.509", publicCertificate);
            this.privateKey = (PrivateKey) CryptUtil.loadKey(KeyType.PRIVATE, "PKCS12", keystore, privateKeyAlias, privateKeyPassword );
            this.triggeringEndPath = Arrays.asList("/tokenize", "/searchTokens", "/getToken", "/transact", "/notifyTokenUpdated");
            this.objectsToEncrypt = Arrays.asList("cardInfo.encryptedData", "encryptedPayload.encryptedData");
            this.objectsToDecrypt = Arrays.asList("encryptedPayload.encryptedData", "tokenDetail.encryptedData");
        } catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }

    }

    public MDESCryptography(InputStream publicCertificate, InputStream keystore, String privateKeyAlias, String privateKeyPassword, List<String> triggeringEndPath, List<String> objectsToEncrypt, List<String> objectsToDecrypt)
            throws SdkException {
        try {
            this.publicCertificate = CryptUtil.loadCertificate("X.509", publicCertificate);
            this.privateKey = (PrivateKey) CryptUtil.loadKey(KeyType.PRIVATE, "PKCS12", keystore, privateKeyAlias, privateKeyPassword );
            this.triggeringEndPath = triggeringEndPath;
            this.objectsToEncrypt = objectsToEncrypt;
            this.objectsToDecrypt = objectsToDecrypt;
        } catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }

    }

    public MDESCryptography(InputStream publicCertificate, InputStream masterCardPrivateKey)
            throws SdkException {
        try {
            this.publicCertificate = CryptUtil.loadCertificate("X.509", publicCertificate);
            this.privateKey = CryptUtil.loadPrivateKey("RSA", masterCardPrivateKey);
            this.triggeringEndPath = Arrays.asList("/tokenize", "/searchTokens", "/getToken", "/transact", "/notifyTokenUpdated");
            this.objectsToEncrypt = Arrays.asList("cardInfo.encryptedData", "encryptedPayload.encryptedData");
            this.objectsToDecrypt = Arrays.asList("encryptedPayload.encryptedData", "tokenDetail.encryptedData");
        } catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }

    }


    public MDESCryptography(InputStream publicCertificate, InputStream masterCardPrivateKey, List<String> triggeringEndPath, List<String> objectsToEncrypt, List<String> objectsToDecrypt)
            throws SdkException {

        try {

        this.publicCertificate = CryptUtil.loadCertificate("X.509", publicCertificate);
        this.privateKey = CryptUtil.loadPrivateKey("RSA", masterCardPrivateKey);
        this.triggeringEndPath = triggeringEndPath;
        this.objectsToEncrypt = objectsToEncrypt;
        this.objectsToDecrypt = objectsToDecrypt;

        } catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }
    }


    public List<String> getTriggeringEndPath() {
        return triggeringEndPath;
    }

    @Override public Map<String,Object> encrypt(Map<String,Object> map) throws SdkException {

        try {

            //requestMap is a SmartMap it offers a easy way to do nested lookups.
            RequestMap smartMap = new RequestMap(map);

            for (String objectToEncryt : objectsToEncrypt) {
                if(smartMap.containsKey(objectToEncryt)) {
                    // 1) extract the encryptedData from map
                    Map<String,Object> encryptedDataMap =  (Map<String,Object>)  smartMap.remove(objectToEncryt);

                    // 2) create json string
                    String payload = JSONValue.toJSONString(encryptedDataMap);
                    // 3) escaping the string
                    payload = CryptUtil.sanitizeJson(payload);

                    // 4) generate random iv
                    IvParameterSpec iv = CryptUtil.generateIv();
                    String hexIv = CryptUtil.byteArrayToHexString(iv.getIV());

                    // 5) generate AES SecretKey
                    SecretKey secretKey = CryptUtil.generateSecretKey("AES",  128);

                    // 6) encrypt payload
                    byte[] encryptedData = CryptUtil.crypt(Cipher.ENCRYPT_MODE, "AES/CBC/PKCS5Padding", "SunJCE",  secretKey, iv, payload.getBytes("UTF8"));
                    String hexEncryptedData = CryptUtil.byteArrayToHexString(encryptedData);

                    // 7) encrypt secretKey with issuer key
                    byte[] encryptedSecretKey = CryptUtil.wrap("RSA/ECB/OAEPWithSHA-512AndMGF1Padding", "SunJCE", this.publicCertificate.getPublicKey(), secretKey);
                    String hexEncryptedKey = CryptUtil.byteArrayToHexString(encryptedSecretKey);

                    byte[] certificateFingerprint = CryptUtil.generateFingerprint("SHA-1", this.publicCertificate);
                    String fingerprintHexString = CryptUtil.byteArrayToHexString(certificateFingerprint);

                    HashMap encryptedMap = new HashMap();
                    encryptedMap.put("publicKeyFingerprint", fingerprintHexString);
                    encryptedMap.put("iv", hexIv);
                    encryptedMap.put("encryptedKey", hexEncryptedKey);
                    encryptedMap.put("encryptedData", hexEncryptedData);
                    encryptedMap.put("oaepHashingAlgorithm", "SHA512");


                    String keyMap = objectToEncryt.substring(0, objectToEncryt.indexOf("."));
                    smartMap.put(keyMap, encryptedMap);
                    break;
                }
            }
            return smartMap;
        } catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }




    }

    @Override
    public Map<String,Object> decrypt(Map<String,Object> map) throws SdkException {


        try {
            //requestMap is a SmartMap it offers a easy way to do nested lookups.
            RequestMap smartMap = new RequestMap(map);
            for (String objectToDescript : objectsToDecrypt) {
                if (smartMap.containsKey(objectToDescript)) {
                    String keyMap = objectToDescript.substring(0, objectToDescript.lastIndexOf("."));
                    String encryptedKeyMap = objectToDescript.substring(objectToDescript.lastIndexOf(".")+1);
                    Map<String, Object> enclosingBlock = (Map<String, Object>) smartMap.get(keyMap);
                    if (enclosingBlock.containsKey(encryptedKeyMap) && enclosingBlock.containsKey("encryptedKey")) {

                        //need to read the key
                        String encryptedKey = (String) enclosingBlock.remove("encryptedKey");
                        byte[] encryptedKeyByteArray = CryptUtil.hexStringToByteArray(encryptedKey);

                        SecretKey secretKey = null;
                        if (!enclosingBlock.containsKey("oaepHashingAlgorithm")) {
                            secretKey = (SecretKey) CryptUtil.unwrap("RSA/ECB/PKCS1Padding", "SunJCE", this.privateKey, encryptedKeyByteArray, "AES", Cipher.SECRET_KEY);
                        } else  {
                            //arizzini: we need to able to provide different digest hasing coming back from the server. sha-256 / sha-512
                            String oaepHashingAlgorithm = (String) enclosingBlock.remove("oaepHashingAlgorithm");
                            if (!oaepHashingAlgorithm.contains("-")) {
                                oaepHashingAlgorithm = oaepHashingAlgorithm.replace("SHA", "SHA-");
                            }
                            secretKey = (SecretKey) CryptUtil.unwrap("RSA/ECB/OAEPWith"+oaepHashingAlgorithm+"AndMGF1Padding", "SunJCE", this.privateKey, encryptedKeyByteArray, "AES", Cipher.SECRET_KEY);
                        }

                        //need to read the iv
                        String ivString = (String) enclosingBlock.remove("iv");
                        byte[] ivByteArray = CryptUtil.hexStringToByteArray(ivString);
                        IvParameterSpec iv = new IvParameterSpec(ivByteArray);


                        //need to decrypt the data
                        String encryptedData = (String) enclosingBlock.remove(encryptedKeyMap);
                        byte[] encryptedDataByteArray = CryptUtil.hexStringToByteArray(encryptedData);

                        byte[] decryptedDataArray = CryptUtil.crypt(Cipher.DECRYPT_MODE, "AES/CBC/PKCS5Padding", "SunJCE", secretKey, iv, encryptedDataByteArray);
                        String decryptedDataString = new String(decryptedDataArray);


                        HashMap encryptedMap = new HashMap();
                        // add the decrypted data map to the token.
                        Map<String, Object> decryptedDataMap = (Map<String, Object>) JSONValue.parse(decryptedDataString);
                        for (Map.Entry<String, Object> entry : decryptedDataMap.entrySet()) {
                            encryptedMap.put(entry.getKey(), entry.getValue());
                        }
                        enclosingBlock.put(encryptedKeyMap, encryptedMap);
                    }
                    break;
                }
            }

            return smartMap;
        } catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }


    }


    public boolean equals(MDESCryptography o) {
        if (this.triggeringEndPath.equals(o.triggeringEndPath) &&
                this.objectsToDecrypt.equals(o.objectsToDecrypt) &&
                this.objectsToEncrypt.equals(o.objectsToEncrypt)) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public int hashCode() {
        return new StringBuilder() // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                .append(this.triggeringEndPath.hashCode())
                .append(this.objectsToDecrypt.hashCode())
                .append(this.objectsToEncrypt.hashCode()).hashCode();
    }
}
