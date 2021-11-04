/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.export.excel;

import com.github.jackieonway.util.export.ExportException;
import com.github.jackieonway.util.export.ImportException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Jackie
 * @version $id: ExcelImportUtils.java v 0.1 2021-11-03 10:44 Jackie Exp $$
 */
public enum ExcelImportUtils {

    /**
     * ExcelImportUtils instance
     */
    INSTANCE;

    private static final String TYPE = "type";
    private static final String FIELD_NAME = "fieldName";
    private static final String FIELD = "field";
    public static final String NUMERIC = "NUMERIC";
    public static final String STRING = "STRING";

    public static <E> List<E> importExcel(InputStream inputStream, Class<E> clazz){
        if (!clazz.isAnnotationPresent(ExcelFile.class)){
            throw new ExportException(String.format("class [%s] can  not find annotation ExcelFile",clazz));
        }
        if (Objects.isNull(inputStream)){
            throw new ExportException("import inputStream is null");
        }
        Map<String, Object> excelFileMap = ExcelTools.putClassFileAndGet(clazz);
        final ExcelType excelType = (ExcelType)excelFileMap.get(TYPE);
        if (excelType.equals(ExcelType.XLS)) {
           return doImportXls(inputStream, clazz,excelFileMap);
        }
        return doImportXlsx(inputStream, clazz,excelFileMap);
    }

    private static <E> List<E> doImportXlsx(InputStream inputStream, Class<E> clazz, Map<String, Object> excelFileMap) {

        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
            String sheetName = excelFileMap.get("sheetName").toString();
            XSSFSheet xssfSheet = xssfWorkbook.getSheet(sheetName);
            return getAllData(clazz, sheetName, xssfSheet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <E> List<E> getAllData(Class<E> clazz, String sheetName, Sheet sheet) {
        Map<Integer, Map<String, Object>> headers = ExcelTools.putAndGetFields(clazz);
        int lastRowNum = sheet.getLastRowNum();
        int dataRowNum = 0;
        for (int i = 0; i < lastRowNum; i++) {
            boolean isTitleFlag = checkTitleIfNecessary(sheet.getRow(i), sheetName);
            if (isTitleFlag) {
                if (lastRowNum < i + 1){
                    throw new IllegalStateException("excel can not find any title");
                }
                boolean isHeaderFlag = checkHeaderIfNecessary(sheet.getRow(i+1), headers);
                if (isHeaderFlag){
                    dataRowNum = i+2;
                    break;
                }
            }else {
                boolean isHeaderFlag = checkHeaderIfNecessary(sheet.getRow(i), headers);
                if (isHeaderFlag){
                    dataRowNum = i+1;
                    break;
                }
            }
        }
        return getData(sheet, dataRowNum, headers, clazz);
    }

    private static <E> List<E> doImportXls(InputStream inputStream, Class<E> clazz, Map<String, Object> excelFileMap) {
        try {
            POIFSFileSystem fileSystem  = new POIFSFileSystem(inputStream);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fileSystem);
            String sheetName = excelFileMap.get("sheetName").toString();
            HSSFSheet hssfSheet = hssfWorkbook.getSheet(sheetName);
            return getAllData(clazz, sheetName, hssfSheet);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <E> List<E> getData(Sheet sheet, int dataRowNum,
                                       Map<Integer, Map<String, Object>> headers, Class<E> clazz) {
        int lastRowNum = sheet.getLastRowNum();
        List<E> result = new ArrayList<>();
        for (int i = dataRowNum; i <= lastRowNum; i++) {
            E data = getRowData(sheet.getRow(i), headers, clazz);
            if (Objects.nonNull(data)) {
                result.add(data);
            }
        }
        return result;
    }

    private static <E> E getRowData(Row row, Map<Integer, Map<String, Object>> headers, Class<E> clazz) {
        try {
            if (Objects.isNull(row)){
                return null;
            }
            E instance = clazz.getDeclaredConstructor().newInstance();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()){
                Cell cell = cellIterator.next();
                int columnIndex = cell.getColumnIndex();
                Map<String, Object> header = headers.get(columnIndex);
                Field field = (Field) header.get(FIELD);
                field.setAccessible(true);
                Class<?> type = field.getType();
                if (int.class.equals(type) || Integer.class.equals(type) ){
                    if (NUMERIC.equalsIgnoreCase(cell.getCellType().name())){
                        double numericCellValue = cell.getNumericCellValue();
                        field.set(instance, Double.valueOf(numericCellValue).intValue());
                    }else {
                        String stringCellValue = cell.getStringCellValue();
                        field.set(instance, Integer.parseInt(stringCellValue));
                    }

                }else if (double.class.equals(type) || Double.class.equals(type)){
                    if (NUMERIC.equalsIgnoreCase(cell.getCellType().name())){
                        field.set(instance, cell.getNumericCellValue());
                    }else {
                        String stringCellValue = cell.getStringCellValue();
                        field.set(instance, Double.parseDouble(stringCellValue));
                    }
                }else if (float.class.equals(type) || Float.class.equals(type)){
                    if (NUMERIC.equalsIgnoreCase(cell.getCellType().name())){
                        double numericCellValue = cell.getNumericCellValue();
                        field.set(instance, Double.valueOf(numericCellValue).floatValue());
                    }else {
                        String stringCellValue = cell.getStringCellValue();
                        field.set(instance, Float.parseFloat(stringCellValue));
                    }
                }else if (long.class.equals(type) || Long.class.equals(type)){
                    if (NUMERIC.equalsIgnoreCase(cell.getCellType().name())){
                        double numericCellValue = cell.getNumericCellValue();
                        field.set(instance, Double.valueOf(numericCellValue).longValue());
                    }else {
                        String stringCellValue = cell.getStringCellValue();
                        field.set(instance, Long.parseLong(stringCellValue));
                    }
                }else if (boolean.class.equals(type) || Boolean.class.equals(type)){
                    field.set(instance, cell.getBooleanCellValue());
                }else if (BigDecimal.class.getSimpleName().equals(type.getSimpleName())){
                    String stringCellValue = cell.getStringCellValue();
                    field.set(instance, new BigDecimal(stringCellValue));
                }else if (LocalDate.class.equals(type)) {
                    field.set(instance, cell.getLocalDateTimeCellValue().toLocalDate());
                } else if (LocalDateTime.class.equals(type)) {
                    field.set(instance, cell.getLocalDateTimeCellValue());
                }else if (Date.class.equals(type)) {
                    field.set(instance, cell.getDateCellValue());
                }else {
                    field.set(instance, cell.getStringCellValue());
                }
            }
            return instance;
        } catch (Exception e) {
           throw new ImportException(e.getMessage(),e);
        }
    }

    private static boolean checkHeaderIfNecessary(Row row, Map<Integer, Map<String, Object>> headers) {
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (STRING.equals(cell.getCellType().name())
                    && cell.getStringCellValue().equals(headers.get(cell.getColumnIndex()).get(FIELD_NAME))) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkTitleIfNecessary(Row row, String sheetName) {
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (STRING.equals(cell.getCellType().name())
                    && cell.getStringCellValue().equals(sheetName)) {
                return true;
            }
        }
        return false;
    }

}
