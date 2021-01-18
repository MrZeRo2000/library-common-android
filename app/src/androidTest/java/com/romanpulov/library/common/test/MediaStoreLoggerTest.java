package com.romanpulov.library.common.test;

import android.content.Context;
import android.os.Environment;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.romanpulov.library.common.logger.MediaStoreLogger;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MediaStoreLoggerTest {

    @Test
    public void mainTest() throws Exception {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        MediaStoreLogger mediaStoreLogger = new MediaStoreLogger(
                appContext,
                "/library-common-test-logger/log/",
                "test_log.txt"
        );

        mediaStoreLogger.log("MyTag", "First line");
        Thread.sleep(5000L);
        mediaStoreLogger.log("MyTag", "Second line");
        Thread.sleep(2000L);
        mediaStoreLogger.log("MyTag", "Third line");

        mediaStoreLogger = new MediaStoreLogger(
                appContext,
                Environment.DIRECTORY_DOCUMENTS +"/library-common-test-logger/log/",
                "test_log_2.txt"
        );

        mediaStoreLogger.log("MyTag2", "File 2 First line");

    }
}
