package com.romanpulov.library.common.media;

import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

public class MediaStoreUtils {
    public static final String MEDIA_STORE_VOLUME_EXTERNAL_NAME;
    public static final String MEDIA_STORE_RELATIVE_PATH_NAME;
    public static final String MEDIA_STORE_ROOT_PATH;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MEDIA_STORE_VOLUME_EXTERNAL_NAME = MediaStore.VOLUME_EXTERNAL_PRIMARY;
            MEDIA_STORE_RELATIVE_PATH_NAME = MediaStore.MediaColumns.RELATIVE_PATH;
            MEDIA_STORE_ROOT_PATH = Environment.DIRECTORY_DOWNLOADS;
        } else {
            MEDIA_STORE_VOLUME_EXTERNAL_NAME = "external_primary";
            MEDIA_STORE_RELATIVE_PATH_NAME = "relative_path";
            MEDIA_STORE_ROOT_PATH = "Documents";
        }
    }
}
