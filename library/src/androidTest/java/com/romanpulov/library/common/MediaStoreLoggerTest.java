package com.romanpulov.library.common;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.romanpulov.library.common.logger.MediaStoreLogger;
import com.romanpulov.library.common.media.MediaStoreUtils;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;

@RunWith(AndroidJUnit4.class)
@SmallTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MediaStoreLoggerTest {

    @Test
    public void test100() throws Exception {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        MediaStoreLogger mediaStoreLogger = new MediaStoreLogger(
                appContext,
                Environment.DIRECTORY_DOWNLOADS + "/library-common-test-logger/log/",
                "test_log.txt"
        );

        mediaStoreLogger.log("MyTag", "First line");
        Thread.sleep(5000L);
        mediaStoreLogger.log("MyTag", "Second line");
        Thread.sleep(2000L);
        mediaStoreLogger.log("MyTag", "Third line");

        mediaStoreLogger = new MediaStoreLogger(
                appContext,
                Environment.DIRECTORY_DOWNLOADS +"/library-common-test-logger/log/",
                "test_log_2.txt"
        );

        mediaStoreLogger.log("MyTag2", "File 2 First line");
        mediaStoreLogger.log("MyTag2", "Another one");
        mediaStoreLogger.log("MyTag2", "One more");

        mediaStoreLogger = new MediaStoreLogger(
                appContext,
                Environment.DIRECTORY_DOWNLOADS +"/library-common-test-logger/log/",
                "test_log_3.log"
        );

        mediaStoreLogger.log("MyTag3", "File 2 First line");
        mediaStoreLogger.log("MyTag3", "Another one");
        mediaStoreLogger.log("MyTag3", "One more");

    }

    @Test
    public void test200() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Uri contentUri = MediaStore.Files.getContentUri(MediaStoreUtils.MEDIA_STORE_VOLUME_EXTERNAL_NAME);
            String selection = MediaStoreUtils.MEDIA_STORE_RELATIVE_PATH_NAME + "=?";
            String[] selectionArgs = new String[]{Environment.DIRECTORY_DOWNLOADS + "/library-common-test-logger/log/"};

            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

            List<String> displayNameList = new ArrayList<>();

            try (Cursor cursor = appContext.getContentResolver().query(contentUri, null, selection, selectionArgs, null)) {
                Assert.assertNotNull(cursor);
                Assert.assertThat(cursor.getCount(), greaterThan(0));

                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                    displayNameList.add(displayName);
                    Log.d("MediaStoreLoggerTest test200", "DisplayName: " + displayName);
                }
            }

            Assert.assertTrue(displayNameList.contains("test_log.txt"));
            Assert.assertTrue(displayNameList.contains("test_log_2.txt"));
            Assert.assertTrue(displayNameList.contains("test_log_3.log"));
        }
    }

    @Test
    public void test300() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Uri contentUri = MediaStore.Files.getContentUri(MediaStoreUtils.MEDIA_STORE_VOLUME_EXTERNAL_NAME);
            String selection = MediaStoreUtils.MEDIA_STORE_RELATIVE_PATH_NAME + "=?";
            String[] selectionArgs = new String[]{Environment.DIRECTORY_DOWNLOADS + "/library-common-test-logger/log/"};

            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            appContext.getContentResolver().delete(contentUri, selection, selectionArgs);
        }
    }
}
