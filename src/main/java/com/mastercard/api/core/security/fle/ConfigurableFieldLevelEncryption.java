package com.mastercard.api.core.security.fle;

import com.mastercard.api.core.exception.SdkException;
import com.mastercard.api.core.model.RequestMap;
import com.mastercard.api.core.security.CryptographyInterceptor;
import com.mastercard.api.core.security.mdes.MDESCryptography;
import com.mastercard.api.core.security.util.CryptUtil;
import com.mastercard.api.core.security.util.KeyType;
import org.json.simple.JSONValue;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andrearizzini on 14/03/2017.
 */
public class ConfigurableFieldLevelEncryption implements CryptographyInterceptor {


    private final Certificate publicCertificate;
    private final PrivateKey privateKey;

    private List<String> triggeringEndPath = Arrays.asList("/tokenize", "/searchTokens", "/getToken", "/transact", "/notifyTokenUpdated");
    private List<String> fieldsToEncrypt = Arrays.asList("cardInfo.encryptedData", "encryptedPayload.encryptedData");
    private List<String> fieldsToDecrypt = Arrays.asList("encryptedPayload.encryptedData", "tokenDetail.encryptedData");

    private String symmetricAlgorithm  = "AES/CBC/PKCS5Padding";

    private String symmetricCipher = "AES";
    private int symmetricKeysize = 128;
    private String asymmetricCipher = "RSA/ECB/OAEPWithSHA-512AndMGF1Padding";
    private String oaepHashingAlgorithm = "SHA512";
    private String fingerprintHashingAlgorithm = "SHA-256";


    private String ivFieldName = "iv";
    private String oaepHashingAlgorithmFieldName = "oaepHashingAlgorithm";
    private String encryptedKeyFiledName = "encryptedKey";
    private String ecvryptedDataFieldName = "encryptedData";
    private String publicKeyFingerprintFiledName = "publicKeyFingerprint";


