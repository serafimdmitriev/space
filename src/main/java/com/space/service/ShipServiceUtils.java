package com.space.service;

import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

public class ShipServiceUtils {
    public static boolean isStringValid(@NonNull String string, int maxLength) {
        return !string.isEmpty() && string.length() <= maxLength;
    }

    public static Date getDateFromYear(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    public static int getYearFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static double round(double value, int scale) {
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        bigDecimal = bigDecimal.setScale(scale, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    public static boolean isBetween(Date date, Date after, Date before) {
        return (after == null ||
                date.after(after)) && (before == null || date.before(before));
    }

    public static boolean isBetween(Double value, Double min, Double max) {
        return (min == null ||  value >= min) && (max == null ||  value <= max);
    }

    public static boolean isBetween(Integer value, Integer min, Integer max) {
        return (min == null ||  value >= min) && (max == null ||  value <= max);
    }

}
