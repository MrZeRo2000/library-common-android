package com.romanpulov.library.common.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Date formatter
 * Created by rpulov on 24.12.2016.
 */

public class DateFormatter {
    private final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private DateFormat mDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());

    /**
     * Formats given date
     * @param date date to format
     * @return formatted date
     */
    public String format(long date) {
        return mDateFormat.format(date);
    }

    /**
     * Formats current date
     * @return formatted date
     */
    public String formatCurrentDate() {
        return mDateFormat.format(System.currentTimeMillis());
    }
}
