/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.export.excel;

import java.lang.annotation.*;

/**
 * @author Jackie
 * @version $id: ExcelIndex.java v 0.1 2021-09-17 08:59 Jackie Exp $$
 */
@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelIndex {
    /**
     * export excel index
     */
    int index();
}
