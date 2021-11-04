/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.export.excel;

import com.github.jackieonway.util.collection.CollectionUtils;
import com.github.jackieonway.util.export.ExportException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jackie
 * @version $id: ExcelTools.java v 0.1 2021-11-03 14:04 Jackie Exp $$
 */
public enum ExcelTools {


    /**
     * ExcelTools.java instance
     */
    INSTANCE;

    private static final Map<String, Map<String, Object>> CLASS_FILE_CACHE = new ConcurrentHashMap<>(512);

    private static final Map<String, Map<Integer, Map<String, Object>>> CLASS_FIELD_CACHE =
            new ConcurrentHashMap<>(512);

    public static final String HEIGHT = "height";
    public static final String BOLD = "bold";
    public static final String ITALIC = "italic";
    public static final String COLOR = "color";
    public static final String FONT_NAME = "fontName";
    public static final String FONT_SIZE = "fontSize";
    public static final String TYPE = "type";
    public static final String FIELD_NAME = "fieldName";
    public static final String WIDTH = "width";
    public static final String FIELD = "field";
    public static final String FORMAT = "format";

    public static Map<String, Object> putClassFileAndGet(Class<?> clazz){
        Map<String, Object> excelFileMap = CLASS_FILE_CACHE.get(clazz.getName());
        if (CollectionUtils.isEmpty(excelFileMap)) {
            synchronized (INSTANCE) {
                ExcelFile excelFile = clazz.getAnnotation(ExcelFile.class);
                excelFileMap = new HashMap<>();
                putCommonMap(excelFileMap, excelFile.height(), excelFile.bold(), excelFile.italic(),
                        excelFile.color(), excelFile.fontName(), excelFile.fontSize());
                excelFileMap.put("sheetName", excelFile.sheetName());
                excelFileMap.put(TYPE, excelFile.type());
                CLASS_FILE_CACHE.put(clazz.getName(), excelFileMap);
            }
        }
        return excelFileMap;
    }

    public static <T> Map<Integer, Map<String, Object>> putAndGetFields(Class<T> clazz) {
        Map<Integer, Map<String, Object>> headers = CLASS_FIELD_CACHE.get(clazz.getName());
        if (CollectionUtils.isEmpty(headers)) {
            synchronized (INSTANCE) {
                Field[] declaredFields = clazz.getDeclaredFields();
                headers = getHeaders(declaredFields,clazz.getName());
            }
        }
        return headers;
    }

    private static Map<Integer, Map<String, Object>> getHeaders(Field[] declaredFields,String className) {
        Map<Integer,Map<String,Object>> headers = new TreeMap<>();
        for(Field declaredField : declaredFields){
            if(declaredField.isAnnotationPresent(ExcelField.class)){
                if (!declaredField.isAnnotationPresent(ExcelIndex.class)) {
                    throw new ExportException("Export excel can not find annotation ExcelIndex");
                }
                Map<String, Object> map = new HashMap<>();
                ExcelIndex excelIndex = declaredField.getAnnotation(ExcelIndex.class);
                ExcelField excelField = declaredField.getAnnotation(ExcelField.class);
                putCommonMap(map, excelField.height(), excelField.bold(), excelField.italic(),
                        excelField.color(), excelField.fontName(), excelField.fontSize());
                map.put(WIDTH, excelField.width());
                map.put(FIELD_NAME, excelField.fieldName());
                map.put(FIELD,declaredField);
                map.put(FORMAT,excelField.format());
                headers.put(excelIndex.index(),map);
            }
        }
        if (CollectionUtils.isEmpty(headers)){
            throw new ExportException("can not find any excel field!");
        }
        CLASS_FIELD_CACHE.put(className, headers);
        return headers;
    }

    private static void putCommonMap(Map<String, Object> map, short height, boolean bold,
                                     boolean italic, short color, String s, short i) {
        map.put(HEIGHT, height);
        map.put(BOLD, bold);
        map.put(ITALIC, italic);
        map.put(COLOR, color);
        map.put(FONT_NAME, s);
        map.put(FONT_SIZE, i);
    }
}
