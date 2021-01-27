package com.romanpulov.library.common.logger;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.romanpulov.library.common.media.MediaStoreUtils;

import java.io.IOException;
import java.io.OutputStream;

public class MediaStoreLogger extends AbstractLogger {
    private final Context mContext;

    private Uri mContentUriWithId;

    public MediaStoreLogger(Context context, String folderName, String fileName) {
        super(folderName, fileName);
        this.mContext = context;
    }

    @Override
    public void open() throws IOException {

    }

    @Override
    public void close() {

    }

    private OutputStream getOutputStreamFromContentUri() {
        try {
            return mContext.getContentResolver().openOutputStream(mContentUriWithId, "wa");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void internalLog(String message) {
        OutputStream outputStream = null;

        if (mContentUriWithId == null) {
            Uri contentUri = MediaStore.Files.getContentUri(MediaStoreUtils.MEDIA_STORE_VOLUME_EXTERNAL_NAME);

            String selection = MediaStoreUtils.MEDIA_STORE_RELATIVE_PATH_NAME + "=?" + " and " + MediaStoreUtils.MEDIA_STORE_DISPLAY_NAME + "=?";

            String[] selectionArgs = new String[]{mFolderName, mFileName};

            try (Cursor cursor = mContext.getContentResolver().query(contentUri, null, selection, selectionArgs, null)) {

                if (cursor == null || cursor.getCount() == 0) {
                    ContentValues values = new ContentValues();

                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, mFileName);       //file name
                    // values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");        //file extension, will automatically add to file
                    values.put(MediaStoreUtils.MEDIA_STORE_RELATIVE_PATH_NAME, mFolderName);     //end "/" is not mandatory

                    Uri uri = mContext.getContentResolver().insert(MediaStore.Files.getContentUri(MediaStoreUtils.MEDIA_STORE_VOLUME_EXTERNAL_NAME), values);      //important!

                    if (uri != null) {
                        try {
                            outputStream = mContext.getContentResolver().openOutputStream(uri, "w");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));

                    mContentUriWithId = ContentUris.withAppendedId(contentUri, id);

                    outputStream = getOutputStreamFromContentUri();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            outputStream = getOutputStreamFromContentUri();
        }

        if (outputStream != null) {
            try {
                outputStream.write(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
