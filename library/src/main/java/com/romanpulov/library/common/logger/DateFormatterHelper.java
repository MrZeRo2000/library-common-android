package com.romanpulov.library.common.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatterHelper {
    private static final String LOG_FILE_DATE_FORMAT = "yyyy-MM-dd";
    private static final String LOG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String TIME_FORMAT = "HH:mm:ss";

    private static DateFormat mLogDateFormat;
    private static DateFormat mTimeFormat;
    private static DateFormat mLogFileDateFormat;

    private static DateFormat getLogDateFormat() {
        if (mLogDateFormat == null)
            mLogDateFormat = new SimpleDateFormat(LOG_DATE_FORMAT, Locale.getDefault());

        return mLogDateFormat;
    }

    private static DateFormat getTimeFormat() {
        if (mTimeFormat == null)
            mTimeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());

        return mTimeFormat;
    }

    private static DateFormat getLogFileDateFormat() {
        if (mLogFileDateFormat == null)
            mLogFileDateFormat = new SimpleDateFormat(LOG_FILE_DATE_FORMAT, Locale.getDefault());

        return mLogFileDateFormat;
    }

    /**
     * Default format
     * @param milliseconds date in milliseconds
     * @return formatted date string
     */
    public static String format(long milliseconds) {
        return java.text.DateFormat.getDateTimeInstance().format(new Date(milliseconds));
    }

    /**
     * Format for log with milliseconds
     * @param milliseconds date in milliseconds
     * @return formatted date string
     */
    public static String formatLog(long milliseconds) {
        return getLogDateFormat().format(new Date(milliseconds));
    }

    /**
     * Format time only
     * @param milliseconds date in milliseconds
     * @return formatted time string
     */
    public static String formatTime(long milliseconds) {
        return java.text.DateFormat.getTimeInstance().format(new Date(milliseconds));
    }

    /**
     * Format time only with predefined pattern
     * @param milliseconds date in milliseconds
     * @return formatted time string
     */
    public static String formatTimeLog(long milliseconds) {
        return getTimeFormat().format(new Date(milliseconds));
    }

    /**
     * Format date for log file
     * @param milliseconds date in milliseconds
     * @return formatted date string
     */
    public static String formatLogFileDate(long milliseconds) {
        return getLogFileDateFormat().format(new Date(milliseconds));
    }
}
