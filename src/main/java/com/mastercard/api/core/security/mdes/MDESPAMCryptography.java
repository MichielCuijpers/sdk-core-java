package com.mastercard.api.core.security.mdes;

import com.mastercard.api.core.exception.SdkException;
import com.mastercard.api.core.security.fle.Config;
import com.mastercard.api.core.security.fle.FieldLevelEncryption;
import com.mastercard.api.core.security.util.DataEncoding;

import java.io.InputStream;
import java.util.Arrays;

public class MDESPAMCryptography extends FieldLevelEncryption {

    public MDESPAMCryptography(InputStream publicCertificate, InputStream keystore, String privateKeyAlias, String privateKeyPassword, String publicKeyFingerprint )
            throws SdkException {
        super(publicCertificate, keystore, privateKeyAlias, privateKeyPassword, config(), publicKeyFingerprint);
    }

    public MDESPAMCryptography(InputStream publicCertificate, InputStream masterCardPrivateKey, String publicKeyFingerprint)
            throws SdkException {
        super(publicCertificate, masterCardPrivateKey, config(), publicKeyFingerprint);

    }

    public MDESPAMCryptography(InputStream publicCertificate, InputStream keystore, String privateKeyAlias, String privateKeyPassword )
            throws SdkException {
        super(publicCertificate, keystore, privateKeyAlias, privateKeyPassword, config(), null);
    }

    public MDESPAMCryptography(InputStream publicCertificate, InputStream masterCardPrivateKey)
            throws SdkException {
        super(publicCertificate, masterCardPrivateKey, config(), null);
    }

    public static final Config config() {
        Config tmpConfig = new Config();
        tmpConfig.triggeringEndPath = Arrays.asList("/closeAccount", "/addAccount", "/overrideForDeleteAccount", "/getPaymentAccountReference", "/updateAccount");
        tmpConfig.fieldsToEncrypt = Arrays.asList("encryptedPayload.encryptedData");
        tmpConfig.fieldsToDecrypt = Arrays.asList("encryptedPayload.encryptedData");
        tmpConfig.symmetricAlgorithm = "AES/CBC/PKCS5Padding";
        tmpConfig.symmetricCipher = "AES";
        tmpConfig.symmetricKeysize = 128;
        tmpConfig.asymmetricCipher = "RSA/ECB/OAEPWithSHA-512AndMGF1Padding";
        tmpConfig.oaepHashingAlgorithm = "SHA512";
        tmpConfig.publicKeyFingerprintHashing = "SHA-256";
        tmpConfig.ivFieldName = "iv";
        tmpConfig.oaepHashingAlgorithmFieldName = "oaepHashingAlgorithm";
        tmpConfig.encryptedKeyFiledName = "encryptedKey";
        tmpConfig.encryptedDataFieldName = "encryptedData";
        tmpConfig.publicKeyFingerprintFiledName = "publicKeyFingerprint";
        tmpConfig.dataEncoding = DataEncoding.HEX;
        return tmpConfig;
    }
}
