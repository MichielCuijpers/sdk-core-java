package com.mastercard.api.core.security.installments;

import com.mastercard.api.core.exception.SdkException;
import com.mastercard.api.core.security.fle.Config;
import com.mastercard.api.core.security.fle.FieldLevelEncryption;
import com.mastercard.api.core.security.util.DataEncoding;

import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by andrearizzini on 13/05/2016.
 */
public class InstallmentCryptography extends FieldLevelEncryption {

    public InstallmentCryptography(InputStream publicCertificate, InputStream keystore, String privateKeyAlias, String privateKeyPassword)
            throws SdkException {
        super(publicCertificate, keystore, privateKeyAlias, privateKeyPassword, config(), null);
    }

    public InstallmentCryptography(InputStream publicCertificate, InputStream keystore, String privateKeyAlias, String privateKeyPassword, String publicKeyFingerprint)
            throws SdkException {
        super(publicCertificate, keystore, privateKeyAlias, privateKeyPassword, config(), publicKeyFingerprint);
    }

    public InstallmentCryptography(InputStream publicCertificate, InputStream masterCardPrivateKey)
            throws SdkException {
        super(publicCertificate, masterCardPrivateKey, config(), null);

    }

    public InstallmentCryptography(InputStream publicCertificate, InputStream masterCardPrivateKey, String publicKeyFingerprint)
            throws SdkException {
        super(publicCertificate, masterCardPrivateKey, config(), publicKeyFingerprint);

    }


    public final static Config config() {
        Config tmpConfig = new Config();
        tmpConfig.triggeringEndPath = Arrays.asList("/installmentConfigdata","/calculateInstallment", "/processInstallment", "/receiveApproval");
        tmpConfig.fieldsToEncrypt = Arrays.asList("configReqData.primaryAccountNumber", "calculatorReqData.primaryAccountNumber", "processInstallmentReqData.primaryAccountNumber", "receiveIssuerApprReqData.primaryAccountNumber");
        tmpConfig.fieldsToDecrypt = Arrays.asList("");

        tmpConfig.symmetricAlgorithm = "AES/CBC/PKCS5Padding";
        tmpConfig.symmetricCipher = "AES";
        tmpConfig.symmetricKeysize = 128;

        tmpConfig.asymmetricCipher = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
        tmpConfig.oaepHashingAlgorithm = "SHA256";
        tmpConfig.publicKeyFingerprintHashing = "SHA-256";

        tmpConfig.ivFieldName = "iv";
        tmpConfig.oaepHashingAlgorithmFieldName = null;
        tmpConfig.encryptedKeyFiledName = "wrappedKey";
        tmpConfig.encryptedDataFieldName = "primaryAccountNumber";
        tmpConfig.publicKeyFingerprintFiledName = null;
        tmpConfig.dataEncoding = DataEncoding.BASE64;

        return tmpConfig;
    }


}
