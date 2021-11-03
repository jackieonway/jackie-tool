/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.export;

/**
 * @author Jackie
 * @version $id: ImportException.java v 0.1 2021-09-17 09:29 Jackie Exp $$
 */
public class ImportException extends RuntimeException{
    public ImportException() {
        super();
    }

    public ImportException(String message) {
        super(message);
    }

    public ImportException(String message,Throwable throwable) {
        super(message,throwable);
    }
}
