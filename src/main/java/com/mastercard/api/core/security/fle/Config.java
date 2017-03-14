package com.mastercard.api.core.security.fle;

import com.mastercard.api.core.exception.SdkException;
import com.mastercard.api.core.security.util.DataEncoding;

import java.util.Arrays;
import java.util.List;

/**
 * Created by e049519 on 3/14/17.
 */


public class Config {

    public List<String> triggeringEndPath = null;
    public List<String> fieldsToEncrypt = null;
    public List<String> fieldsToDecrypt = null;

    public String symmetricAlgorithm = null;
    public String symmetricCipher = null;
    public int symmetricKeysize = -1;
    public String asymmetricCipher = null;
    public String oaepHashingAlgorithm = null;
    public String digestAlgorithm = null;


    public String ivFieldName = null;
    public String oaepHashingAlgorithmFieldName = null;
    public String encryptedKeyFiledName = null;
    public String encryptedDataFieldName = null;
    public String publicKeyFingerprintFiledName = null;

    public DataEncoding dataEncoding;

    private Config() {

    }

    protected void validate() {
        if (triggeringEndPath == null) {
            throw new SdkException("Config: triggetingEndPath is null");
        }
        if (fieldsToEncrypt == null) {
            throw new SdkException("Config: fieldsToEncrypt is null");
        }
        if (fieldsToDecrypt == null) {
            throw new SdkException("Config: fieldsToDecrypt is null");
        }
        if (symmetricAlgorithm == null) {
            throw new SdkException("Config: symmetricAlgorithm is null");
        }
        if (symmetricCipher == null) {
            throw new SdkException("Config: symmetricCipher is null");
        }
        if (symmetricKeysize == -1) {
            throw new SdkException("Config: symmetricKeysize is not set");
        }
        if (asymmetricCipher == null) {
            throw new SdkException("Config: asymmetricCipher is null");
        }
        if (oaepHashingAlgorithm == null) {
            throw new SdkException("Config: oaepHashingAlgorithm is null");
        }
        if (digestAlgorithm == null) {
            throw new SdkException("Config: digestAlgorithm is null");
        }
        if (ivFieldName == null) {
            throw new SdkException("Config: ivFieldName is null");
        }
        if (oaepHashingAlgorithmFieldName == null) {
            throw new SdkException("Config: oaepHashingAlgorithmFieldName is null");
        }
        if (encryptedKeyFiledName == null) {
            throw new SdkException("Config: encryptedKeyFiledName is null");
        }
        if (encryptedDataFieldName == null) {
            throw new SdkException("Config: encryptedDataFieldName is null");
        }
        if (publicKeyFingerprintFiledName == null) {
            throw new SdkException("Config: publicKeyFingerprintFiledName is null");
        }
    }


    public final static Config MDES() {
        Config tmpConfig = new Config();
        tmpConfig.triggeringEndPath = Arrays.asList("/tokenize", "/searchTokens", "/getToken", "/transact", "/notifyTokenUpdated");
        tmpConfig.fieldsToEncrypt = Arrays.asList("cardInfo.encryptedData", "encryptedPayload.encryptedData");
        tmpConfig.fieldsToDecrypt = Arrays.asList("encryptedPayload.encryptedData", "tokenDetail.encryptedData");

        tmpConfig.symmetricAlgorithm = "AES/CBC/PKCS5Padding";
        tmpConfig.symmetricCipher = "AES";
        tmpConfig.symmetricKeysize = 128;

        tmpConfig.asymmetricCipher = "RSA/ECB/OAEPWithSHA-512AndMGF1Padding";
        tmpConfig.oaepHashingAlgorithm = "SHA512";
        tmpConfig.digestAlgorithm = "SHA-256";

        tmpConfig.ivFieldName = "iv";
        tmpConfig.oaepHashingAlgorithmFieldName = "oaepHashingAlgorithm";
        tmpConfig.encryptedKeyFiledName = "encryptedKey";
        tmpConfig.encryptedDataFieldName = "encryptedData";
        tmpConfig.publicKeyFingerprintFiledName = "publicKeyFingerprint";
        tmpConfig.dataEncoding = DataEncoding.HEX;

        return tmpConfig;
    }


