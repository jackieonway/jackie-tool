/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.export;

import com.github.jackieonway.util.collection.CollectionUtils;
import com.github.jackieonway.util.export.excel.ExcelField;
import com.github.jackieonway.util.export.excel.ExcelFile;
import com.github.jackieonway.util.export.excel.ExcelIndex;

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

    private static final String HEIGHT = "height";
    private static final String BOLD = "bold";
    private static final String ITALIC = "italic";
    private static final String COLOR = "color";
    private static final String FONT_NAME = "fontName";
    private static final String FONT_SIZE = "fontSize";
    private static final String TYPE = "type";
    private static final String FIELD_NAME = "fieldName";
    private static final String WIDTH = "width";
    private static final String FIELD = "field";

    public static Map<String, Object> putClassFileAndGet(Class<?> clazz){
        Map<String, Object> excelFileMap = CLASS_FILE_CACHE.get(clazz.getName());
        if (CollectionUtils.isEmpty(excelFileMap)) {
            synchronized (INSTANCE) {
                ExcelFile excelFile = clazz.getAnnotation(ExcelFile.class);
                excelFileMap = new HashMap<>();
                excelFileMap.put("sheetName", excelFile.sheetName());
                excelFileMap.put(HEIGHT, excelFile.height());
                excelFileMap.put(BOLD, excelFile.bold());
                excelFileMap.put(ITALIC, excelFile.italic());
                excelFileMap.put(COLOR, excelFile.color());
                excelFileMap.put(FONT_NAME, excelFile.fontName());
                excelFileMap.put(FONT_SIZE, excelFile.fontSize());
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
                map.put(WIDTH, excelField.width());
                map.put(FIELD_NAME, excelField.fieldName());
                map.put(HEIGHT, excelField.height());
                map.put(BOLD, excelField.bold());
                map.put(ITALIC, excelField.italic());
                map.put(COLOR, excelField.color());
                map.put(FONT_NAME, excelField.fontName());
                map.put(FONT_SIZE, excelField.fontSize());
                map.put(FIELD,declaredField);
                headers.put(excelIndex.index(),map);
            }
        }
        if (CollectionUtils.isEmpty(headers)){
            throw new ExportException("can not find any excel field!");
        }
        CLASS_FIELD_CACHE.put(className, headers);
        return headers;
    }
}
