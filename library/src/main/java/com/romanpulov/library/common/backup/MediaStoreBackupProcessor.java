package com.romanpulov.library.common.backup;

import com.romanpulov.jutilscore.storage.BackupProcessor;

import java.util.List;

public class MediaStoreBackupProcessor implements BackupProcessor {
    private final String mDataFileName;
    private final String mBackupFileName;
    private final String mBackupFolderName;

    public MediaStoreBackupProcessor(String mDataFileName, String mBackupFileName, String mBackupFolderName) {
        this.mDataFileName = mDataFileName;
        this.mBackupFileName = mBackupFileName;
        this.mBackupFolderName = mBackupFolderName;
    }

    @Override
    public String createRollingBackup() {
        return null;
    }

    @Override
    public String restoreBackup() {
        return null;
    }

    @Override
    public List<String> getBackupFileNames() {
        return null;
    }
}
