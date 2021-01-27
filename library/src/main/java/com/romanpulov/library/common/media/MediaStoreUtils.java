package com.romanpulov.library.common.media;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MediaStoreUtils {
    public static final String MEDIA_STORE_VOLUME_EXTERNAL_NAME;
    public static final String MEDIA_STORE_RELATIVE_PATH_NAME;
    public static final String MEDIA_STORE_DISPLAY_NAME;
    public static final String MEDIA_STORE_ROOT_PATH;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MEDIA_STORE_VOLUME_EXTERNAL_NAME = MediaStore.VOLUME_EXTERNAL_PRIMARY;
            MEDIA_STORE_RELATIVE_PATH_NAME = MediaStore.MediaColumns.RELATIVE_PATH;
            MEDIA_STORE_DISPLAY_NAME = MediaStore.MediaColumns.DISPLAY_NAME;
            MEDIA_STORE_ROOT_PATH = Environment.DIRECTORY_DOWNLOADS;
        } else {
            MEDIA_STORE_VOLUME_EXTERNAL_NAME = "external_primary";
            MEDIA_STORE_RELATIVE_PATH_NAME = "relative_path";
            MEDIA_STORE_DISPLAY_NAME = "_display_name";
            MEDIA_STORE_ROOT_PATH = "Documents";
        }
    }

    public static Uri getMediaFilesContentUri() {
        return MediaStore.Files.getContentUri(MediaStoreUtils.MEDIA_STORE_VOLUME_EXTERNAL_NAME);
    }

    public static Uri insertMediaFile(Context context, String folderName, String fileName) {
        ContentValues values = new ContentValues();

        values.put(MediaStoreUtils.MEDIA_STORE_DISPLAY_NAME, fileName);
        values.put(MediaStoreUtils.MEDIA_STORE_RELATIVE_PATH_NAME, folderName);

        return context.getContentResolver().insert(MediaStoreUtils.getMediaFilesContentUri(), values);
    }

    public static int deleteMediaFolder(Context context, String folderName) {
        return context.getContentResolver().delete(
                MediaStoreUtils.getMediaFilesContentUri(),
                MediaStoreUtils.MEDIA_STORE_RELATIVE_PATH_NAME + "=?",
                new String[]{folderName}
        );
    }

    public static Cursor queryMediaFolder(Context context, String folderName) {
        return context.getContentResolver().query(
                MediaStore.Files.getContentUri(MediaStoreUtils.MEDIA_STORE_VOLUME_EXTERNAL_NAME),
                null,
                MEDIA_STORE_RELATIVE_PATH_NAME + "=?",
                new String[]{folderName},
                null);
    }

    public static Cursor queryMediaFile(Context context, String folderName, String fileName) {
        return context.getContentResolver().query(
                MediaStore.Files.getContentUri(MediaStoreUtils.MEDIA_STORE_VOLUME_EXTERNAL_NAME),
                null,
                MediaStoreUtils.MEDIA_STORE_RELATIVE_PATH_NAME + "=?" + " and " + MediaStoreUtils.MEDIA_STORE_DISPLAY_NAME + "=?",
                new String[]{folderName, fileName},
                null);
    }

    public static Uri queryFirstMediaFileUri (Context context, String folderName, String fileName) {
        Uri uri = null;

        try (Cursor cursor = MediaStoreUtils.queryMediaFile(context, folderName, fileName)) {
            if (cursor != null && cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                uri = ContentUris.withAppendedId(MediaStoreUtils.getMediaFilesContentUri(), id);
            }
        }

        return uri;
    }

    public static List<String> getDisplayNameList(Context context, String folderName) {
        List<String> displayNameList = new ArrayList<>();

        try (Cursor cursor = queryMediaFolder(context, folderName)
        ) {
            while (cursor != null && cursor.moveToNext()) {
                String displayName = cursor.getString(cursor.getColumnIndex(MediaStoreUtils.MEDIA_STORE_DISPLAY_NAME));
                displayNameList.add(displayName);
            }
        }

        return displayNameList;
    }
}
