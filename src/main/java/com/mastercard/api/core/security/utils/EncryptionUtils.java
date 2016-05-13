package com.mastercard.api.core.security.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;

public class EncryptionUtils {

  static Logger logger = LoggerFactory.getLogger(DecryptionUtils.class);

  /**
   *  - Encrypts data in base 64 using an RSA public key
   *  
   * @param data
   * @param pubk
   * @return
   */
  public byte[] encryptUsingRSA(byte[] data, PublicKey pubk){
    try {
      Cipher cipher = Cipher.getInstance(SecurityParameters.RSA_TRANSFORM.name());
      cipher.init(Cipher.ENCRYPT_MODE, pubk);
      cipher.update(data);
      return cipher.doFinal();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    return null;
  }
  
  public byte[] encryptUsingAES(String clearText, byte[] secretKey){
    try {
      Cipher cipher = Cipher.getInstance(SecurityParameters.AES_TRANSFORM.name());
      int offset = secretKey.length/2;
      IvParameterSpec ivParameterSpec = new IvParameterSpec(secretKey, offset, cipher.getBlockSize());
      SecretKeySpec key = new SecretKeySpec(secretKey, 0, cipher.getBlockSize(), SecurityParameters.AES_ALGORTHM.name());
      cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
      return cipher.doFinal(clearText.getBytes("UTF-8")); 
    } catch(Exception e){
      logger.error(e.getMessage(), e);
    }
    return null;
  }
  
  public byte[] generateRandom(){
    byte[] randomBytes = new byte[16];
    try {
      SecureRandom secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG");  // Create a secure random number generator using the SHA1PRNG algorithm
      secureRandomGenerator.nextBytes(randomBytes);  // Get 16 random bytes
    } catch (NoSuchAlgorithmException e) {
      logger.error(e.getMessage(), e);
    } 
    return randomBytes;
  }
  
  /**
   *  - Generates an AES symmetric key of the given size in bits
   *    - generates the AES key
   *    - generates an IV using SecureRandom
   *    - create a 32 byte array (for a 128 bit key size) and 
   *        - copy the 16 byte key into the first 16 bytes
   *        - copy the IV into the second 16 bytes
   *        
   * @param algorithm
   * @param keySize
   * @return
   */
  public byte[] generateSecretKeyWithIV(String algorithm, int keySize) throws UnsupportedEncodingException {
    byte[] encryptionKey = null;
    KeyGenerator keyGenerator;

    try {
      keyGenerator = KeyGenerator.getInstance(algorithm);
      keyGenerator.init(keySize);
      SecretKey sKey = keyGenerator.generateKey();
      encryptionKey = sKey.getEncoded();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    
    byte[] IV = generateRandom();
    byte[] secretKeywithIV = new byte[(keySize/8)*2];
    System.arraycopy(encryptionKey, 0, secretKeywithIV, 0, encryptionKey.length);
    System.arraycopy(IV, 0, secretKeywithIV, encryptionKey.length, IV.length);
    return secretKeywithIV;
  }



//  private void setupKeys() throws Exception {
//    // Override PKCS#11 private key loader and substitute with our test private key
//    KeyStore serverKs = KeystoreUtils.getKeyStore("ServerStore.jks", "JKS", "changeit");
//    publicKey = KeystoreUtils.getPublicKey(serverKs, "server");
//  }
//
//  public EncryptedField encryptInput(String fieldToEncrypt) throws Exception {
//    setupKeys();
//    byte[] secretKeywithIV = generateSecretKeyWithIV("AES", 128); // Generate 128-bit (16 byte) AES key with 128-bit IV
//    byte[] encryptedInput = encryptUsingAES(fieldToEncrypt, secretKeywithIV);
//    byte[] encryptedKey = encryptUsingRSA(secretKeywithIV, publicKey);
//
//    EncryptedMessage encryptedFieldAndKey = new EncryptedMessage(encryptedKey,
//                                                                 encryptedInput);
//    EncryptedField encryptedField = new EncryptedField();
//    encryptedField.setValue(om.writeValueAsString(encryptedFieldAndKey));
//    encryptedField.setEncrypted(true);
//    return encryptedField;
//  }
  
}