package org.neat4j.neat.utils;

public class NumberUtil {
    public static Double castToDouble(Object o) {
        Double val = null;
        if (o instanceof Number) {
            val = ((Number) o).doubleValue();
        } else {
            throw new NumberFormatException(String.format("Object [ %s ] cant be represented as Number",o.toString()));
        }
        return val;
    }
}
