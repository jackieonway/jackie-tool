/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.export.excel;

import com.github.jackieonway.util.StringUtils;
import com.github.jackieonway.util.collection.CollectionUtils;
import com.github.jackieonway.util.export.ExportException;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
     * @param outputStream export outputStream
     * @param collection export collection
     * @param clazz export class
     * @author  Jackie
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
        Map<String, Object> excelFileMap = ExcelTools.putClassFileAndGet(clazz);
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
        Map<Integer, Map<String, Object>> headers = ExcelTools.putAndGetFields(clazz);
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

    private static <E> void doExportXlsx(OutputStream outputStream, Collection<E> collection,
                                         Class<E> clazz, Map<String, Object> excelFileMap) {
        final String sheetName = excelFileMap.get("sheetName").toString();
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook();
        SXSSFSheet workbookSheet = sxssfWorkbook.createSheet(sheetName);
        Map<Integer, Map<String, Object>> headers = ExcelTools.putAndGetFields(clazz);
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
        final CellStyle cellStyle = getHeaderCellStyle(workbook);
        cellStyle.setFont(createFont(workbook,value));
        cell.setCellStyle(cellStyle);
        Class<?> type = field.getType();
        if (Objects.isNull(dataValue)) {
            cell.setBlank();
        } else if (long.class.equals(type) || Long.class.equals(type)) {
            formatData(workbook, value, cellStyle);
            cell.setCellValue(dataValue.toString());
        } else if (double.class.equals(type) || Double.class.equals(type)) {
            formatData(workbook, value, cellStyle);
            cell.setCellValue(Double.parseDouble(dataValue.toString()));
        }  else if (int.class.equals(type) || Integer.class.equals(type)) {
            cell.setCellValue(dataValue.toString());
        } else if (float.class.equals(type) || Float.class.equals(type)) {
            formatData(workbook, value, cellStyle);
            cell.setCellValue(Float.parseFloat(dataValue.toString()));
        } else if (boolean.class.equals(type) || Boolean.class.equals(type)) {
            formatData(workbook, value, cellStyle);
            cell.setCellValue(Boolean.parseBoolean(dataValue.toString()));
        } else if (LocalDate.class.equals(type)) {
            formatData(workbook, value, cellStyle);
            cell.setCellValue((LocalDate)dataValue);
        } else if (LocalDateTime.class.equals(type)) {
            formatData(workbook, value, cellStyle);
            cell.setCellValue((LocalDateTime)dataValue);
        }else if (Date.class.equals(type)) {
            formatData(workbook, value, cellStyle);
            cell.setCellValue((Date)dataValue);
        }else {
            formatData(workbook, value, cellStyle);
            cell.setCellValue(dataValue.toString());
        }
    }

    private static void formatData(Workbook workbook, Map<String, Object> value, CellStyle cellStyle) {
        Object format = value.get("format");
        if (Objects.nonNull(format) && StringUtils.isNotBlank(format.toString())){
            cellStyle.setDataFormat(workbook.createDataFormat().getFormat(format.toString()));
        }
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
