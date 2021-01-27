package com.romanpulov.library.common.backup;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.romanpulov.jutilscore.io.ZipFileUtils;
import com.romanpulov.jutilscore.storage.BackupProcessor;
import com.romanpulov.library.common.media.MediaStoreUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MediaStoreBackupProcessor implements BackupProcessor {
    private final Context mContext;
    private final String mDataFileName;
    private final String mBackupFolderName;
    private final String mBackupFileName;

    public MediaStoreBackupProcessor(Context context, String mDataFileName, String mBackupFolderName, String mBackupFileName) {
        this.mContext = context;
        this.mDataFileName = mDataFileName;
        this.mBackupFolderName = mBackupFolderName;
        this.mBackupFileName = mBackupFileName;
    }

    public String createSingleBackup() {
        Uri uri = null;

        try (Cursor cursor = MediaStoreUtils.queryMediaFile(mContext, mBackupFolderName, mBackupFileName)) {
            if (cursor == null || cursor.getCount() == 0) {
                uri = MediaStoreUtils.insertMediaFile(mContext, mBackupFolderName, mBackupFileName);

            } else if (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                uri = ContentUris.withAppendedId(MediaStoreUtils.getMediaFilesContentUri(), id);
            }
        }

        if (uri != null) {
            try (
                    InputStream inputStream = new FileInputStream(mDataFileName);
                    OutputStream outputStream = mContext.getContentResolver().openOutputStream(uri, "wt");
            ) {
                if (outputStream != null) {
                    ZipFileUtils.zipStream(mBackupFileName, inputStream, outputStream);
                    return mBackupFileName;
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }

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
