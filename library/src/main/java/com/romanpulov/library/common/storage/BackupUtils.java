package com.romanpulov.library.common.storage;

import android.content.Context;
import android.os.Environment;

import com.romanpulov.library.common.io.FileUtils;
import com.romanpulov.library.common.io.ZipFileUtils;

import java.io.File;

/**
 * Backup and restore for database
 * Created by romanpulov on 19.12.2016.
 */

public class BackupUtils {
    private final Context mContext;
    private final String mDatabaseName;
    private final String mlocalBackupFolderName;
    private final String mlocalBackupFileName;

    public String getLocalBackupFolderName() {
        return Environment.getExternalStorageDirectory().toString() + "/" + mlocalBackupFolderName + "/";
    }

    public String getLocalBackupFileName() {
        return getLocalBackupFolderName() + mlocalBackupFileName;
    }

    public String getLocalBackupZIPFileName() {
        return ZipFileUtils.getZipFileName(getLocalBackupFileName());
    }

    public BackupUtils(Context context, String databaseName, String localBackupFolderName, String localBackupFileName) {
        mContext = context;
        mDatabaseName = databaseName;
        mlocalBackupFolderName = localBackupFolderName;
        mlocalBackupFileName = localBackupFileName;
    }

    private String getDatabasePath() {
        return mContext.getDatabasePath(mDatabaseName).toString();
    }

    public String createLocalBackup() {
        //init backup folder
        File backupFolder = new File(getLocalBackupFolderName());
        if (!backupFolder.exists()) {
            if (!backupFolder.mkdir()) {
                return null;
            }
        }

        //write file
        if (!FileUtils.copy(getDatabasePath(), getLocalBackupFileName()))
            return null;

        //archive file
        return ZipFileUtils.zipFile(getLocalBackupFolderName(), mlocalBackupFileName);
    }

    public String restoreLocalBackup() {
        String localBackupFileName = getLocalBackupFileName();

        //check backup availability
        File zipFile = new File(getLocalBackupZIPFileName());
        if (!zipFile.exists())
            return null;

        //extract backup
        if (!ZipFileUtils.unZipFile(getLocalBackupFolderName(), ZipFileUtils.getZipFileName(mlocalBackupFileName)))
            return null;

        //check restored file availability
        File file = new File(localBackupFileName);
        if (!file.exists())
            return null;

        //replace database file
        if (!FileUtils.copy(localBackupFileName, getDatabasePath()))
            return null;

        //delete and ignore any errors
        file.delete();

        return localBackupFileName;
    }

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
