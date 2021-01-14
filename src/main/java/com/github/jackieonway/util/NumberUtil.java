package com.github.jackieonway.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberUtil {
    private NumberUtil() {
    }

    public static String intelligenceFormat(Number number) {
        if (number == null) {
            return null;
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setGroupingUsed(false);
        return numberFormat.format(number);
    }

    public static String cannedFormat(Number number, String formatStr) {
        if (number == null) {
            return null;
        }
        DecimalFormat decimalFormat = new DecimalFormat(formatStr);
        return decimalFormat.format(number);
    }

    public static double rounding(double doubleMath, int precision) {
        BigDecimal b = BigDecimal.valueOf(doubleMath);
        BigDecimal bigDecimal = b.setScale(precision, 4);
        return bigDecimal.doubleValue();
    }

    public static String objectToNumberFormat(Object obj) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        return numberFormat.format(obj);
    }

    public static double log(double value, double base) {
        return value == 0.0d ? 0.0d : Math.log(value) / Math.log(base);
    }

    public static double log2(double value) {
        return value == 0.0D ? 0.0d : Math.log(value) / Math.log(2.0d);
    }

    public static double log10(double value) {
        return value == 0.0D ? 0.0d : Math.log10(value);
    }

    public static double loge(double value) {
        return value == 0.0D ? 0.0d : Math.log(value);
    }

    public static BigDecimal divideBigDecimal(BigDecimal decimal, double divisor) {
        return divisor == 0.0D ? new BigDecimal(0) : decimal.divide(BigDecimal.valueOf(divisor));
    }

    public static BigDecimal multiplyBigDecimal(BigDecimal decimal, double multiply) {
        return decimal.multiply(BigDecimal.valueOf(multiply));
    }

    public static BigDecimal addBigDecimal(BigDecimal decimal, int add) {
        return decimal.add(BigDecimal.valueOf(add));
    }

    public static BigDecimal subtractBigDecimal(BigDecimal decimal, int subtract) {
        return decimal.subtract(BigDecimal.valueOf(subtract));
    }
}
