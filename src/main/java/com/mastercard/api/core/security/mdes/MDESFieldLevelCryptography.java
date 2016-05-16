package com.mastercard.api.core.security.mdes;

import com.mastercard.api.core.model.RequestMap;
import com.mastercard.api.core.security.CryptographyContext;
import com.mastercard.api.core.security.CryptographyInterceptor;
import com.mastercard.api.core.security.util.CryptUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.simple.JSONValue;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Map;

/**
 * Created by andrearizzini on 13/05/2016.
 */
public class MDESFieldLevelCryptography implements CryptographyInterceptor {

    private PublicKey issuerKey;
    private CryptographyContext context;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }


    public MDESFieldLevelCryptography(CryptographyContext context, InputStream issuerKeyInputStream)
            throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException,
            KeyStoreException, IOException {
        this.context = context;
        this.issuerKey = (PublicKey) CryptUtil.loadPublicKey("X.509", issuerKeyInputStream);
    }

    @Override public CryptographyContext getContext() {
        return context;
    }

    @Override public Map<String,Object> encrypt(Map<String,Object> map) {

        if(map.containsKey("cardInfo")) {
            // 1) extract the encryptedData from map
            Map<String,Object> encryptedDataMap =  (Map<String,Object>)  map.remove("cardInfo");

            // 2) create json string
            String payload = JSONValue.toJSONString(map);
            // 3) escaping the string
            payload = payload.replaceAll("\n", "").replaceAll("\r", "").replaceAll("    ", "");

            try {
                // 4) generate random iv
                IvParameterSpec iv = CryptUtil.generateIv();
                String hexIv = CryptUtil.byteArrayToHexString(iv.getIV());

                // 5) generate AES SecretKey
                SecretKey secretKey = CryptUtil.generateSecretKey("AES");

                // 6) encrypt payload
                byte[] encryptedData = CryptUtil.crypt(Cipher.ENCRYPT_MODE, "AES/CBC/PKCS7Padding", "BC", secretKey, iv, payload.getBytes());
                String hexEncryptedData = CryptUtil.byteArrayToHexString(encryptedData);

                // 7) encrypt secretKey with issuer key
                byte[] encryptedSecretKey = CryptUtil.crypt(Cipher.ENCRYPT_MODE, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding", this.issuerKey, secretKey.getEncoded());
                String hexEncryptedKey = CryptUtil.byteArrayToHexString(encryptedSecretKey);

                RequestMap encryptedMap = new RequestMap();
                encryptedMap.put("publicKeyFingerprint", "c91de1c58b82616ebf581fb603ff63a0f440f8e8f65772039f0838b24dc63a9c");
                encryptedMap.put("encryptedKey", hexEncryptedKey);
                encryptedMap.put("encryptedData", hexEncryptedData);
                encryptedMap.put("oaepHashingAlgorithm", "SHA256");
                encryptedMap.put("iv", hexIv);
                map.put("cardInfo", encryptedMap);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return map;

    }

    @Override public Map<String,Object> decrypt(Map<String,Object> map) {
        return null;
    }


    public boolean equals(MDESFieldLevelCryptography o) {
        if (this.issuerKey.getAlgorithm().compareTo(o.issuerKey.getAlgorithm()) == 0 &&
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
                append(this.issuerKey.getAlgorithm().hashCode()).
                append(this.context.name()).toString().hashCode();
    }
}
