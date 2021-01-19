package com.romanpulov.library.common.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * File logger class
 * Created by rpulov on 24.12.2016.
 */

public abstract class AbstractLogger {
    private final static String LEVEL_FILL_STRING = " ";
    private final static String LOG_DELIMITER = " ";

    private final DateFormatter mDateFormatter = new DateFormatter();

    protected final String mFolderName;

    public String getFolderName() {
        return mFolderName;
    }

    protected final String mFileName;

    public String getFileName() {
        return mFileName;
    }

    public AbstractLogger(String mFolderName, String mFileName) {
        this.mFolderName = mFolderName;
        this.mFileName = mFileName;
    }

    public abstract void open() throws IOException;

    public abstract void close();

    protected abstract void internalLog(String message);

    public static String getLevelString(int level, String fillString) {
        return new String(new char[level]).replace("\0", fillString);
    }

    public String prepareLogMessage(String tag, String message, int level) {
        String result = "";

        // level
        if (level > 0)
            result += getLevelString(level, LEVEL_FILL_STRING);

        // date
        result += (result.isEmpty() ? "" : LOG_DELIMITER) + mDateFormatter.formatCurrentDate();

        // tag
        if ((tag != null) && !tag.isEmpty())
            result += (result.isEmpty() ? "" : LOG_DELIMITER) + tag;

        // message
        result += (result.isEmpty() ? "" : LOG_DELIMITER) + message;

        if (!result.isEmpty())
            result = result + "\n";

        return result;
    }

    /**
     * Log with level
     * @param tag Tag
     * @param message Message
     * @param level Level
     */
    public synchronized void log(String tag, String message, int level) {
        internalLog(prepareLogMessage(tag, message, level));
    }

    /**
     * Log without level
     * @param tag Tag
     * @param message Message
     */
    public synchronized void log(String tag, String message) {
        log(tag, message, 0);
    }
}
