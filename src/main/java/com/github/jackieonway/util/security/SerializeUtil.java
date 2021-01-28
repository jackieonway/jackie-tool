package com.github.jackieonway.util.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Jackie
 */
public enum  SerializeUtil {

    /**
     * AESUtil 实例
     */
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(SerializeUtil.class);

    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;

        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (Exception var14) {
            LOGGER.error("serializable error, {}", var14.getMessage(), var14);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException var13) {
                LOGGER.error("serializable error, {}", var13.getMessage(), var13);
            }

        }

        return new byte[0];
    }

    public static Object deserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;

        try {
            bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception var13) {
            LOGGER.error("serializable error", var13);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (bais != null) {
                    bais.close();
                }
            } catch (IOException var12) {
                LOGGER.error("serializable error", var12);
            }

        }

        return null;
    }
}
