package com.mastercard.api.core.security.mdes;

import com.mastercard.api.core.exception.SdkException;
import com.mastercard.api.core.security.fle.Config;
import com.mastercard.api.core.security.fle.FieldLevelEncryption;

import java.io.InputStream;

/**
 * Created by andrearizzini on 13/05/2016.
 */
public class MDESCryptography extends FieldLevelEncryption {

    public MDESCryptography(InputStream publicCertificate, InputStream keystore, String privateKeyAlias, String privateKeyPassword)
            throws SdkException {
        super(publicCertificate, keystore, privateKeyAlias, privateKeyPassword, Config.MDES());


    }

    public MDESCryptography(InputStream publicCertificate, InputStream masterCardPrivateKey)
            throws SdkException {
        super(publicCertificate, masterCardPrivateKey, Config.MDES());

    }




}
