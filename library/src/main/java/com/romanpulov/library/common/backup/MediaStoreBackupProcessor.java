package com.romanpulov.library.common.backup;

import android.content.Context;

import com.romanpulov.jutilscore.storage.BackupProcessor;
import com.romanpulov.library.common.media.MediaStoreUtils;

import java.util.List;

public class MediaStoreBackupProcessor implements BackupProcessor {
    private final Context mContext;
    private final String mDataFileName;
    private final String mBackupFileName;
    private final String mBackupFolderName;

    public MediaStoreBackupProcessor(Context context, String mDataFileName, String mBackupFileName, String mBackupFolderName) {
        this.mContext = context;
        this.mDataFileName = mDataFileName;
        this.mBackupFileName = mBackupFileName;
        this.mBackupFolderName = mBackupFolderName;
    }

    public String createSingleBackup() {


        return null;
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
        return MediaStoreUtils.getDisplayNameList(mContext, mBackupFolderName);
    }
}
