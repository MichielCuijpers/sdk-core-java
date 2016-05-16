package com.mastercard.api.core.security.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Created by andrearizzini on 13/05/2016.
 */
public class CryptUtil {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();


    /**
     * This is the method to conver a byte array to hex string
     * @param bytes
     * @return
     */
    public static String byteArrayToHexString(byte[] bytes) {
        return new String(Hex.encodeHex(bytes));
    }

    /**
     * this is method to convert a hex string to byte array
     * @param s
     * @return
     */
    public static byte[] hexStringToByteArray(String s) throws DecoderException {
        return Hex.decodeHex(s.toCharArray());
    }

    /**
     * this is the method to generate a unique iv
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static IvParameterSpec generateIv() throws NoSuchAlgorithmException {
        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] ivBytes = new byte[16]; //cipher.getBlockSize()
        randomSecureRandom.nextBytes(ivBytes);
        return  new IvParameterSpec(ivBytes);
    }

    /**
     * this is the method to generate AES secret key
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static SecretKey generateSecretKey(String algorithm) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        keyGen.init(256);
        return keyGen.generateKey();
    }


    public static byte[] generateFingerprint(Key key) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(key.getEncoded());
        return digest.digest();
    }


    /**
     * This is the method to used to encrypt or decrypt
     * @param operation Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE
     * @param algorithm Cipher algorithm
     * @param key Key
     * @param iv
     * @param clearText
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */

    public static byte[] crypt(int operation, String algorithm, String securityPrivider, Key key, AlgorithmParameterSpec iv, byte[] clearText)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException,
            NoSuchProviderException {
        Cipher currentCipher = Cipher.getInstance(algorithm, securityPrivider);
        if (iv == null) {
            currentCipher.init(operation, key);
        } else {
            currentCipher.init(operation, key, iv);
        }
        return currentCipher.doFinal(clearText);
    }

    public static byte[] crypt(int operation, String algorithm, Key key, AlgorithmParameterSpec iv, byte[] clearText)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        Cipher currentCipher = Cipher.getInstance(algorithm);
        if (iv == null) {
            currentCipher.init(operation, key);
        } else {
            currentCipher.init(operation, key, iv);
        }
        return currentCipher.doFinal(clearText);
    }

    public static PublicKey loadPublicKey(String instance, InputStream is) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance(instance); //"X.509"
        X509Certificate certificate = (X509Certificate) factory.generateCertificate(is);
        return certificate.getPublicKey();
    }

    public static PrivateKey loadPrivateKey(String instance, InputStream is)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        byte[] keyBytes = getBytesFromInputStream(is);
        PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance(instance);
        return kf.generatePrivate(kspec);
    }

    public static byte[] getBytesFromInputStream(InputStream is) throws IOException
    {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();)
        {
            byte[] buffer = new byte[1024];

            for (int len; (len = is.read(buffer)) != -1;) {
                os.write(buffer, 0, len);
            }
            os.flush();

            return os.toByteArray();
        }
    }


    public static Key loadKey(KeyType type, String keystore, InputStream p12, String alias, String password)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException,
            UnrecoverableKeyException {
        KeyStore ks = KeyStore.getInstance(keystore);
        ks.load(p12, password.toCharArray());
        Key key = ks.getKey(alias, password.toCharArray());
        if (key instanceof PrivateKey) {
            if (type.name().compareTo(KeyType.PRIVATE.name()) == 0) {
                return key;
            } else if (type.name().compareTo(KeyType.PUBLIC.name()) == 0){
                X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
                return cert.getPublicKey();
            }
        }
        return null;
    }

    /**
     * This is the method to used to encrypt or decrypt
     * @param operation Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE
     * @param algorithm Cipher algorithm
     * @param key Key
     * @param iv
     * @param clearText
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] crypt(int operation, String algorithm, Key key, byte[] clearText)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        return crypt(operation, algorithm, key, null, clearText);
    }


}
