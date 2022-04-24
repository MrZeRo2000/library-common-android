package com.romanpulov.library.common.logger;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.romanpulov.library.common.media.MediaStoreUtils;

import java.io.File;

public class BaseLoggerHelper {
    private static String LOCAL_APP_FOLDER_NAME;
    private static String LOG_FOLDER_NAME;

    public static void configure(String appFolderName, String logFolderName) {
        LOCAL_APP_FOLDER_NAME = appFolderName;
        LOG_FOLDER_NAME = logFolderName;
    }

    protected static void validateConfiguration() {
        if (LOCAL_APP_FOLDER_NAME == null || LOG_FOLDER_NAME == null) {
            throw new RuntimeException("LoggerHelper not configured. Please, configure first");
        }
    }

    private static AbstractLogger mLogger;

    private final Context mContext;

    private boolean mEnableLogging;

    public boolean isEnableLogging() {
        return mEnableLogging;
    }

    public void setEnableLogging(boolean enableLogging) {
        this.mEnableLogging = enableLogging;
    }

    private String mLogFolderName;

    public void log(String tag, String message) {
        if (mEnableLogging) {
            internalLog(tag, message);
        }
    }

    public void unconditionalLog(String tag, String message) {
        internalLog(tag, message);
    }

    private void internalLog(String tag, String message) {
        String logFileName =  DateFormatterHelper.formatLogFileDate(System.currentTimeMillis()) + ".log";

        if (mLogger == null) {
            mLogger = prepareLogger(logFileName);
        } else if (!mLogger.getFileName().equals(logFileName)) {
            mLogger.close();
            mLogger = prepareLogger(logFileName);
        }

        if (mLogger != null) {
            mLogger.log(tag, message, 0);
        }

        Log.d(tag, message);
    }

    protected BaseLoggerHelper(@NonNull Context context) {
        mContext = context.getApplicationContext();
    }

    private @Nullable File prepareLogFolder() {
        File logFolder = mContext.getExternalFilesDir(LOG_FOLDER_NAME);
        if (logFolder == null) {
            logFolder = new File(mContext.getFilesDir(), LOG_FOLDER_NAME);
        }

        if (!logFolder.exists() && !logFolder.mkdir()) {
            Log.d(BaseLoggerHelper.class.getSimpleName(), "Log folder does not exist and can't be created");
            return null;
        }

        return logFolder;
    }

    private @Nullable AbstractLogger prepareLogger(@NonNull String logFileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new MediaStoreLogger(
                    mContext,
                    MediaStoreUtils.MEDIA_STORE_ROOT_PATH + "/" + LOCAL_APP_FOLDER_NAME + LOG_FOLDER_NAME + "/",
                    logFileName
            );
        } else {
            File logFolder = prepareLogFolder();
            if (logFolder != null) {
                return new FileLogger(logFolder.getPath() + "/", logFileName);
            } else {
                return null;
            }
        }
    }
}
