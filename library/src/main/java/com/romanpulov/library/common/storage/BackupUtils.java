package com.romanpulov.library.common.storage;

import android.os.Environment;

import com.romanpulov.library.common.io.FileUtils;
import com.romanpulov.library.common.io.ZipFileUtils;

import java.io.File;

/**
 * Backup and restore for file
 * Created by romanpulov on 19.12.2016.
 */

public class BackupUtils {
    private final String mDataFileName;
    private final String mLocalBackupFolderName;
    private final String mLocalBackupFileName;

    private String getLocalBackupFolderName() {
        return Environment.getExternalStorageDirectory().toString() + "/" + mLocalBackupFolderName + "/";
    }

    private String getLocalBackupFileName() {
        return getLocalBackupFolderName() + mLocalBackupFileName;
    }

    private String getLocalBackupZIPFileName() {
        return ZipFileUtils.getZipFileName(getLocalBackupFileName());
    }

    public BackupUtils(String dataFileName, String localBackupFolderName, String localBackupFileName) {
        mDataFileName = dataFileName;
        mLocalBackupFolderName = localBackupFolderName;
        mLocalBackupFileName = localBackupFileName;
    }

    /**
     * Created local backup
     * @return archived file name if successful
     */
    private String createLocalBackup() {
        //init backup folder
        File backupFolder = new File(getLocalBackupFolderName());
        if (!backupFolder.exists()) {
            if (!backupFolder.mkdir()) {
                return null;
            }
        }

        //write file
        if (!FileUtils.copy(mDataFileName, getLocalBackupFileName()))
            return null;

        //archive file
        return ZipFileUtils.zipFile(getLocalBackupFolderName(), mLocalBackupFileName);
    }

    /**
     * Restores backup from local archive
     * @return restored file name if successful
     */
    public String restoreLocalBackup() {
        String localBackupFileName = getLocalBackupFileName();

        //check backup availability
        File zipFile = new File(getLocalBackupZIPFileName());
        if (!zipFile.exists())
            return null;

        //extract backup
        if (!ZipFileUtils.unZipFile(getLocalBackupFolderName(), ZipFileUtils.getZipFileName(mLocalBackupFileName)))
            return null;

        //check restored file availability
        File file = new File(localBackupFileName);
        if (!file.exists())
            return null;

        //replace database file
        if (!FileUtils.copy(localBackupFileName, mDataFileName))
            return null;

        //delete and ignore any errors
        file.delete();

        return localBackupFileName;
    }

    /**
     * Created rolling backup
     * @return archived file if successful
     */
    public String createRollingLocalBackup() {
        //get file names
        String fileName = getLocalBackupFileName();
        String zipFileName = getLocalBackupZIPFileName();

        //roll copies of data: first try rename, then copy
        if (!FileUtils.renameCopies(zipFileName))
            if ((!FileUtils.saveCopies(zipFileName)))
                return null;

        //create backup
        String result = createLocalBackup();

        //delete non zipped file, ignore any errors
        if (result != null)
            FileUtils.delete(fileName);

        return result;
    }
}
