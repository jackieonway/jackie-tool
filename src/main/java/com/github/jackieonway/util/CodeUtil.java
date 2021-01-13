package com.github.jackieonway.util;

import com.github.jackieonway.util.security.Base64;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Jackie
 */
public class CodeUtil {
	private CodeUtil() {
	}

	/**
	 * 获取盐值
	 * @return: String
	 */
	public static String getSalt() {
		Random random = new SecureRandom();
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		return Base64.encode(salt);
	}

	/**
	 * 获取num位随机码
	 * @param num 位数
	 * @return: 随机数字符串
	 */
	public static String getRandomCode(int num) {
		StringBuilder digitDivisor = new StringBuilder("1");
		for (int i = 1; i < num; i++) {
			digitDivisor.append("0");
		}
		return Integer.toString( (new SecureRandom().nextInt(9) + 1) * Integer.parseInt(digitDivisor.toString()));

	}

	/**
	 *  获取八位随机码 第一位为大写字母，第二位为小写字母，后六位为数字
	 * @return 随机码
	 */
	public static String getEightRandomCode(){
		return String.valueOf((char) Math.round(Math.random() * 25 + 65)) +
				(char) Math.round(Math.random() * 25 + 97) +
				getRandomCode(6);
	}
}
