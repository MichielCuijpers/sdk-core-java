package com.mastercard.api.core.security.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

/**
 * Created by andrearizzini on 13/05/2016.
 */
public class CryptUtil {

    /**
     *
     * @param json
     * @return
     */
    public static String sanitizeJson(String json) {
        return json.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "").replaceAll(" ", "");
    }

    /**
     * This is the method to conver a byte array to hex string
     * @param bytes
     * @return
     */
    public static String byteArrayToString(byte[] bytes, DataEncoding encoding) {
        if (encoding == DataEncoding.HEX) {
            return new String(Hex.encodeHex(bytes));
        } else {
            return Base64.encodeBase64String(bytes);
        }

    }

    /**
     * this is method to convert a hex string to byte array
     * @param s
     * @return
     */
    public static byte[] stringToByteArray(String s, DataEncoding encoding) throws DecoderException {
        if (encoding == DataEncoding.HEX) {
            return Hex.decodeHex(s.toCharArray());
        } else {
            return Base64.decodeBase64(s);
        }

    }

    /**
     * this is the method to generate a unique iv
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static IvParameterSpec generateIv() throws NoSuchAlgorithmException, NoSuchProviderException {
        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
        byte[] ivBytes = new byte[16]; //cipher.getBlockSize()
        randomSecureRandom.nextBytes(ivBytes);
        return  new IvParameterSpec(ivBytes);
    }


    /**
     * this is the method to generate AES secret key
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static SecretKey generateSecretKey(String algorithm, int size, String digestAlgorithm ) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm, "SunJCE");
        keyGen.init(size);
        MessageDigest messageDigest = MessageDigest.getInstance(digestAlgorithm, "SUN");
        messageDigest.reset();
        byte[] keyValue = messageDigest.digest(keyGen.generateKey().getEncoded());
        keyValue = Arrays.copyOf(keyValue, 16);
        return new SecretKeySpec(keyValue, algorithm);
    }


    /**
     * this is the method to generate AES secret key
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static SecretKey generateSecretKey(String algorithm, String provider,int size) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm, provider);
        keyGen.init(size);
        return keyGen.generateKey();
    }


    public static byte[] generateFingerprint(String algorithm, Key key) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        messageDigest.reset();
        messageDigest.update(key.getEncoded());
        return messageDigest.digest();
    }

    public static byte[] generateFingerprint(String algorithm, Certificate certificate) throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        messageDigest.reset();
        messageDigest.update(certificate.getEncoded());
        return messageDigest.digest();
    }

    /**
     *
     * @param operation
     * @param algorithm
     * @param key
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
    public static byte[] crypt(int operation, String algorithm, Key key, AlgorithmParameterSpec iv, byte[] clearText)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {

        Cipher currentCipher = null;

        currentCipher = Cipher.getInstance(algorithm);

        if (iv == null) {
            currentCipher.init(operation, key);
        } else {
            currentCipher.init(operation, key, iv);
        }

        return currentCipher.doFinal(clearText);
    }

    /**
     *
     * @param algorithm
     * @param provider
     * @param key
     * @param keyToWrap
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchProviderException
     */
    public static byte[] wrap(String algorithm, String provider, Key key, Key keyToWrap)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException,
            NoSuchProviderException {

        Cipher currentCipher = null;



        if (provider != null) {
            currentCipher = Cipher.getInstance(algorithm, provider);
        } else {
            currentCipher = Cipher.getInstance(algorithm);
        }

        if (algorithm.contains("OAEPWith")) {
            currentCipher.init(Cipher.WRAP_MODE, key, getOAEPParameterSpec(algorithm, null));
        } else {
            currentCipher.init(Cipher.WRAP_MODE, key);
        }



        return currentCipher.wrap(keyToWrap);
    }

    private static OAEPParameterSpec getOAEPParameterSpec(String algorithm, String oaepHashingAlgorithm) {
        if (algorithm.contains("OAEPWith")) {

            //OAEPWith<digest>And<mgf>Padding
            // String template = "OAEPWith<digest>And<mgf>Padding";
            int startDigest = algorithm.indexOf("OAEPWith") + 8;
            int endDigest = algorithm.indexOf("And");

            int startPadding = endDigest + 3;
            int endPadding = algorithm.indexOf("Padding");


            String digest = algorithm.substring(startDigest, endDigest);
            String padding = algorithm.substring(startPadding, endPadding);

            if (oaepHashingAlgorithm != null) {
                return new OAEPParameterSpec(digest, padding, new MGF1ParameterSpec(oaepHashingAlgorithm), PSource.PSpecified.DEFAULT);
            } else {
                return new OAEPParameterSpec(digest, padding, new MGF1ParameterSpec(digest), PSource.PSpecified.DEFAULT);
            }
        }

        return null;
    }

    /**
     *
     *
     * @param algorithm
     * @param provider
     * @param key
     * @param keyToUnwrap
     * @param keyAlgorithmToUnwrap
     * @param keyTypeToUnwrap
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchProviderException
     */
    public static Key unwrap(String algorithm, String provider, Key key, byte[] keyToUnwrap, String keyAlgorithmToUnwrap, int keyTypeToUnwrap)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException,
            NoSuchProviderException {

        Cipher currentCipher = null;

        if (provider != null) {
            currentCipher = Cipher.getInstance(algorithm, provider);
        } else {
            currentCipher = Cipher.getInstance(algorithm);
        }


        if (algorithm.contains("OAEPWith")) {
            currentCipher.init(Cipher.UNWRAP_MODE, key, getOAEPParameterSpec(algorithm, null));
        } else {
            currentCipher.init(Cipher.UNWRAP_MODE, key);
        }
        return currentCipher.unwrap(keyToUnwrap, keyAlgorithmToUnwrap, keyTypeToUnwrap);
    }

    /**
     *
     *
     * @param algorithm
     * @param provider
     * @param key
     * @param keyToUnwrap
     * @param keyAlgorithmToUnwrap
     * @param oaepHashingAlgorithm
     * @param keyTypeToUnwrap
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchProviderException
     */
    public static Key unwrap(String algorithm, String oaepHashingAlgorithm, String provider, Key key, byte[] keyToUnwrap, String keyAlgorithmToUnwrap, int keyTypeToUnwrap)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException,
            NoSuchProviderException {

        Cipher currentCipher = null;

        if (provider != null) {
            currentCipher = Cipher.getInstance(algorithm, provider);
        } else {
            currentCipher = Cipher.getInstance(algorithm);
        }


        if (algorithm.contains("OAEPWith")) {
            currentCipher.init(Cipher.WRAP_MODE, key, getOAEPParameterSpec(algorithm, oaepHashingAlgorithm));
        } else {
            currentCipher.init(Cipher.UNWRAP_MODE, key);
        }
        return currentCipher.unwrap(keyToUnwrap, keyAlgorithmToUnwrap, keyTypeToUnwrap);
    }



    /**
     *
     * @param operation
     * @param algorithm
     * @param provider
     * @param key
     * @param iv
     * @param clearText
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchProviderException
     */

    public static byte[] crypt(int operation, String algorithm, String provider, Key key, AlgorithmParameterSpec iv, byte[] clearText)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException,
            NoSuchProviderException {

        if (operation == Cipher.UNWRAP_MODE || operation == Cipher.WRAP_MODE) {
            throw new InvalidAlgorithmParameterException("Cannot use Wrap/UnWrap in a crypt method");
        }

        Cipher currentCipher = null;

        if (provider != null) {
            currentCipher = Cipher.getInstance(algorithm, provider);
        } else {
            currentCipher = Cipher.getInstance(algorithm);
        }

        if (iv == null) {
            currentCipher.init(operation, key);
        } else {
            currentCipher.init(operation, key, iv);
        }

        return currentCipher.doFinal(clearText);
    }

    /**
     *
     *
     * @param decryptMode
     * @param algorithm
     * @param provider
     * @param key
     * @param keyToUnwrap
     * @param keyAlgorithmToUnwrap
     * @param keyTypeToUnwrap
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchProviderException
     */
    public static Key unwrap(int decryptMode, String algorithm, String provider, Key key, byte[] keyToUnwrap, String keyAlgorithmToUnwrap, int keyTypeToUnwrap)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException,
            NoSuchProviderException {

        Cipher currentCipher = null;

        if (provider != null) {
            currentCipher = Cipher.getInstance(algorithm, provider);
        } else {
            currentCipher = Cipher.getInstance(algorithm);
        }

        currentCipher.init(Cipher.UNWRAP_MODE, key);


        return currentCipher.unwrap(keyToUnwrap, keyAlgorithmToUnwrap, keyTypeToUnwrap);
    }



    public static Certificate loadCertificate(String instance, InputStream is) throws CertificateException, NoSuchProviderException {
        CertificateFactory factory = CertificateFactory.getInstance(instance, "SUN"); //"X.509"
        return factory.generateCertificate(is);
    }


    public static Certificate loadCertificate(String instance, String provider, InputStream is) throws CertificateException, NoSuchProviderException {
        CertificateFactory factory = CertificateFactory.getInstance(instance, provider); //"X.509"
        return factory.generateCertificate(is);
    }

    public static PrivateKey loadPrivateKey(String instance, InputStream is)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, NoSuchProviderException {
        byte[] keyBytes = getBytesFromInputStream(is);
        KeyFactory kf = KeyFactory.getInstance(instance, "SunRsaSign");
        PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(keyBytes);
        return kf.generatePrivate(kspec);
    }

    public static PrivateKey loadPrivateKey(String instance, String provider, InputStream is)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, NoSuchProviderException {
        byte[] keyBytes = getBytesFromInputStream(is);
        KeyFactory kf = KeyFactory.getInstance(instance, provider);
        PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(keyBytes);
        return kf.generatePrivate(kspec);
    }

    public static byte[] getBytesFromInputStream(InputStream is) throws IOException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        for (int len; (len = is.read(buffer)) != -1;) {
            os.write(buffer, 0, len);
        }
        os.flush();

        return os.toByteArray();
    }

    public static Key loadKey(KeyType type, String instance, InputStream p12, String alias, String password)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException,
            UnrecoverableKeyException {
        KeyStore ks = KeyStore.getInstance(instance);
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


    public static Key loadKey(KeyType type, String instance, String provider, InputStream p12, String alias, String password)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException,
            UnrecoverableKeyException, NoSuchProviderException {

        KeyStore ks;
        if (provider == null)
        {
          ks = KeyStore.getInstance(instance);
        } else {
            ks = KeyStore.getInstance(instance,provider);
        }

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



}
