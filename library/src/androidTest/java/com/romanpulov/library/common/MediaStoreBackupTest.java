package com.romanpulov.library.common;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.romanpulov.jutilscore.io.FileUtils;
import com.romanpulov.jutilscore.io.ZipFileUtils;
import com.romanpulov.library.common.media.MediaStoreUtils;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MediaStoreBackupTest {

    @Test
    public void test100_backupFile() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

            File dataFile = new File(appContext.getFilesDir(), "data-file.bin");
            final String backupFolderName = MediaStoreUtils.MEDIA_STORE_ROOT_PATH + "/library-common-test-backup/";
            final String backupFileName = "data-file.zip";

            if (dataFile.exists()) {
                dataFile.delete();
            }

            // generate and write random bytes file

            byte[] b1 = new byte[2056];
            new Random().nextBytes(b1);

            Assert.assertFalse(dataFile.exists());

            try (
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(b1);
                    FileOutputStream outputStream = new FileOutputStream(dataFile);
            ) {
                FileUtils.copyStream(inputStream, outputStream);
            }

            Assert.assertTrue(dataFile.exists());

            // delete backup folder
            MediaStoreUtils.deleteMediaFolder(appContext, backupFolderName);

            Assert.assertEquals(MediaStoreUtils.getDisplayNameList(appContext, backupFolderName).size(), 0);

            Uri uri = null;

            try (Cursor cursor = MediaStoreUtils.queryMediaFile(appContext, backupFolderName, backupFileName)) {
                if (cursor == null || cursor.getCount() == 0) {
                    uri = MediaStoreUtils.insertMediaFile(appContext, backupFolderName, backupFileName);

                } else if (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                    uri = ContentUris.withAppendedId(MediaStoreUtils.getMediaFilesContentUri(), id);
                }
            }

            if (uri != null) {
                try (
                        InputStream inputStream = new FileInputStream(dataFile);
                        OutputStream outputStream = appContext.getContentResolver().openOutputStream(uri, "wt");
                ) {
                    ZipFileUtils.zipStream("data-file.bin", inputStream, outputStream);
                }

                Assert.assertEquals(MediaStoreUtils.getDisplayNameList(appContext, backupFolderName).size(), 1);
                Assert.assertEquals(MediaStoreUtils.getDisplayNameList(appContext, backupFolderName).get(0), backupFileName);

                try (
                        InputStream inputStream = new FileInputStream(dataFile);
                        OutputStream outputStream = appContext.getContentResolver().openOutputStream(uri, "wt");
                ) {
                    ZipFileUtils.zipStream("data-file.bin", inputStream, outputStream);
                }

                Assert.assertEquals(MediaStoreUtils.getDisplayNameList(appContext, backupFolderName).size(), 1);
                Assert.assertEquals(MediaStoreUtils.getDisplayNameList(appContext, backupFolderName).get(0), backupFileName);
            } else {
                throw new RuntimeException("uri was not created successfully");
            }

            Uri backupFileUri = MediaStoreUtils.queryFirstMediaFileUri(appContext, backupFolderName, backupFileName);
            Assert.assertNotNull(backupFileUri);

            try (
                    InputStream inputStream = appContext.getContentResolver().openInputStream(backupFileUri);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ) {
                ZipFileUtils.unZipStream(inputStream, outputStream);
                Assert.assertArrayEquals(b1, outputStream.toByteArray());
            }

            //appContext.getCacheDir()
            File tempFile = File.createTempFile("aaa", "bbb", appContext.getCacheDir());

            // delete backup folder
            MediaStoreUtils.deleteMediaFolder(appContext, backupFolderName);
        }
    }
}