    public final static Config Installments() {
        Config tmpConfig = new Config();
        tmpConfig.triggeringEndPath = Arrays.asList("/calculateInstalment", "/processInstalment");
        tmpConfig.fieldsToEncrypt = Arrays.asList("calculatorReqData.primaryAccountNumber", "processInstalmentReqData.primaryAccountNumber");
        tmpConfig.fieldsToDecrypt = Arrays.asList("");

        tmpConfig.symmetricAlgorithm = "AES/CBC/PKCS5Padding";
        tmpConfig.symmetricCipher = "AES";
        tmpConfig.symmetricKeysize = 128;

        tmpConfig.asymmetricCipher = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
        tmpConfig.oaepHashingAlgorithm = "SHA256";
        tmpConfig.digestAlgorithm = "SHA-256";

        tmpConfig.ivFieldName = "iv";
        tmpConfig.oaepHashingAlgorithmFieldName = null;
        tmpConfig.encryptedKeyFiledName = "wrappedKey";
        tmpConfig.encryptedDataFieldName = "primaryAccountNumber";
        tmpConfig.publicKeyFingerprintFiledName = null;
        tmpConfig.dataEncoding = DataEncoding.BASE64;


        return tmpConfig;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Config config = (Config) o;

        if (symmetricKeysize != config.symmetricKeysize) return false;
        if (triggeringEndPath != null ? !triggeringEndPath.equals(config.triggeringEndPath) : config.triggeringEndPath != null)
            return false;
        if (fieldsToEncrypt != null ? !fieldsToEncrypt.equals(config.fieldsToEncrypt) : config.fieldsToEncrypt != null)
            return false;
        if (fieldsToDecrypt != null ? !fieldsToDecrypt.equals(config.fieldsToDecrypt) : config.fieldsToDecrypt != null)
            return false;
        if (symmetricAlgorithm != null ? !symmetricAlgorithm.equals(config.symmetricAlgorithm) : config.symmetricAlgorithm != null)
            return false;
        if (symmetricCipher != null ? !symmetricCipher.equals(config.symmetricCipher) : config.symmetricCipher != null)
            return false;
        if (asymmetricCipher != null ? !asymmetricCipher.equals(config.asymmetricCipher) : config.asymmetricCipher != null)
            return false;
        if (oaepHashingAlgorithm != null ? !oaepHashingAlgorithm.equals(config.oaepHashingAlgorithm) : config.oaepHashingAlgorithm != null)
            return false;
        if (digestAlgorithm != null ? !digestAlgorithm.equals(config.digestAlgorithm) : config.digestAlgorithm != null)
            return false;
        if (ivFieldName != null ? !ivFieldName.equals(config.ivFieldName) : config.ivFieldName != null) return false;
        if (oaepHashingAlgorithmFieldName != null ? !oaepHashingAlgorithmFieldName.equals(config.oaepHashingAlgorithmFieldName) : config.oaepHashingAlgorithmFieldName != null)
            return false;
        if (encryptedKeyFiledName != null ? !encryptedKeyFiledName.equals(config.encryptedKeyFiledName) : config.encryptedKeyFiledName != null)
            return false;
        if (encryptedDataFieldName != null ? !encryptedDataFieldName.equals(config.encryptedDataFieldName) : config.encryptedDataFieldName != null)
            return false;
        return publicKeyFingerprintFiledName != null ? publicKeyFingerprintFiledName.equals(config.publicKeyFingerprintFiledName) : config.publicKeyFingerprintFiledName == null;
    }

    @Override
    public int hashCode() {
        int result = triggeringEndPath != null ? triggeringEndPath.hashCode() : 0;
        result = 31 * result + (fieldsToEncrypt != null ? fieldsToEncrypt.hashCode() : 0);
        result = 31 * result + (fieldsToDecrypt != null ? fieldsToDecrypt.hashCode() : 0);
        result = 31 * result + (symmetricAlgorithm != null ? symmetricAlgorithm.hashCode() : 0);
        result = 31 * result + (symmetricCipher != null ? symmetricCipher.hashCode() : 0);
        result = 31 * result + symmetricKeysize;
        result = 31 * result + (asymmetricCipher != null ? asymmetricCipher.hashCode() : 0);
        result = 31 * result + (oaepHashingAlgorithm != null ? oaepHashingAlgorithm.hashCode() : 0);
        result = 31 * result + (digestAlgorithm != null ? digestAlgorithm.hashCode() : 0);
        result = 31 * result + (ivFieldName != null ? ivFieldName.hashCode() : 0);
        result = 31 * result + (oaepHashingAlgorithmFieldName != null ? oaepHashingAlgorithmFieldName.hashCode() : 0);
        result = 31 * result + (encryptedKeyFiledName != null ? encryptedKeyFiledName.hashCode() : 0);
        result = 31 * result + (encryptedDataFieldName != null ? encryptedDataFieldName.hashCode() : 0);
        result = 31 * result + (publicKeyFingerprintFiledName != null ? publicKeyFingerprintFiledName.hashCode() : 0);
        return result;
    }
}
