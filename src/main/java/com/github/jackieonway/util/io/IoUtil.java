package com.github.jackieonway.util.io;

import java.io.*;

public enum  IoUtil {

    /**
     *  IoUtil instance
     */
    INSTNCE;

    public static ByteArrayOutputStream parse(final InputStream in) throws IOException {
        final ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            swapStream.write(ch);
        }
        return swapStream;
    }

    public static ByteArrayInputStream parse(final OutputStream out) {
        ByteArrayOutputStream baos;
        baos = (ByteArrayOutputStream) out;
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public static String parseString(final InputStream in) throws IOException {
        final ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            swapStream.write(ch);
        }
        return swapStream.toString();
    }

    public static String parseString(final OutputStream out) {
        ByteArrayOutputStream baos;
        baos = (ByteArrayOutputStream) out;
        final ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream.toString();
    }

    public static ByteArrayInputStream parseInputStream(final String in) {
        return new ByteArrayInputStream(in.getBytes());
    }

    public static ByteArrayOutputStream parseOutputStream(final String in) throws IOException {
        return parse(parseInputStream(in));
    }
}