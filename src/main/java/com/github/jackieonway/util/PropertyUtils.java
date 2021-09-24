
package com.github.jackieonway.util;

/**
 * 属性工具类
 *
 * @author Herbert
 */
public enum  PropertyUtils {
    /**
     * PropertyUtils 实例
     */
    INSTANCE;
    public static String getValue(String name) {
        String value = System.getProperty(name);
        if (StringUtils.isEmpty(value)) {
            return "";
        } else {
            return value;
        }
    }

    /**
     * 不带/
     *
     * @param name property name
     * @return property value
     */
    public static String getValue1(String name) {
        String value = System.getProperty(name);
        if (StringUtils.isEmpty(value)) {
            return "";
        } else {
            if (value.endsWith(String.valueOf(java.io.File.separatorChar))) {
                value = value.substring(0, value.length() - 1);
            }
            return value;
        }
    }
}
