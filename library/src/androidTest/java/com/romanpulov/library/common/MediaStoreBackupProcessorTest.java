package com.romanpulov.library.common;

import android.content.Context;
import android.os.Build;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.romanpulov.jutilscore.io.FileUtils;
import com.romanpulov.jutilscore.io.ZipFileUtils;
import com.romanpulov.jutilscore.storage.BackupProcessor;
import com.romanpulov.library.common.backup.MediaStoreBackupProcessor;
import com.romanpulov.library.common.media.MediaStoreUtils;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MediaStoreBackupProcessorTest {
    private final static Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    final String dataFileName = "data-file-to-backup.bin";
    final String backupFolderName = MediaStoreUtils.MEDIA_STORE_ROOT_PATH + "/library-common-test-backup-processor/";
    final String backupFileName = "data-file-to-backup";

    @Test
    @Ignore("Temporary while test200 is being developed")
    public void test100_createSingleBackup() throws Exception {
        File dataFile = new File(appContext.getFilesDir(), dataFileName);

        if (dataFile.exists() && !dataFile.delete()) {
            throw new RuntimeException("Error deleting existing file:" + dataFile.getAbsolutePath());
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

        // data file exists now

        MediaStoreBackupProcessor bp = new MediaStoreBackupProcessor(appContext, dataFile.getAbsolutePath(), backupFolderName, backupFileName);

        MediaStoreUtils.deleteMediaFolder(appContext, backupFolderName);

        //create and check if backup is created
        Assert.assertEquals(MediaStoreUtils.getDisplayNameList(appContext, backupFolderName).size(), 0);
        Assert.assertEquals(backupFileName, bp.createSingleBackup());
        Assert.assertEquals(MediaStoreUtils.getDisplayNameList(appContext, backupFolderName).size(), 1);

        //check if backup is created only once
        Assert.assertEquals(backupFileName, bp.createSingleBackup());
        Assert.assertEquals(MediaStoreUtils.getDisplayNameList(appContext, backupFolderName).size(), 1);
        Assert.assertEquals(MediaStoreUtils.getDisplayNameUriList(appContext, backupFolderName).size(), 1);

        Assert.assertNotNull(MediaStoreUtils.getDisplayNameUriList(appContext, backupFolderName).get(ZipFileUtils.getZipFileName(backupFileName)));

        // restore backup
        Assert.assertNotNull(bp.restoreBackup());
        Assert.assertTrue(dataFile.exists());

        if (!dataFile.delete()) {
            throw new RuntimeException("Error deleting file " + dataFile.getAbsolutePath());
        }

        Assert.assertNotNull(bp.restoreBackup());
        Assert.assertTrue(dataFile.exists());

        try (InputStream inputStream = new FileInputStream(dataFile);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            FileUtils.copyStream(inputStream, outputStream);
            Assert.assertArrayEquals(b1, outputStream.toByteArray());
        }

        // cleanup after tests
        MediaStoreUtils.deleteMediaFolder(appContext, backupFolderName);
    }

    @Test
    public void test200_createRollingBackup() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File dataFile = new File(appContext.getFilesDir(), dataFileName);

            if (dataFile.exists() && !dataFile.delete()) {
                throw new RuntimeException("Error deleting existing file:" + dataFile.getAbsolutePath());
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

            MediaStoreBackupProcessor bp = new MediaStoreBackupProcessor(appContext, dataFile.getAbsolutePath(), backupFolderName, backupFileName);

            MediaStoreUtils.deleteMediaFolder(appContext, backupFolderName);

            Assert.assertNotNull(bp.createRollingBackup());

            //create and check if backup is created
            Assert.assertEquals(MediaStoreUtils.getDisplayNameList(appContext, backupFolderName).size(), 1);

            // generate and write random bytes file 2

            byte[] b2 = new byte[3600];
            new Random().nextBytes(b2);

            try (
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(b2);
                    FileOutputStream outputStream = new FileOutputStream(dataFile);
            ) {
                FileUtils.copyStream(inputStream, outputStream);
            }

            Assert.assertNotNull(bp.createRollingBackup());
            Assert.assertEquals(MediaStoreUtils.getDisplayNameList(appContext, backupFolderName).size(), 2);

            // generate and write random bytes file 3

            byte[] b3 = new byte[5555];
            new Random().nextBytes(b3);

            try (
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(b3);
                    FileOutputStream outputStream = new FileOutputStream(dataFile);
            ) {
                FileUtils.copyStream(inputStream, outputStream);
            }

            Assert.assertNotNull(bp.createRollingBackup());

            // restore last backup
            if (dataFile.exists() && !dataFile.delete()) {
                throw new RuntimeException("Error deleting existing file:" + dataFile.getAbsolutePath());
            }

            // restore
            Assert.assertNotNull(bp.restoreBackup());
            Assert.assertTrue(dataFile.exists());

            // check data identity
            try (InputStream inputStream = new FileInputStream(dataFile);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                FileUtils.copyStream(inputStream, outputStream);
                Assert.assertArrayEquals(b3, outputStream.toByteArray());
            }

            File fo = new File(appContext.getCacheDir(), "fo");
            if (fo.exists() && !fo.delete()) {
                throw new RuntimeException("Error deleting existing file:" + fo.getAbsolutePath());
            }
            Assert.assertFalse(fo.exists());

            try (InputStream inputStream = bp.createBackupInputStream(ZipFileUtils.getZipFileName(backupFileName));
            OutputStream outputStream = new FileOutputStream(fo)) {
                Assert.assertNotNull(inputStream);
                FileUtils.copyStream(inputStream, outputStream);
            }

            Assert.assertTrue(fo.exists());

            if (fo.exists() && !fo.delete()) {
                throw new RuntimeException("Error deleting existing file:" + fo.getAbsolutePath());
            }
            Assert.assertFalse(fo.exists());

            // cleanup after tests
            MediaStoreUtils.deleteMediaFolder(appContext, backupFolderName);
        }
    }

}
