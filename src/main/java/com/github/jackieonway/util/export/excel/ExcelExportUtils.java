/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.export.excel;

import com.github.jackieonway.util.collection.CollectionUtils;
import com.github.jackieonway.util.export.ExportException;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jackie
 * @version $id: ExcelExportUtils.java v 0.1 2021-09-17 08:55 Jackie Exp $$
 */
public enum ExcelExportUtils {
    /**
     * ExcelExportUtils instance
     */
    INSTANCE;

    /**
     * max export number for excel 2003
     */
    private static final int MAX_EXPORT_NUM_EXCEL_2003 = 65536;
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");

    private static final Map<String, Map<Integer, Map<String, Object>>> CLASS_FIELD_CACHE =
            new ConcurrentHashMap<>(512);

    private static final Map<String, Map<String, Object>> CLASS_FILE_CACHE = new ConcurrentHashMap<>(512);

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

    /**
     * export excel
     * @param collection export collection
     * @param clazz export class
     * @author  Jackie
     * @date  2021/9/17 9:44
     * @since 1.0.2
     * @see ExcelExportUtils
     */
    public static <E> void export(OutputStream outputStream, Collection<E> collection, Class<E> clazz){
        if (!clazz.isAnnotationPresent(ExcelFile.class)){
            throw new ExportException(String.format("class [%s] can  not find annotation ExcelFile",clazz));
        }
        if (CollectionUtils.isEmpty(collection)){
            throw new ExportException("export data is null");
        }

        if (Objects.isNull(outputStream)){
            throw new ExportException("export outputStream is null");
        }
        Map<String, Object> excelFileMap = CLASS_FILE_CACHE.get(clazz.getName());
        if (CollectionUtils.isEmpty(excelFileMap)) {
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
        final ExcelType excelType = (ExcelType)excelFileMap.get(TYPE);
        if (excelType.equals(ExcelType.XLS)) {
            if (collection.size() > MAX_EXPORT_NUM_EXCEL_2003){
                throw new ExportException("Excel 2003 type can export max less than 65536");
            }
            doExportXls(outputStream, collection,clazz, excelFileMap);
            return;
        }
        doExportXlsx(outputStream, collection,clazz, excelFileMap);
    }

    private static <E> void doExportXls(OutputStream outputStream, Collection<E> collection,
                                        Class<E> clazz, Map<String, Object> excelFileMap) {
        final String sheetName = excelFileMap.get("sheetName").toString();
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet workbookSheet = hssfWorkbook.createSheet(sheetName);
        Map<Integer, Map<String, Object>> headers = getHeaderMap(clazz);
        createTitleRow(sheetName, hssfWorkbook,workbookSheet, headers.size() - 1, excelFileMap);
        HSSFRow headerRow = workbookSheet.createRow(1);
        headers.forEach((key,value) ->{
            workbookSheet.setColumnWidth(key,((Integer) value.get(WIDTH)) * 256);
            HSSFCell cell = headerRow.createCell(key);
            cell.setCellValue(value.get(FIELD_NAME).toString());
            final CellStyle cellStyle = getHeaderCellStyle(hssfWorkbook);
            final Font font = createFont(hssfWorkbook, value);
            font.setBold(true);
            cellStyle.setFont(font);
            cell.setCellStyle(cellStyle);

        });
        int index = 2;
        for (E data : collection) {
            HSSFRow dataRow = workbookSheet.createRow(index);
            headers.forEach((key, value) -> {
                dataRow.setHeightInPoints(Short.parseShort(value.get("height").toString()));
                HSSFCell cell = dataRow.createCell(key);
                createRowData(hssfWorkbook, data, value, cell);
            });
            index++;
        }
        try {
            hssfWorkbook.write(outputStream);
        } catch (IOException e) {
            throw new ExportException(e.getMessage());
        }
    }

    private static <T> Map<Integer, Map<String, Object>> getHeaderMap(Class<T> clazz) {
        Map<Integer, Map<String, Object>> headers = CLASS_FIELD_CACHE.get(clazz.getName());
        if (CollectionUtils.isEmpty(headers)) {
            Field[] declaredFields = clazz.getDeclaredFields();
            headers = getHeaders(declaredFields,clazz.getName());
        }
        return headers;
    }

    private static <E> void doExportXlsx(OutputStream outputStream, Collection<E> collection,
                                         Class<E> clazz, Map<String, Object> excelFileMap) {
        final String sheetName = excelFileMap.get("sheetName").toString();
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook();
        SXSSFSheet workbookSheet = sxssfWorkbook.createSheet(sheetName);
        Map<Integer, Map<String, Object>> headers = getHeaderMap(clazz);
        createTitleRow(sheetName, sxssfWorkbook, workbookSheet, headers.size() - 1, excelFileMap);
        SXSSFRow headerRow = workbookSheet.createRow(1);
        headers.forEach((key,value) ->{
            workbookSheet.setColumnWidth(key,((Integer) value.get(WIDTH)) * 256);
            SXSSFCell cell = headerRow.createCell(key);
            cell.setCellValue(value.get(FIELD_NAME).toString());
            final CellStyle cellStyle = getHeaderCellStyle(sxssfWorkbook);
            final Font font = createFont(sxssfWorkbook, value);
            font.setBold(true);
            font.setFontHeightInPoints((short) 15);
            font.setItalic(false);
            font.setColor(Font.COLOR_NORMAL);
            cellStyle.setFont(font);
            cell.setCellStyle(cellStyle);
        });
        int index = 2;
        for (E data : collection) {
            SXSSFRow dataRow = workbookSheet.createRow(index);
            headers.forEach((key, value) -> {
                SXSSFCell cell = dataRow.createCell(key);
                createRowData(sxssfWorkbook, data, value, cell);
            });
            index++;
        }
        try {
            sxssfWorkbook.write(outputStream);
        } catch (IOException e) {
            throw new ExportException(e.getMessage());
        }
    }

    private static void createTitleRow(String sheetName, Workbook workbook, Sheet sheet, Integer
            lastCol, Map<String, Object> excelFileMap) {
        final float height = Float.parseFloat(excelFileMap.get(HEIGHT).toString());
        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0);
        CellRangeAddress cellRangeAddress = new CellRangeAddress(0, 0, 0, lastCol);
        sheet.addMergedRegion(cellRangeAddress);
        Row titleRow1 = sheet.getRow(0);
        titleRow1.setHeightInPoints(height);
        Cell titleCell = titleRow1.getCell(0);
        CellStyle basicCellStyle = getBasicCellStyle(workbook);
        basicCellStyle.setFont(createFont(workbook,excelFileMap));
        titleCell.setCellStyle(basicCellStyle);
        RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress, sheet);
        titleCell.setCellValue(sheetName);
    }

    private static <E> void createRowData(Workbook workbook, E data, Map<String, Object> value, Cell cell) {
        Field field = (Field) value.get(FIELD);
        field.setAccessible(true);
        Object dataValue;
        try {
            dataValue = field.get(data);
        } catch (IllegalAccessException e) {
            dataValue = "";
        }
        if (Objects.isNull(dataValue)) {
            cell.setBlank();
        } else if (isNumeric(dataValue.toString())) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(dataValue.toString());
        } else {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(dataValue.toString());
        }
        final CellStyle cellStyle = getHeaderCellStyle(workbook);
        cellStyle.setFont(createFont(workbook,value));
        cell.setCellStyle(cellStyle);
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
            throw new ExportException("can not find any export field!");
        }
        CLASS_FIELD_CACHE.put(className, headers);
        return headers;
    }

    private static boolean isNumeric(String str) {
        if (str == null || !"".equals(str.trim())) {
            return false;
        }
        Matcher matcher = NUMERIC_PATTERN.matcher(str);
        if (matcher.matches()) {
            return str.contains(".") || !str.startsWith("0");
        }
        return false;
    }

    private static CellStyle getHeaderCellStyle(Workbook workbook){
        CellStyle cellStyle =  getBasicCellStyle(workbook);
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }

    private static CellStyle getBasicCellStyle(Workbook workbook){
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setWrapText(true);
        return cellStyle;
    }

    private static Font createFont(Workbook workbook, Map<String, Object> configMap){
        final boolean bold = Boolean.parseBoolean(configMap.get(BOLD).toString());
        final String fontName = configMap.get(FONT_NAME).toString();
        final short color = Short.parseShort(configMap.get(COLOR).toString());
        final short fontSize = Short.parseShort(configMap.get(FONT_SIZE).toString());
        final boolean italic = Boolean.parseBoolean(configMap.get(ITALIC).toString());
        Font font = workbook.createFont();
        font.setFontName(fontName);
        font.setBold(bold);
        font.setColor(color);
        font.setItalic(italic);
        font.setFontHeightInPoints(fontSize);
        return font;
    }

}
