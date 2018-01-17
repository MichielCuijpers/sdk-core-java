package com.mastercard.api.core.security.fle;

import com.mastercard.api.core.exception.SdkException;
import com.mastercard.api.core.model.RequestMap;
import com.mastercard.api.core.security.CryptographyInterceptor;
import com.mastercard.api.core.security.util.CryptUtil;
import com.mastercard.api.core.security.util.KeyType;
import org.json.simple.JSONValue;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;

/**
 * Created by andrearizzini on 14/03/2017.
 */
public class FieldLevelEncryption implements CryptographyInterceptor {


    private final Certificate publicCertificate;
    private final PrivateKey privateKey;
    protected final Config config;
    protected final String publicKeyFingerprint;


    public FieldLevelEncryption(InputStream publicCertificate, InputStream keystore, String privateKeyAlias, String privateKeyPassword, Config config, String publicKeyFingerprint)
            throws SdkException {
        try {
            if (publicCertificate != null) {
                this.publicCertificate = CryptUtil.loadCertificate("X.509", publicCertificate);
            } else {
                this.publicCertificate = null;
            }

            if (keystore != null) {
                this.privateKey = (PrivateKey) CryptUtil.loadKey(KeyType.PRIVATE, "PKCS12", keystore, privateKeyAlias, privateKeyPassword );
            } else {
                this.privateKey = null;
            }

            this.config = config;
            this.publicKeyFingerprint = publicKeyFingerprint;
        } catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }

    }

    public FieldLevelEncryption(InputStream publicCertificate, InputStream privateKey, Config config, String publicKeyFingerprint)
            throws SdkException {
        try {
            if (publicCertificate != null) {
                this.publicCertificate = CryptUtil.loadCertificate("X.509", publicCertificate);
            } else {
                this.publicCertificate = null;
            }

            if (privateKey != null) {
                this.privateKey = CryptUtil.loadPrivateKey("RSA", privateKey);
            } else {
                this.privateKey = null;
            }
            this.config = config;
            this.publicKeyFingerprint = publicKeyFingerprint;
        } catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }
    }


    @Override
    public List<String> getTriggeringEndPath() {
        return config.triggeringEndPath;
    }

    @Override
    public Map<String, Object> encrypt(Map<String, Object> map) throws SdkException {
        try {

            //requestMap is a SmartMap it offers a easy way to do nested lookups.
            RequestMap smartMap = new RequestMap(map);

            if (publicCertificate != null) {
                for (String fieldToEncrypt : config.fieldsToEncrypt) {
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
                        String ivValue = CryptUtil.byteArrayToString(iv.getIV(), config.dataEncoding);

                        // 5) generate AES SecretKey
                        SecretKey secretKey = CryptUtil.generateSecretKey(config.symmetricCipher, config.symmetricKeysize, config.publicKeyFingerprintHashing);

                        // 6) encrypt payload
                        byte[] encryptedData = CryptUtil.crypt(Cipher.ENCRYPT_MODE, config.symmetricAlgorithm, "SunJCE",  secretKey, iv, payload.getBytes("UTF8"));
                        String encryptedDataValue = CryptUtil.byteArrayToString(encryptedData, config.dataEncoding);


                        // 7) encrypt secretKey with issuer key
                        byte[] encryptedSecretKey = CryptUtil.wrap(config.asymmetricCipher, "SunJCE", this.publicCertificate.getPublicKey(), secretKey);
                        String encryptedKeyValue = CryptUtil.byteArrayToString(encryptedSecretKey, config.dataEncoding);

                        String fingerprintValue;
                        if (publicKeyFingerprint == null) {
                            byte[] certificateFingerprint = CryptUtil.generateFingerprint(config.publicKeyFingerprintHashing, this.publicCertificate);
                            fingerprintValue = CryptUtil.byteArrayToString(certificateFingerprint, config.dataEncoding);
                        } else {
                            //arizzini: use the pre-calculated value
                            fingerprintValue = publicKeyFingerprint;
                        }

                        String baseKey = "";
                        if ((fieldToEncrypt.indexOf('.') > 0)) {
                            baseKey =  fieldToEncrypt.substring(0, fieldToEncrypt.lastIndexOf("."));
                            baseKey += ".";
                        }
                        if (config.publicKeyFingerprintFiledName != null) {
                            smartMap.put(baseKey+config.publicKeyFingerprintFiledName, fingerprintValue);
                        }
                        if (config.oaepHashingAlgorithmFieldName != null) {
                            smartMap.put(baseKey+config.oaepHashingAlgorithmFieldName, config.oaepHashingAlgorithm);
                        }
                        smartMap.put(baseKey+config.ivFieldName, ivValue);
                        smartMap.put(baseKey+config.encryptedKeyFiledName, encryptedKeyValue);
                        smartMap.put(baseKey+config.encryptedDataFieldName, encryptedDataValue);

                        break;
                    }
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
            if (this.privateKey != null) {
                for (String fieldToDecrypt : config.fieldsToDecrypt) {
                    if (smartMap.containsKey(fieldToDecrypt)) {

                        String baseKey = "";
                        String encryptedDataMapField = fieldToDecrypt;
                        if ((fieldToDecrypt.indexOf('.') > 0)) {
                            baseKey =  fieldToDecrypt.substring(0, fieldToDecrypt.lastIndexOf("."));
                            encryptedDataMapField = fieldToDecrypt.substring(fieldToDecrypt.lastIndexOf(".")+1);
                        }

                        Map<String, Object> enclosingBlock = (Map<String, Object>) smartMap.get(baseKey);

                        //need to read the key
                        String encryptedKey = (String) enclosingBlock.remove(config.encryptedKeyFiledName);
                        byte[] encryptedKeyByteArray = CryptUtil.stringToByteArray(encryptedKey, config.dataEncoding);

                        SecretKey secretKey = null;
                        //arizzini: MDES, is not oaepHashingAlgorithm is used thew are guarantee to default to standard
                        if (!enclosingBlock.containsKey(config.oaepHashingAlgorithmFieldName)) {
                            secretKey = (SecretKey) CryptUtil.unwrap(config.asymmetricCipher, "SunJCE", this.privateKey, encryptedKeyByteArray, config.symmetricCipher, Cipher.SECRET_KEY);
                        } else  {
                            //arizzini: we need to able to provide different digest hasing coming back from the server. sha-256 / sha-512
                            String oaepHashingAlgorithm = (String) enclosingBlock.remove(config.oaepHashingAlgorithmFieldName);
                            if (!oaepHashingAlgorithm.contains("-")) {
                                oaepHashingAlgorithm = oaepHashingAlgorithm.replace("SHA", "SHA-");
                            }
                            secretKey = (SecretKey) CryptUtil.unwrap("RSA/ECB/OAEPWith"+oaepHashingAlgorithm+"AndMGF1Padding", "SunJCE", this.privateKey, encryptedKeyByteArray, config.symmetricCipher, Cipher.SECRET_KEY);
                        }

                        //need to read the iv
                        String ivString = (String) enclosingBlock.remove(config.ivFieldName);
                        if (enclosingBlock.containsKey(config.publicKeyFingerprintFiledName)) {
                            enclosingBlock.remove(config.publicKeyFingerprintFiledName);
                        }

                        byte[] ivByteArray = CryptUtil.stringToByteArray(ivString, config.dataEncoding);
                        IvParameterSpec iv = new IvParameterSpec(ivByteArray);


                        //need to decrypt the data
                        String encryptedData = (String) enclosingBlock.remove(encryptedDataMapField);
                        byte[] encryptedDataByteArray = CryptUtil.stringToByteArray(encryptedData, config.dataEncoding);

                        byte[] decryptedDataArray = CryptUtil.crypt(Cipher.DECRYPT_MODE, config.symmetricAlgorithm, "SunJCE", secretKey, iv, encryptedDataByteArray);
                        String decryptedDataString = new String(decryptedDataArray, Charset.forName("UTF-8"));

                        if (decryptedDataString.startsWith("{")) {
                            Map<String, Object> decryptedDataMap = (Map<String, Object>) JSONValue.parse(decryptedDataString);
                            for (Map.Entry<String, Object> entry : decryptedDataMap.entrySet()) {
                                smartMap.put(baseKey+"."+encryptedDataMapField+"."+entry.getKey(), entry.getValue());
                            }
                        } else {
                            smartMap.put(baseKey+"."+encryptedDataMapField, decryptedDataString);
                        }
                        break;
                    }
                }
            }
            return smartMap;
        } catch (Exception e) {
            throw new SdkException(e.getMessage(), e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldLevelEncryption otherMdes = (FieldLevelEncryption) o;

        return this.config.equals(otherMdes.config);
    }

    @Override
    public int hashCode() {
        return this.config.hashCode();
    }


}