    public ConfigurableFieldLevelEncryption(InputStream publicCertificate, InputStream keystore, String privateKeyAlias, String privateKeyPassword)
            throws SdkException {
        try {
            this.publicCertificate = CryptUtil.loadCertificate("X.509", publicCertificate);
            this.privateKey = (PrivateKey) CryptUtil.loadKey(KeyType.PRIVATE, "PKCS12", keystore, privateKeyAlias, privateKeyPassword );
        } catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }

    }

    public ConfigurableFieldLevelEncryption(InputStream publicCertificate, InputStream keystore, String privateKeyAlias, String privateKeyPassword, List<String> triggeringEndPath, List<String> objectsToEncrypt, List<String> objectsToDecrypt)
            throws SdkException {
        try {
            this.publicCertificate = CryptUtil.loadCertificate("X.509", publicCertificate);
            this.privateKey = (PrivateKey) CryptUtil.loadKey(KeyType.PRIVATE, "PKCS12", keystore, privateKeyAlias, privateKeyPassword );
        } catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }

    }

    public ConfigurableFieldLevelEncryption(InputStream publicCertificate, InputStream masterCardPrivateKey)
            throws SdkException {

        try {

            this.publicCertificate = CryptUtil.loadCertificate("X.509", publicCertificate);
            this.privateKey = CryptUtil.loadPrivateKey("RSA", masterCardPrivateKey);
        } catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }
    }

    public void configFields(String ivFieldName, String oaepHashingAlgorithmFieldName, String encryptedKeyFiledName, String ecvryptedDataFieldName, String publicKeyFingerprintFiledName) {
        this.ivFieldName = ivFieldName;
        this.oaepHashingAlgorithmFieldName = oaepHashingAlgorithmFieldName;
        this.encryptedKeyFiledName = encryptedKeyFiledName;
        this.ecvryptedDataFieldName = ecvryptedDataFieldName;
        this.publicKeyFingerprintFiledName = publicKeyFingerprintFiledName;
    }


    public void configAsymetric(String asymmetricCipher, String fingerprintHashingAlgorithm) {
        this.asymmetricCipher = asymmetricCipher;
        this.fingerprintHashingAlgorithm = fingerprintHashingAlgorithm;
        if (asymmetricCipher.contains("OAEP")) {
            int start = asymmetricCipher.indexOf("SHA-")+4;
            this.oaepHashingAlgorithm = asymmetricCipher.substring(start, start+3);
        }
    }

    public void configSymetric(String symmetricAlgorithm, String symmetricCipher, int symmetricKeysize) {
        this.symmetricAlgorithm = symmetricAlgorithm;
        this.symmetricCipher = symmetricCipher;
        this.symmetricKeysize = symmetricKeysize;
    }

    public void configTrigger(List<String> triggeringEndPath, List<String> fieldsToEncrypt, List<String> fieldsToDecrypt) {
        this.triggeringEndPath = triggeringEndPath;
        this.fieldsToEncrypt = fieldsToEncrypt;
        this.fieldsToDecrypt = fieldsToDecrypt;
    }


    @Override
    public List<String> getTriggeringEndPath() {
        return triggeringEndPath;
    }

    @Override
    public Map<String, Object> encrypt(Map<String, Object> map) throws SdkException {
        try {

            //requestMap is a SmartMap it offers a easy way to do nested lookups.
            RequestMap smartMap = new RequestMap(map);

            for (String fieldToEncrypt : fieldsToEncrypt) {
                if(smartMap.containsKey(fieldToEncrypt)) {
                    // 1) extract the encryptedData from map
                    Object tmpObjectToEncrypt =  smartMap.remove(fieldToEncrypt);

                    String payload = null;
                    if (tmpObjectToEncrypt instanceof Map) {
                        // 2) create json string
                        payload = JSONValue.toJSONString(tmpObjectToEncrypt);
                        // 3) escaping the string
                        payload = CryptUtil.sanitizeJson(payload);
                    } else {
                        // this is a simple value
                        payload = String.valueOf(tmpObjectToEncrypt);
                    }


                    // 4) generate random iv
                    IvParameterSpec iv = CryptUtil.generateIv();
                    String hexIv = CryptUtil.byteArrayToHexString(iv.getIV());

                    // 5) generate AES SecretKey
                    SecretKey secretKey = CryptUtil.generateSecretKey(symmetricCipher,  symmetricKeysize);

                    // 6) encrypt payload
                    byte[] encryptedData = CryptUtil.crypt(Cipher.ENCRYPT_MODE, symmetricAlgorithm, "SunJCE",  secretKey, iv, payload.getBytes("UTF8"));
                    String hexEncryptedData = CryptUtil.byteArrayToHexString(encryptedData);

                    boolean isOAEP = false;
                    if (asymmetricCipher.contains("OAEP")) {
                        isOAEP = true;
                    }

                    // 7) encrypt secretKey with issuer key
                    byte[] encryptedSecretKey = CryptUtil.wrap(asymmetricCipher, "SunJCE", this.publicCertificate.getPublicKey(), secretKey);
                    String hexEncryptedKey = CryptUtil.byteArrayToHexString(encryptedSecretKey);

                    byte[] certificateFingerprint = CryptUtil.generateFingerprint(fingerprintHashingAlgorithm, this.publicCertificate);
                    String fingerprintHexString = CryptUtil.byteArrayToHexString(certificateFingerprint);


                    String baseKey = fieldToEncrypt.substring(0, fieldToEncrypt.indexOf("."));

                    smartMap.put(baseKey+"."+publicKeyFingerprintFiledName, fingerprintHexString);
                    if (isOAEP) {
                        smartMap.put(baseKey+"."+oaepHashingAlgorithmFieldName, oaepHashingAlgorithm);
                    }
                    smartMap.put(baseKey+"."+ivFieldName, hexIv);
                    smartMap.put(baseKey+"."+encryptedKeyFiledName, hexEncryptedKey);
                    smartMap.put(baseKey+"."+ecvryptedDataFieldName, hexEncryptedData);


                    break;
                }
            }
            return smartMap;
        } catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> decrypt(Map<String, Object> map) throws SdkException {
        try {
            //requestMap is a SmartMap it offers a easy way to do nested lookups.
            RequestMap smartMap = new RequestMap(map);
            for (String fieldToDecrypt : fieldsToDecrypt) {
                if (smartMap.containsKey(fieldToDecrypt)) {
                    String baseKey = fieldToDecrypt.substring(0, fieldToDecrypt.lastIndexOf("."));
                    String encryptedDataMapField = fieldToDecrypt.substring(fieldToDecrypt.lastIndexOf(".")+1);


                    Map<String, Object> enclosingBlock = (Map<String, Object>) smartMap.get(baseKey);
                    if (enclosingBlock.containsKey(encryptedDataMapField) && enclosingBlock.containsKey(encryptedKeyFiledName)) {

                        //need to read the key
                        String encryptedKey = (String) enclosingBlock.remove(encryptedKeyFiledName);
                        byte[] encryptedKeyByteArray = CryptUtil.hexStringToByteArray(encryptedKey);

                        SecretKey secretKey = null;
                        //arizzini: MDES, is not oaepHashingAlgorithm is used thew are guarantee to default to standard
                        if (!enclosingBlock.containsKey(oaepHashingAlgorithmFieldName)) {
                            secretKey = (SecretKey) CryptUtil.unwrap(asymmetricCipher, "SunJCE", this.privateKey, encryptedKeyByteArray, symmetricCipher, Cipher.SECRET_KEY);
                        } else  {
                            //arizzini: we need to able to provide different digest hasing coming back from the server. sha-256 / sha-512
                            String oaepHashingAlgorithm = (String) enclosingBlock.remove(oaepHashingAlgorithmFieldName);
                            if (!oaepHashingAlgorithm.contains("-")) {
                                oaepHashingAlgorithm = oaepHashingAlgorithm.replace("SHA", "SHA-");
                            }
                            secretKey = (SecretKey) CryptUtil.unwrap("RSA/ECB/OAEPWith"+oaepHashingAlgorithm+"AndMGF1Padding", "SunJCE", this.privateKey, encryptedKeyByteArray, symmetricCipher, Cipher.SECRET_KEY);
                        }

                        //need to read the iv
                        String ivString = (String) enclosingBlock.remove(ivFieldName);
                        enclosingBlock.remove(publicKeyFingerprintFiledName);
                        byte[] ivByteArray = CryptUtil.hexStringToByteArray(ivString);
                        IvParameterSpec iv = new IvParameterSpec(ivByteArray);


                        //need to decrypt the data
                        String encryptedData = (String) enclosingBlock.remove(encryptedDataMapField);
                        byte[] encryptedDataByteArray = CryptUtil.hexStringToByteArray(encryptedData);

                        byte[] decryptedDataArray = CryptUtil.crypt(Cipher.DECRYPT_MODE, symmetricAlgorithm, "SunJCE", secretKey, iv, encryptedDataByteArray);
                        String decryptedDataString = new String(decryptedDataArray);

                        if (decryptedDataString.startsWith("{")) {
                            Map<String, Object> decryptedDataMap = (Map<String, Object>) JSONValue.parse(decryptedDataString);
                            for (Map.Entry<String, Object> entry : decryptedDataMap.entrySet()) {
                                smartMap.put(baseKey+"."+entry.getKey(), entry.getValue());
                            }
                        } else {
                            smartMap.put(baseKey+"."+encryptedDataMapField, decryptedDataString);
                        }
                    }
                    break;
                }
            }

            return smartMap;
        } catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }
    }

    public boolean equals(ConfigurableFieldLevelEncryption o) {
        if (this.triggeringEndPath.equals(o.triggeringEndPath) &&
                this.fieldsToDecrypt.equals(o.fieldsToDecrypt) &&
                this.fieldsToEncrypt.equals(o.fieldsToEncrypt)) {
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
                .append(this.fieldsToDecrypt.hashCode())
                .append(this.fieldsToEncrypt.hashCode()).hashCode();
    }

}
