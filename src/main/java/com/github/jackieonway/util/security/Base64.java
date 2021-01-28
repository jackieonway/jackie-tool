package com.github.jackieonway.util.security;

import com.github.jackieonway.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public enum  Base64 {
    /**
     * Base64 实例
     */
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(Base64.class);
    private static final char[] LEGAL_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    public static String encode(String data, String charset) {
        try {
            return encode(data.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("base 64 encode error", e);
            return StringUtils.EMPTY;
        }
    }

    public static String encode(byte[] data) {
        int start = 0;
        int len = data.length;
        StringBuilder buf = new StringBuilder(data.length * 3 / 2);
        int end = len - 3;
        int i = start;
        int n = 0;

        int d;
        while(i <= end) {
            d = (data[i] & 255) << 16 | (data[i + 1] & 255) << 8 | data[i + 2] & 255;
            buf.append(LEGAL_CHARS[d >> 18 & 63]);
            buf.append(LEGAL_CHARS[d >> 12 & 63]);
            buf.append(LEGAL_CHARS[d >> 6 & 63]);
            buf.append(LEGAL_CHARS[d & 63]);
            i += 3;
            if (n++ >= 14) {
                n = 0;
                buf.append(" ");
            }
        }

        if (i == start + len - 2) {
            d = (data[i] & 255) << 16 | (data[i + 1] & 255) << 8;
            buf.append(LEGAL_CHARS[d >> 18 & 63]);
            buf.append(LEGAL_CHARS[d >> 12 & 63]);
            buf.append(LEGAL_CHARS[d >> 6 & 63]);
            buf.append("=");
        } else if (i == start + len - 1) {
            d = (data[i] & 255) << 16;
            buf.append(LEGAL_CHARS[d >> 18 & 63]);
            buf.append(LEGAL_CHARS[d >> 12 & 63]);
            buf.append("==");
        }

        return buf.toString();
    }

    public static String decode(String s, String charset) {
        try {
            return new String(decode(s),charset);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("base 64 decode error", e);
            return StringUtils.EMPTY;
        }
    }

    public static byte[] decode(String s) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            decode(s, bos);
        } catch (IOException var5) {
            throw new IllegalArgumentException();
        }

        byte[] decodedBytes = bos.toByteArray();

        try {
            bos.close();
        } catch (IOException var4) {
            System.err.println("Error while decoding BASE64: " + var4.toString());
        }

        return decodedBytes;
    }

    private static void decode(String s, OutputStream os) throws IOException {
        int i = 0;
        int len = s.length();

        while(true) {
            while(i < len && s.charAt(i) <= ' ') {
                ++i;
            }

            if (i == len) {
                break;
            }

            int tri = (decode(s.charAt(i)) << 18) + (decode(s.charAt(i + 1)) << 12) +
                    (decode(s.charAt(i + 2)) << 6) + decode(s.charAt(i + 3));
            os.write(tri >> 16 & 255);
            if (s.charAt(i + 2) == '=') {
                break;
            }

            os.write(tri >> 8 & 255);
            if (s.charAt(i + 3) == '=') {
                break;
            }

            os.write(tri & 255);
            i += 4;
        }

    }

    private static int decode(char c) {
        if (c >= 'A' && c <= 'Z') {
            return c - 65;
        } else if (c >= 'a' && c <= 'z') {
            return c - 97 + 26;
        } else if (c >= '0' && c <= '9') {
            return c - 48 + 26 + 26;
        } else {
            switch(c) {
            case '+':
                return 62;
            case '/':
                return 63;
            case '=':
                return 0;
            default:
                throw new IllegalArgumentException("unexpected code: " + c);
            }
        }
    }
}
