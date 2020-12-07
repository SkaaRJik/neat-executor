package org.neat4j.neat.utils;

public class NumberUtils {

    public static Double asDouble(Object o) {
        Double val = null;
        if (o instanceof Number) {
            val = ((Number) o).doubleValue();
        }
        return val;
    }

    public static Integer asInt(Object o) {
        Integer val = null;
        if (o instanceof Number) {
            val = ((Number) o).intValue();
        }
        if(o instanceof String) {
            try {
                val = Integer.parseInt(o.toString());
            } catch (NumberFormatException ex) {

            }
        }
        return val;
    }

}
