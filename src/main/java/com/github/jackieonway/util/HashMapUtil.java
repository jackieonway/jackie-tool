/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util;

/**
 * @author Jackie
 * @version $id: HashMapUtil.java v 0.1 2021-01-13 16:47 Jackie Exp $$
 */
public enum  HashMapUtil {

    /**
     * HashMapUtil 实例
     */
    INSTANCE;
    /**
     * generate default capacity for hashMap  to reduce it resize's count
     * @param size data size
     * @return capacity
     */
    public static int generateCapacity(int size){
        if (size < 0){
            throw new IllegalArgumentException("size can not less than 0");
        }
        if(size >= Integer.MAX_VALUE){
            return Integer.MIN_VALUE;
        }
        int i = (int) ((float) size / 0.75F + 1.0F);
        i |= i >>> 1;
        i |= i >>> 2;
        i |= i >>> 4;
        i |= i >>> 8;
        i |= i >>> 16;
        return i + 1;
    }
}
