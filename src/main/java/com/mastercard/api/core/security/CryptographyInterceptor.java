package com.mastercard.api.core.security;

import org.apache.commons.codec.DecoderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by andrearizzini on 13/05/2016.
 */
public interface CryptographyInterceptor {
    List<String> getTriggeringEndPath();
    Map<String,Object> encrypt(Map<String,Object> map) throws NoSuchAlgorithmException, InvalidKeyException, CertificateEncodingException, InvalidAlgorithmParameterException, NoSuchPaddingException, BadPaddingException, UnsupportedEncodingException, NoSuchProviderException, IllegalBlockSizeException;
    Map<String,Object> decrypt(Map<String,Object> map) throws DecoderException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException;
}



