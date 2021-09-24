/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.export.excel;

import org.apache.poi.ss.usermodel.Font;

import java.lang.annotation.*;

/**
 * @author Jackie
 * @version $id: ExcelField.java v 0.1 2021-09-17 09:05 Jackie Exp $$
 */
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelField {

    String fieldName();

    int width() default 20;

    /**
     * export field height
     */
    short height() default 20;

    /**
     * excel title font name
     */
    String fontName() default "宋体";

    /**
     * excel title font size
     */
    short fontSize() default 20;

    /**
     * excel title color
     */
    short  color() default Font.COLOR_NORMAL;

    /**
     * excel title bold
     */
    boolean bold() default false;

    /**
     * excel title italic
     */
    boolean italic() default false;
}
