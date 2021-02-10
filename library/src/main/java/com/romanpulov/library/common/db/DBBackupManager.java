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
    // backup folder name
    private final String mLocalBackupFolderName;
    // database backup file name
    private final String mLocalBackupDBFileName;
    // database controller
    private final DBController mDBController;

    public DBBackupManager(String mLocalBackupFolderName, String mLocalBackupDBFileName, DBController mDBController) {
        this.mLocalBackupFolderName = mLocalBackupFolderName;
        this.mLocalBackupDBFileName = mLocalBackupDBFileName;
        this.mDBController = mDBController;
    }

    /**
     * Restores backup from a temporary file
     * @param context Context to create BackupProcessor
     * @param path File path to restore
     * @return success flag
     */
    public boolean restoreFromBackupPath(@NonNull Context context, String path) {
        File file = new File(path);
        if (file.exists()) {
            String restoreResult = restorePathBackup(context, file.getParent());

            return  restoreResult != null;
        } else {
            return false;
        }
    }

    /**
     * Create internally BackupProcessor to support differences between Android versions
     * @param context Context
     * @param dataFileName Data file name
     * @return BackupProcessor implementation
     */
    @NonNull
    private BackupProcessor createBackupProcessor(@NonNull Context context, String dataFileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new MediaStoreBackupProcessor(
                    context,
                    dataFileName,
                    MediaStoreUtils.MEDIA_STORE_ROOT_PATH + '/' + mLocalBackupFolderName + '/',
                    mLocalBackupDBFileName
            );
        } else {
            File f = context.getExternalFilesDir(mLocalBackupFolderName);
            if (f == null) {
                // should not get here
                f = new File(context.getFilesDir(), mLocalBackupFolderName);
            }

            return new FileBackupProcessor(
                    dataFileName,
                    f.getAbsolutePath() + '/',
                    mLocalBackupDBFileName
            );
        }
    }

    /**
     * Creates backup locally
     * @param context Context
     * @return backup creation result as file name, null if failed
     */
    public String createLocalBackup(@NonNull Context context) {
        BackupProcessor bp = createBackupProcessor(
                context,
                context.getDatabasePath(mDBController.getDBName()).toString()
        );

        mDBController.closeDB();
        String result = bp.createRollingBackup();
        mDBController.openDB();

        return result;
    }

    /**
     * Restores backup from local file via BackupProcessor
     * @param context Context
     * @return restore result as file name, null if failed
     */
    public String restoreLocalBackup(@NonNull Context context) {

        BackupProcessor bp = createBackupProcessor(
                context,
                context.getDatabasePath(mDBController.getDBName()).toString()
        );

        mDBController.closeDB();
        String result = bp.restoreBackup();
        mDBController.openDB();

        mDBController.dbDataChanged();

        return result;
    }

    /**
     * Restores backup from local file via FileBackupProcessor
     * @param context Context
     * @param restorePath Path to restore
     * @return restore result as file name, null if failed
     */
    public String restorePathBackup(@NonNull Context context, String restorePath) {

        BackupProcessor bp =  new FileBackupProcessor(
                context.getDatabasePath(mDBController.getDBName()).toString(),
                restorePath,
                mLocalBackupDBFileName
        );

        mDBController.closeDB();
        String result = bp.restoreBackup();
        mDBController.openDB();

        mDBController.dbDataChanged();

        return result;
    }

    /**
     * Returns database backup file names
     * @param context Context
     * @return File name list
     */
    public List<String> getDatabaseBackupFiles(@NonNull Context context) {
        return createBackupProcessor(context, null).getBackupFileNames();
    }

    /**
     * Creates InputStream for backup
     * @param context Context
     * @param backupFileName Backup file name
     * @return InputStream
     * @throws IOException if InputStream is not created
     */
    public InputStream createBackupInputStream(@NonNull Context context, String backupFileName) throws IOException {
        return createBackupProcessor(context, mLocalBackupDBFileName).createBackupInputStream(backupFileName);
    }
}
