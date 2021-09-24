package com.github.jackieonway.util;

import java.util.Collection;

public enum  ArrayUtils {
    /**
     * ArrayUtils 实例
     */
    INSTANCE;

    /**
         * 将数组转换成"1,2,3..."的字符串格式
         *
         * @param <T> type of array
         * @param ts 数据
         * @param connector 连接符
         * @return 字符串
         */
        public static <T> String toString(T[] ts, String connector) {
            StringBuilder b = new StringBuilder();
            for (T id : ts) {
                b.append(id);
                b.append(connector);
            }
            b.deleteCharAt(b.lastIndexOf(connector));
            return b.toString();
        }

        /**
         * 将List转换成"1,2,3..."的字符串格式
         *
         * @param <T> type of collection
         * @param collection 对象集合
         * @param connector 连接符
         * @return 字符串
         */
        public static <T> String toString(Collection<T> collection, String connector) {
            StringBuilder b = new StringBuilder();
            for (T element : collection) {
                b.append(element);
                b.append(connector);
            }
            b.deleteCharAt(b.lastIndexOf(connector));
            return b.toString();
        }
}