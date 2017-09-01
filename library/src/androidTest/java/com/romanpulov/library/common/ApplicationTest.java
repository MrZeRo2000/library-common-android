package com.romanpulov.library.common;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.romanpulov.library.common.storage.BackupUtils;

import java.io.File;

/**
 * Created by romanpulov on 01.09.2017.
 */

public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testBackupFiles() {
        BackupUtils backupUtils = new BackupUtils("test", "VioletNoteBackup", "test");
        File[] backupFiles = backupUtils.getLocalBackupFiles();

        for (File f : backupFiles)
            Log.d("testBackupFiles", f.getName());
    }
}
