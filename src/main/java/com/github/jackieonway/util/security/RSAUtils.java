package com.github.jackieonway.util.security;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA encryption and decryption tools
 * @author Jackie
 */
public class RSAUtils {

	private static final String ENCOUNTERED_AN_EXCEPTION = "] encountered an exception";

	private RSAUtils() {
	}

	private static final String RSA_ALGORITHM = "RSA";

	public static Map<String, String> createKeys(int keySize) {
		// Create a Key Pair Generator object for the RSA algorithm
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
		}

		// Initialize Key Pair Generator object, key length
		kpg.initialize(keySize);
		// Generate key pair
		KeyPair keyPair = kpg.generateKeyPair();
		// Get public key
		Key publicKey = keyPair.getPublic();
		String publicKeyStr = Base64.encode(publicKey.getEncoded());
		// Get private key
		Key privateKey = keyPair.getPrivate();
		String privateKeyStr = Base64.encode(privateKey.getEncoded());
		Map<String, String> keyPairMap = new HashMap<>();
		keyPairMap.put("publicKey", publicKeyStr);
		keyPairMap.put("privateKey", privateKeyStr);

		return keyPairMap;
	}

	/**
	 * Get public key
	 * @param publicKey Key string（Encoded by base 64）
	 */
	public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// Obtain the public key object through the X509 encoded Key instruction
		KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
		X509EncodedKeySpec x509KeySpec =  new X509EncodedKeySpec(Base64.decode(publicKey));
		return (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
	}

	/**
	 * Get private key
	 * @param privateKey Key string（Encoded by base 64）
	 */
	public static RSAPrivateKey getPrivateKey(String privateKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// Obtain the private key object through the PKCS#8 encoded Key instruction
		KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
		return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
	}

	/**
	 * Public key encryption
	 * @param data data
	 * @param publicKey public key
	 * @return Encrypted content
	 */
	public static String publicEncrypt(String data, String publicKey) {
		try {
			RSAPublicKey pubKey = getPublicKey(publicKey);
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			return Base64.encode(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(StandardCharsets.UTF_8),
					pubKey.getModulus().bitLength()));
		} catch (Exception e) {
			throw new IllegalStateException("Encrypted string[" + data + ENCOUNTERED_AN_EXCEPTION, e);
		}
	}


	/**
	 * Public key decryption
	 * @param data data
	 * @param publicKey public key
	 * @return Decrypt content
	 */

	public static String publicDecrypt(String data, String publicKey) {
		try {
			RSAPublicKey pubKey = getPublicKey(publicKey);
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, pubKey);
			return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decode(data),
					pubKey.getModulus().bitLength()), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new IllegalStateException("An exception was encountered while decrypting the string [" + data + "]", e);
		}
	}


	/**
	 * Private key decryption
	 * @param data data
	 * @param privateKey private key
	 * @return Decrypt content
	 */

	public static String privateDecrypt(String data, String privateKey) {
		try {
			RSAPrivateKey priKey = getPrivateKey(privateKey);
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, priKey);
			return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decode(data),
					priKey.getModulus().bitLength()), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new IllegalStateException("An exception was encountered while decrypting the string [" + data + "]", e);
		}
	}

	/**
	 * Private key encryption
	 * @param data data
	 * @param privateKey private key
	 * @return Encrypted content
	 */

	public static String privateEncrypt(String data, String privateKey) {
		try {
			RSAPrivateKey priKey = getPrivateKey(privateKey);
			Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, priKey);
			return Base64.encode(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(StandardCharsets.UTF_8),
					priKey.getModulus().bitLength()));
		} catch (Exception e) {
			throw new IllegalStateException("Encryption string [" + data + ENCOUNTERED_AN_EXCEPTION, e);
		}
	}

	/**
	 * Private key encryption
	 * @param data data
	 * @param privateKey private key
	 * @return Encrypted content
	 */
    public static String privateEncryptSha1WithRsa(String data, String privateKey) {
        try {
			RSAPrivateKey priKey = getPrivateKey(privateKey);
			Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(priKey);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return Base64.encode(signature.sign());
        } catch (Exception e) {
            throw new IllegalStateException("Encryption string [" + data + ENCOUNTERED_AN_EXCEPTION, e);
        }
    }

    public static boolean verify(String content, String sign, String publicKey) {
        try {
            KeyFactory e = KeyFactory.getInstance(RSA_ALGORITHM);
            PublicKey pubKey = e.generatePublic(new X509EncodedKeySpec(Base64.decode(publicKey)));
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.decode(sign));
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
		int maxBlock = 0;
		if (opmode == Cipher.DECRYPT_MODE) {
			maxBlock = keySize / 8;
		} else {
			maxBlock = keySize / 8 - 11;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] buff;
		int i = 0;
		try {
			while (datas.length > offSet) {
				if (datas.length - offSet > maxBlock) {
					buff = cipher.doFinal(datas, offSet, maxBlock);
				} else {
					buff = cipher.doFinal(datas, offSet, datas.length - offSet);
				}
				out.write(buff, 0, buff.length);
				i++;
				offSet = i * maxBlock;
			}
		} catch (Exception e) {
			throw new IllegalStateException("An exception occurred while encrypting and decrypting the threshold [" + maxBlock + "] data"
					, e);
		}
		byte[] resultDatas = out.toByteArray();
		IOUtils.closeQuietly(out);
		return resultDatas;
	}

	public static void main(String[] args) {
		Map<String, String> keys = createKeys(2048);
		String publicKey = keys.get("publicKey");
		String privateKey = keys.get("privateKey");
		System.out.println("publicKey: "+ publicKey);
		System.out.println("privateKey: "+ privateKey);
		String str = "123456ABCDabcd";
		System.out.println("-------------");
		String publicEncrypt = publicEncrypt(str, publicKey);
		System.out.println(publicEncrypt);
		String s = privateDecrypt(publicEncrypt, privateKey);
		System.out.println(s);
		System.out.println("-------------");
		String privateEncrypt = privateEncrypt(str, privateKey);
		System.out.println(privateEncrypt);
		String ss = publicDecrypt(privateEncrypt, publicKey);
		System.out.println(ss);

	}
}
