package com.romanpulov.library.common.db;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.romanpulov.jutilscore.storage.BackupProcessor;
import com.romanpulov.jutilscore.storage.FileBackupProcessor;
import com.romanpulov.library.common.backup.MediaStoreBackupProcessor;
import com.romanpulov.library.common.media.MediaStoreUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DBBackupManager {
    private final Context mContext;
    // backup folder name
    private final String mLocalBackupFolderName;
    // database backup file name
    private final String mLocalBackupDBFileName;
    // database controller
    private final DBController mDBController;
    // last backup processor
    private BackupProcessor mBP;

    public BackupProcessor getBackupProcessor() {
        return mBP;
    }

    public DBBackupManager(
            Context context,
            String localBackupFolderName,
            String localBackupDBFileName,
            DBController dbController
    ) {
        this.mContext = context;
        this.mLocalBackupFolderName = localBackupFolderName;
        this.mLocalBackupDBFileName = localBackupDBFileName;
        this.mDBController = dbController;
    }

    /**
     * Restores backup from a temporary file
     * @param path File path to restore
     * @return success flag
     */
    public boolean restoreFromBackupPath(String path) {
        File file = new File(path);
        if (file.exists()) {
            String restoreResult = restorePathBackup(file.getParent());

            return  restoreResult != null;
        } else {
            return false;
        }
    }

    /**
     * Create internally BackupProcessor to support differences between Android versions
     * @param dataFileName Data file name
     * @return BackupProcessor implementation
     */
    @NonNull
    private BackupProcessor createBackupProcessor(String dataFileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new MediaStoreBackupProcessor(
                    mContext,
                    dataFileName,
                    MediaStoreUtils.MEDIA_STORE_ROOT_PATH + '/' + mLocalBackupFolderName + '/',
                    mLocalBackupDBFileName
            );
        } else {
            File f = mContext.getExternalFilesDir(mLocalBackupFolderName);
            if (f == null) {
                // should not get here
                f = new File(mContext.getFilesDir(), mLocalBackupFolderName);
            }

            return new FileBackupProcessor(
                    dataFileName,
                    f.getAbsolutePath() + '/',
                    mLocalBackupDBFileName
            );
        }
    }

    /**
     * Creates backup locally     *
     * @return backup creation result as file name, null if failed
     */
    public String createLocalBackup() {
        mBP = createBackupProcessor(
                mContext.getDatabasePath(mDBController.getDBName()).toString()
        );

        mDBController.closeDB();
        String result = mBP.createRollingBackup();
        mDBController.openDB();

        return result;
    }

    /**
     * Restores backup from local file via BackupProcessor
     * @return restore result as file name, null if failed
     */
    public String restoreLocalBackup() {

        mBP = createBackupProcessor(
                mContext.getDatabasePath(mDBController.getDBName()).toString()
        );

        mDBController.closeDB();
        String result = mBP.restoreBackup();
        mDBController.openDB();

        mDBController.dbDataChanged();

        return result;
    }

    /**
     * Restores backup from local file via FileBackupProcessor
     * @param restorePath Path to restore
     * @return restore result as file name, null if failed
     */
    public String restorePathBackup(String restorePath) {
        mBP =  new FileBackupProcessor(
                mContext.getDatabasePath(mDBController.getDBName()).toString(),
                restorePath,
                mLocalBackupDBFileName
        );

        mDBController.closeDB();
        String result = mBP.restoreBackup();
        mDBController.openDB();

        mDBController.dbDataChanged();

        return result;
    }

    /**
     * Returns database backup file names
     * @return File name list
     */
    public List<String> getDatabaseBackupFiles() {
        return createBackupProcessor(null).getBackupFileNames();
    }

    /**
     * Creates InputStream for backup
     * @param backupFileName Backup file name
     * @return InputStream
     * @throws IOException if InputStream is not created
     */
    public InputStream createBackupInputStream(String backupFileName) throws IOException {
        return createBackupProcessor(mLocalBackupDBFileName).createBackupInputStream(backupFileName);
    }
}
