/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.export.excel;

import org.apache.poi.ss.usermodel.Font;

import java.lang.annotation.*;

/**
 * @author Jackie
 * @version $id: ExcelFile.java v 1.0.2 2021-09-17 08:56 Jackie Exp $$
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelFile {

    /**
     * export excel sheet name
     * @return sheet name
     */
    String sheetName();

    /**
     * export excel title height
     * @return height
     */
    short height() default 20;

    /**
     * excel title font name
     * @return font  name
     */
    String fontName() default "宋体";

    /**
     * excel title font size
     * @return font size
     */
    short fontSize() default 20;

    /**
     * excel title color
     * @return color
     */
    short  color() default Font.COLOR_NORMAL;

    /**
     * excel title bold
     * @return bold
     */
    boolean bold() default true;

    /**
     * excel title italic
     * @return italic
     */
    boolean italic() default false;

    /**
     * excel type ,default type: xlsx
     * @return excel type
     */
    ExcelType type() default ExcelType.XLSX;
}
