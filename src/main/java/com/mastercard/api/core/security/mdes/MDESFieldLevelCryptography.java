package com.mastercard.api.core.security.mdes;

import com.mastercard.api.core.model.RequestMap;
import com.mastercard.api.core.security.CryptographyInterceptor;
import com.mastercard.api.core.security.util.CryptUtil;
import com.mastercard.api.core.security.util.KeyType;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Created by andrearizzini on 13/05/2016.
 */
public class MDESFieldLevelCryptography implements CryptographyInterceptor {

    private String alias;
    private PublicKey issuerKey;
    private CryptographyContext context;


    public MDESFieldLevelCryptography(CryptographyContext context, InputStream issuerKeyInputStream, String alias, String password)
            throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException,
            KeyStoreException, IOException {
        this.context = context;
        this.alias = alias;
        this.issuerKey = (PublicKey) CryptUtil.loadKey(KeyType.PRIVATE, "PKCS12", issuerKeyInputStream, alias, password);
    }

    @Override public CryptographyContext getContext() {
        return context;
    }

    @Override public RequestMap encrypt(RequestMap map) {
        return null;
    }

    @Override public RequestMap decrypt(RequestMap map) {
        return null;
    }


    public boolean equals(MDESFieldLevelCryptography o) {
        if (this.alias.compareTo(o.alias) == 0 &&
            this.context.name().compareTo(o.context.name()) == 0 ) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public int hashCode() {
        return new StringBuilder(). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(this.alias.toString()).
                append(this.context.name()).toString().hashCode();
    }
}
