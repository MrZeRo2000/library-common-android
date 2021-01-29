package com.romanpulov.library.common.backup;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.romanpulov.jutilscore.io.FileUtils;
import com.romanpulov.jutilscore.io.ZipFileUtils;
import com.romanpulov.jutilscore.storage.BackupProcessor;
import com.romanpulov.library.common.media.MediaStoreUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

        String zipFileName  = ZipFileUtils.getZipFileName(mBackupFileName);
        try (Cursor cursor = MediaStoreUtils.queryMediaFile(mContext, mBackupFolderName, zipFileName)) {
            if (cursor == null || cursor.getCount() == 0) {
                uri = MediaStoreUtils.insertMediaFile(mContext, mBackupFolderName, zipFileName);

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
        final Map<String, Uri> displayNameUriList = MediaStoreUtils.getDisplayNameUriList(mContext, mBackupFolderName);

        if (displayNameUriList.isEmpty()) {
            return null;
        }

        final Collection<String> fileNameList = displayNameUriList.keySet();

        boolean processListCopiesResult = FileUtils.processListCopies(fileNameList, new FileUtils.FileProcessor() {
            @Override
            public boolean process(String fromFileName, String toFileName) {
                Uri fromFileUri = displayNameUriList.get(fromFileName);

                Uri toFileUri = displayNameUriList.get(toFileName);
                if (toFileUri == null) {
                    toFileUri = MediaStoreUtils.insertMediaFile(mContext, mBackupFolderName, toFileName);
                }

                if (fromFileUri == null || toFileUri == null) {
                    return false;
                }

                try (
                        InputStream inputStream = mContext.getContentResolver().openInputStream(fromFileUri);
                        OutputStream outputStream = mContext.getContentResolver().openOutputStream(toFileUri, "wt")
                    ){
                    if (inputStream != null) {
                        FileUtils.copyStream(inputStream, outputStream);
                        return true;
                    } else {
                        return false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });

        if (!processListCopiesResult) {
            return null;
        } else {
            return createSingleBackup();
        }
    }

    @Override
    public String restoreBackup() {
        String zipFileName = ZipFileUtils.getZipFileName(mBackupFileName);
        Uri backupFileUri = MediaStoreUtils.queryFirstMediaFileUri(mContext, mBackupFolderName, zipFileName);

        if (backupFileUri == null) {
            return null;
        }

        File tempFile = null;

        try {
            tempFile = File.createTempFile(mBackupFileName, "backup", mContext.getCacheDir());

            try (
                    InputStream inputStream = mContext.getContentResolver().openInputStream(backupFileUri);
                    OutputStream outputStream = new FileOutputStream(tempFile);
            ) {
                if (inputStream != null) {
                    ZipFileUtils.unZipStream(inputStream, outputStream);
                } else {
                    throw new MediaStoreBackupException("Error unzipping stream");
                }
            }

            if (!FileUtils.copy(tempFile.getAbsolutePath(), mDataFileName)) {
                throw new MediaStoreBackupException("Error copying from " + tempFile.getAbsolutePath() + " to " + mDataFileName);
            }

            return mDataFileName;
        } catch (IOException | MediaStoreBackupException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (tempFile != null && tempFile.exists()) {
                boolean deleteResult = tempFile.delete();
            }
        }
    }

    @Override
    public List<String> getBackupFileNames() {
        return MediaStoreUtils.getDisplayNameList(mContext, mBackupFolderName);
    }
}
