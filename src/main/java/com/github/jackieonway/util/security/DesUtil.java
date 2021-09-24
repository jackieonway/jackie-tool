/**
 * ChinaMobile iot Inc.
 * Copyright (c) 2010-2019 All Rights Reserved.
 */
package com.github.jackieonway.util.security;

import java.security.SecureRandom;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Jackie
 * @version \$Id: DesUtil.java, v 0.1 2019-03-15 10:33 Jackie Exp $$
 */
public enum  DesUtil {

	/**
	 * DesUtil 实例
	 */
	INSTANCE;

	private static final String DES_CBC_PKCS_5_PADDING = "DES/CBC/PKCS5Padding";

	private static byte[] eniv = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};
	private static byte[] deiv = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};

	// Password, if the length is a multiple of 8. Note that this is a simple password.
	// Simple application. This password can be used when the requirements are not high.
	/*
		DES is a symmetric encryption algorithm. The so-called symmetric encryption algorithm is: an algorithm that
		uses the same key for encryption and decryption. The DES encryption algorithm originated from IBM research and
		was officially adopted by the US government. It has been widely circulated since then, but it has been used
		less and less in recent years because DES uses 56-bit keys and can be cracked within 24 hours with
		 modern computing power. .
	 */

	private static String password = "9588888888880288";
	/**
	 * Encrypted data
	 * @param data Encrypted data
	 * @return String
	 */
	public static String encrypt(String data) throws Exception {
		byte[] bt = encryptByKey(data.getBytes(), password);
		return Base64.encode(bt);
	}

	public static String encrypt(String data, String password) throws Exception {
		byte[] bt = encryptByKey(data.getBytes(), password);
		return Base64.encode(bt);
	}

	/**
	 * Decrypt data,Convert BASE 64 Encoder to string
	 * @param data Decrypt data
	 * @return String
	 * @throws Exception Exception
	 */
	public static String decryptor(String data) throws Exception {
		byte[] bt = decrypt(Base64.decode(data), password);
		return new String(bt);
	}

	/**
	 * Decrypt data,Convert BASE 64 Encoder to string
	 * @param data Decrypt data
	 * @param password password
	 * @return String
	 */
	public static String decryptor(String data, String password) throws Exception {
		byte[] bt = decrypt(Base64.decode(data), password);
		return new String(bt);
	}

	public static String encryptDESByPKCS5Padding(String encryptString, String encryptKey) throws Exception {
		IvParameterSpec zeroIv = new IvParameterSpec(eniv);
		SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance(DES_CBC_PKCS_5_PADDING);
		cipher.init(1, key, zeroIv);
		byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
		return Base64.encode(encryptedData);
	}

	public static String decryptDESByPKCS5Padding(String decryptString, String decryptKey) throws Exception {
		byte[] byteMi = Base64.decode(decryptString);
		IvParameterSpec zeroIv = new IvParameterSpec(deiv);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance(DES_CBC_PKCS_5_PADDING);
		cipher.init(2, key, zeroIv);
		byte[] decryptedData = cipher.doFinal(byteMi);
		return new String(decryptedData);
	}

	/**
	 * encryption
	 * @param datasource byte[]
	 * @param key String
	 * @return byte[]
	 */
	private static byte[] encryptByKey(byte[] datasource, String key) throws Exception {
		SecureRandom random = new SecureRandom();

		DESKeySpec desKey = new DESKeySpec(key.getBytes());
		//Create a key factory and use it to convert DES Key Spec into
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		//Cipher object actually completes the encryption operation
		Cipher cipher = Cipher.getInstance("DES");
		//Initialize Cipher object with key
		cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
		//Now, get the data and encrypt
		//Formally perform encryption operations
		return cipher.doFinal(datasource);
	}
	/**
	 * Decrypt
	 * @param src byte[]
	 * @param key String
	 * @return byte[]
	 * @throws Exception Exception
	 */
	private static byte[] decrypt(byte[] src, String key) throws Exception {
		// The DES algorithm requires a trusted source of random numbers
		SecureRandom random = new SecureRandom();
		// Create a DES Key Spec object
		DESKeySpec desKey = new DESKeySpec(key.getBytes());
		// Create a key factory
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// Convert DES Key Spec object to Secret Key object
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher object actually completes the decryption operation
		Cipher cipher = Cipher.getInstance("DES");
		// Initialize Cipher object with key
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// Really start the decryption operation
		return cipher.doFinal(src);
	}
}
