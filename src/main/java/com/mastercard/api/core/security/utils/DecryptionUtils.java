package com.mastercard.api.core.security.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;

public class DecryptionUtils {

  static Logger logger = LoggerFactory.getLogger(DecryptionUtils.class);

//  public String decryptJsonString(String json) {
//    if(json.isEmpty()) {
//      return "";
//    }
//
//    logger.debug("Decryption started");
//
//    try {
//      // Unmarshall JSON to POJO
//      EncryptedMessage encryptedMessage = om.readValue(json, EncryptedMessage.class);
//
//      logger.debug("JSON string unmarshalled successfully.");
//
//      // Decrypt generated AES key using private key
//      byte[] generatedKey = DecryptionUtils.decrypt(encryptedMessage.getKey(), keyLoader.getDecryptionKey());
//
//      logger.log(Level.DEBUG, "Generated key successfully decrypted using private key.");
//
//      // Decrypt data using decrypted generated key
//      return DecryptionUtils.decrypt(encryptedMessage.getData(), generatedKey);
//    } catch (Exception e) {
//      logger.log(Level.ERROR, "Input message not properly encrypted/formatted.");
//      throw new MRSServiceException(MrsConstants.DEFAULT_ERROR_CODE, "Input message not properly encrypted/formatted.", e);
//    }
//  }
  
  public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws Exception {
    Cipher cipher = Cipher.getInstance(SecurityParameters.RSA_TRANSFORM.name());
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    logger.debug("RSA cipher successfully initialized with private key from HSM.");
    return cipher.doFinal(data);
  }
  
  public static String decrypt(byte[] cipherText, byte[] secretKey) throws Exception {
    Cipher cipher = Cipher.getInstance(SecurityParameters.AES_TRANSFORM.name());
    int offset = secretKey.length/2;
    cipher.init(Cipher.DECRYPT_MODE, 
                  new SecretKeySpec(secretKey, 0, cipher.getBlockSize(), SecurityParameters.AES_ALGORTHM.name()),
                  new IvParameterSpec(secretKey, offset, cipher.getBlockSize()));
    
    logger.debug("AES cipher successfully initialized with generated key on the request.");
    
    byte[] decryptedBytes = cipher.doFinal(cipherText);
    
    logger.debug( "Decryption of request data completed successfully.");
    
    return new String(decryptedBytes);
  }
 
}
