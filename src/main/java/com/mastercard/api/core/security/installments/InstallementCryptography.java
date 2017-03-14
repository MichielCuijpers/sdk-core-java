package com.mastercard.api.core.security.installments;

import com.mastercard.api.core.exception.SdkException;
import com.mastercard.api.core.security.fle.Config;
import com.mastercard.api.core.security.fle.FieldLevelEncryption;

import java.io.InputStream;

/**
 * Created by andrearizzini on 13/05/2016.
 */
public class InstallementCryptography extends FieldLevelEncryption {

    public InstallementCryptography(InputStream publicCertificate, InputStream keystore, String privateKeyAlias, String privateKeyPassword)
            throws SdkException {
        super(publicCertificate, keystore, privateKeyAlias, privateKeyPassword, Config.Installments());


    }

    public InstallementCryptography(InputStream publicCertificate, InputStream masterCardPrivateKey)
            throws SdkException {
        super(publicCertificate, masterCardPrivateKey, Config.Installments());

    }




}
