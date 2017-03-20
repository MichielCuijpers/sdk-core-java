package com.mastercard.api.core.security.mdes;

import com.mastercard.api.core.exception.SdkException;
import com.mastercard.api.core.security.fle.Config;
import com.mastercard.api.core.security.fle.FieldLevelEncryption;
import com.mastercard.api.core.security.util.DataEncoding;

import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by andrearizzini on 13/05/2016.
 */
public class MDESCryptography extends FieldLevelEncryption {

    public MDESCryptography(InputStream publicCertificate, InputStream keystore, String privateKeyAlias, String privateKeyPassword)
            throws SdkException {
        super(publicCertificate, keystore, privateKeyAlias, privateKeyPassword, Mdes());


    }

    public MDESCryptography(InputStream publicCertificate, InputStream masterCardPrivateKey)
            throws SdkException {
        super(publicCertificate, masterCardPrivateKey, Mdes());

    }



    private final static Config Mdes() {
        Config tmpConfig = new Config();
        tmpConfig.triggeringEndPath = Arrays.asList("/tokenize", "/searchTokens", "/getToken", "/transact", "/notifyTokenUpdated");
        tmpConfig.fieldsToEncrypt = Arrays.asList("cardInfo.encryptedData", "encryptedPayload.encryptedData");
        tmpConfig.fieldsToDecrypt = Arrays.asList("encryptedPayload.encryptedData", "tokenDetail.encryptedData");

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
