package com.github.jackieonway.util;


import java.security.SecureRandom;

public enum  PasswordUtil {
    /**
     * PasswordUtil 实例
     */
    INSTANCE;

    private static final String[] WORD = {
            "a", "b", "c", "d", "e", "f", "g",
            "h", "j", "k", "m", "n",
            "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G",
            "H", "J", "K", "M", "N",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };

    private static final String[] NUM = {
            "2", "3", "4", "5", "6", "7", "8", "9"
    };

    private static final  String[] CHARSET = {
            "%","$","#",".","?","@","&","*","!","~"
    };

    public static String randomPassword() {
        StringBuilder stringBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        int length = random.nextInt(8) + 8;
        randomString(stringBuilder, random, length);
        return stringBuilder.toString();
    }

    /**
     * 随机生成8位到 (8+len)位长度密码
     * @param len 后续长度
     * @return
     */
    public static String randomPassword(Integer len) {
        StringBuilder stringBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        int length = random.nextInt(len) + 8;
        randomString(stringBuilder, random, length);
        return stringBuilder.toString();
    }

    public static String randomString(Integer length) {
        StringBuilder stringBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int i1 = random.nextInt(2);
           if (i1 == 0){
               stringBuilder.append(NUM[random.nextInt(NUM.length)]);
           }else {
               stringBuilder.append(WORD[random.nextInt(WORD.length)]);
           }
        }
        return stringBuilder.toString();
    }

    private static void randomString(StringBuilder stringBuilder, SecureRandom random, int length) {
        for (int i = 0; i < length; i++) {
            int i1 = random.nextInt(3);
            if (i1 == 0) {
                stringBuilder.append(NUM[random.nextInt(NUM.length)]);
            } else if (i1 == 1) {
                stringBuilder.append(WORD[random.nextInt(WORD.length)]);
            } else {
                stringBuilder.append(CHARSET[random.nextInt(CHARSET.length)]);
            }
        }
    }
}