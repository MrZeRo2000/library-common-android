package com.romanpulov.library.common;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.romanpulov.jutilscore.io.FileUtils;
import com.romanpulov.jutilscore.storage.BackupProcessor;
import com.romanpulov.library.common.backup.MediaStoreBackupProcessor;
import com.romanpulov.library.common.media.MediaStoreUtils;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MediaStoreBackupProcessorTest {
    private final static Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    final String dataFileName = "data-file-to-backup.bin";
    final String backupFolderName = MediaStoreUtils.MEDIA_STORE_ROOT_PATH + "/library-common-test-backup-processor/";
    final String backupFileName = "data-file-backup.zip";

    @Test
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
        Assert.assertEquals(MediaStoreUtils.getDisplayNameList(appContext, backupFolderName).size(), 0);

        Assert.assertEquals(backupFileName, bp.createSingleBackup());
    }
}
