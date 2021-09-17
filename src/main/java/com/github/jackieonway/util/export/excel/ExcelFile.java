/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.export.excel;

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
     * export excel file name
     */
    String sheetName();

    /**
     * excel type ,default type: xlsx,
     */
    ExcelType type() default ExcelType.XLSX;
}
