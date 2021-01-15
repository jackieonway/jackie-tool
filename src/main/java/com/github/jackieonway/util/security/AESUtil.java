package com.github.jackieonway.util.security;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *  AES encryption tools
 * @version V1.0
 */
public enum  AESUtil {
    /**
     * AESUtil 实例
     */
    INSTANCE;
    private static final String KEY_ALGORITHM = "AES";
    //Default encryption algorithm
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * AES 加密操作
     *
     * @param content 待加密内容
     * @param password 加密密码
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content, String password) {
        try {
            // Create a cipher
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
            // Cipher initialized to encryption mode
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));
            // encrypt
            byte[] result = cipher.doFinal(byteContent);
            //Return via Base 64 transcoding
            return Base64.encodeBase64String(result);
        } catch (Exception ex) {
            Logger.getLogger(AESUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * AES decryption operation
     *
     * @param content content
     * @param password password
     * @return
     */
    public static String decrypt(String content, String password) {

        try {
            //Instantiation
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

            //Initialize with key and set to decrypt mode
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password));

            //Perform operation
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));

            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            Logger.getLogger(AESUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    /**
     * Generate encryption key
     *
     * @return
     */
    private static SecretKeySpec getSecretKey(final String password) {
        //Returns the KeyGenerator object that generates the specified algorithm key generator
        KeyGenerator kg = null;

        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);

            //AES requires a key length of 128
            kg.init(128, new SecureRandom(password.getBytes()));

            //Generate a key
            SecretKey secretKey = kg.generateKey();
            // Convert to AES private key
            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AESUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}