package com.github.jackieonway.util.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Jackie
 */
public class Md5Utils {
    private static final Logger logger = LoggerFactory.getLogger(Md5Utils.class);

    private static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private Md5Utils() {
    }

    public static String md5(String text) {
        MessageDigest msgDigest;

        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var5) {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }
        msgDigest.update(text.getBytes(StandardCharsets.UTF_8));
        byte[] bytes = msgDigest.digest();
        return (new String(encodeHex(bytes))).toLowerCase();
    }

    public static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        int i = 0;

        for (int var4 = 0; i < l; ++i) {
            out[var4++] = DIGITS[(240 & data[i]) >>> 4];
            out[var4++] = DIGITS[15 & data[i]];
        }

        return out;
    }


    public static String getFileStreamMd5(File file) throws IOException {
        FileInputStream in = null;
        FileChannel channel = null;
        MappedByteBuffer byteBuffer = null;
        StringBuilder value = new StringBuilder();
        try {
            in = new FileInputStream(file);
            channel = in.getChannel();
            byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            value = byteToStringBuilder(md5.digest());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (null != byteBuffer) {
                unmap(byteBuffer);
            }
            if (null != channel) {
                channel.close();
            }
            if (null != in) {
                in.close();
            }
        }
        return value.toString();
    }

    private static void unmap(MappedByteBuffer byteBuffer) {
        Cleaner cleaner = ((DirectBuffer) byteBuffer).cleaner();
        if (cleaner != null) {
            cleaner.clean();
        }
    }

    public static String getFileStreamMd5(MultipartFile multipartFile) throws IOException {
        StringBuilder value = new StringBuilder();
        FileInputStream in = null;
        FileChannel channel = null;
        MappedByteBuffer byteBuffer = null;
        try {
            in = (FileInputStream) multipartFile.getInputStream();
            channel = in.getChannel();
            byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, multipartFile.getSize());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
           value = byteToStringBuilder(md5.digest());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                in.close();
            }
            if (channel != null) {
                channel.close();
            }
            if (byteBuffer != null) {
                byteBuffer.clear();
            }
        }
        return value.toString();
    }

    private static StringBuilder byteToStringBuilder(byte[] bytes){
        StringBuilder value = new StringBuilder();
        for (byte b : bytes) {
            String str = Integer.toHexString(0xff & b);
            if (str.length() < 2) {
                value.append("0");
            }
            value.append(str);
        }
        return value;
    }
}