/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.export.excel;

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
}
