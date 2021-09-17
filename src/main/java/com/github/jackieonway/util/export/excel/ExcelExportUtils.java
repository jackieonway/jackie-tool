/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.export.excel;

import com.github.jackieonway.util.collection.CollectionUtils;
import com.github.jackieonway.util.export.ExportException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
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
    private static final Pattern numericPattern = Pattern.compile("[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");

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
        ExcelFile excelFile = clazz.getAnnotation(ExcelFile.class);
        String sheetName = excelFile.sheetName();
        if (excelFile.type().equals(ExcelType.XLS)) {
            if (collection.size() > MAX_EXPORT_NUM_EXCEL_2003){
                throw new ExportException("Excel 2003 type can export max less than 65536");
            }
            doExportXls(outputStream, collection,sheetName,clazz);
            return;
        }
        doExportXlsx(outputStream, collection,sheetName,clazz);
    }

    private static <E> void doExportXls(OutputStream outputStream, Collection<E> collection, String sheetName, Class<E> clazz) {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet workbookSheet = hssfWorkbook.createSheet(sheetName);
        Field[] declaredFields = clazz.getDeclaredFields();
        HSSFRow headerRow = workbookSheet.createRow(0);
        Map<Integer, Map<String, Object>> headers = getHeaders(declaredFields);
        headers.forEach((key,value) ->{
            workbookSheet.setColumnWidth(key,((Integer) value.get("width")) * 256);
            HSSFCell cell = headerRow.createCell(key);
            cell.setCellValue(value.get("fieldName").toString());
            cell.setCellStyle(getTitleCellStyle(hssfWorkbook));

        });
        int index = 1;
        for (E data : collection) {
            HSSFRow dataRow = workbookSheet.createRow(index);
            headers.forEach((key, value) -> {
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

    private static <E> void doExportXlsx(OutputStream outputStream, Collection<E> collection, String sheetName, Class<E> clazz) {
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook();
        SXSSFSheet workbookSheet = sxssfWorkbook.createSheet(sheetName);
        Field[] declaredFields = clazz.getDeclaredFields();
        SXSSFRow headerRow = workbookSheet.createRow(0);
        Map<Integer, Map<String, Object>> headers = getHeaders(declaredFields);
        headers.forEach((key,value) ->{
            workbookSheet.setColumnWidth(key,((Integer) value.get("width")) * 256);
            SXSSFCell cell = headerRow.createCell(key);
            cell.setCellValue(value.get("fieldName").toString());
            cell.setCellStyle(getTitleCellStyle(sxssfWorkbook));
        });
        int index = 1;
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

    private static <E> void createRowData(Workbook workbook, E data, Map<String, Object> value, Cell cell) {
        Field field = (Field) value.get("field");
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
        cell.setCellStyle(getTitleCellStyle(workbook));
    }


    private static Map<Integer, Map<String, Object>> getHeaders(Field[] declaredFields) {
        Map<Integer,Map<String,Object>> headers = new TreeMap<>();
        for(Field declaredField : declaredFields){
            if(declaredField.isAnnotationPresent(ExcelField.class)){
                if (!declaredField.isAnnotationPresent(ExcelIndex.class)) {
                    throw new ExportException("Export excel can not find annotation ExcelIndex");
                }
                Map<String, Object> map = new HashMap<>();
                ExcelIndex excelIndex = declaredField.getAnnotation(ExcelIndex.class);
                ExcelField excelField = declaredField.getAnnotation(ExcelField.class);
                map.put("width", excelField.width());
                map.put("fieldName", excelField.fieldName());
                map.put("field",declaredField);
                headers.put(excelIndex.index(),map);
            }
        }
        return headers;
    }

    private static boolean isNumeric(String str) {
        if (str == null || !"".equals(str.trim())) {
            return false;
        }
        Matcher matcher = numericPattern.matcher(str);
        if (matcher.matches()) {
            return str.contains(".") || !str.startsWith("0");
        }
        return false;
    }

    private static CellStyle getTitleCellStyle(Workbook workbook){
        CellStyle hssfcellstyle =  getBasicCellStyle(workbook);
        hssfcellstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return hssfcellstyle;
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

}
