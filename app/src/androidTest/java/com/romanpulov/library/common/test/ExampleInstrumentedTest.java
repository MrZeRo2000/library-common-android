package com.romanpulov.library.common.test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.romanpulov.library.common.test", appContext.getPackageName());
    }

    @Test
    public void createNewFile() throws Exception {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        ContentValues values = new ContentValues();

        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "test.txt");       //file name
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");        //file extension, will automatically add to file
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/library-common-test/");     //end "/" is not mandatory

        Uri uri = appContext.getContentResolver().insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values);      //important!

        OutputStream outputStream = appContext.getContentResolver().openOutputStream(uri, "rwt");

        outputStream.write("This is a test data.".getBytes());

        outputStream.close();

    }

    @Test
    public void writeToDocumentsTest() throws Exception {
        String TAG = "writeToDocumentsTest";

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Log.d(TAG, "Started");


        Uri contentUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

        String selection = MediaStore.MediaColumns.RELATIVE_PATH + "=?" + " and " + MediaStore.MediaColumns.DISPLAY_NAME + "=?";

        String[] selectionArgs = new String[]{Environment.DIRECTORY_DOCUMENTS + "/library-common-test/", "test.txt"};    //must include "/" in front and end

        Cursor cursor = appContext.getContentResolver().query(contentUri, null, selection, selectionArgs, null);

        if (cursor.getCount() == 0) {
            throw new RuntimeException("Cursor is empty");
        } else {
            while (cursor.moveToNext()) {
                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                Log.d(TAG, "Found file:" + fileName);

                if (fileName.equals("test.txt")) {                          //must include extension
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));

                    Uri uri = ContentUris.withAppendedId(contentUri, id);

                    OutputStream outputStream = appContext.getContentResolver().openOutputStream(uri, "wa");      //overwrite mode, see below

                    outputStream.write("\r\nAppending data".getBytes());

                    outputStream.close();

                    break;
                }
            }
        }
    }

    @Test
    public void createOrAppendDocumentsTest() {
        String TAG = "createOrAppendDocumentsTest";

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        String logFolder = Environment.DIRECTORY_DOCUMENTS + "/library-common-test/log/";
        String logFileName = "log.txt";

        Uri contentUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

        String selection = MediaStore.MediaColumns.RELATIVE_PATH + "=?" + " and " + MediaStore.MediaColumns.DISPLAY_NAME + "=?";

        String[] selectionArgs = new String[]{logFolder, logFileName};

        Cursor cursor = appContext.getContentResolver().query(contentUri, null, selection, selectionArgs, null);

        OutputStream outputStream = null;

        if (cursor == null || cursor.getCount() == 0) {
            ContentValues values = new ContentValues();

            values.put(MediaStore.MediaColumns.DISPLAY_NAME, logFileName);       //file name
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");        //file extension, will automatically add to file
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, logFolder);     //end "/" is not mandatory

            Uri uri = appContext.getContentResolver().insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values);      //important!

            if (uri != null) {
                try {
                    outputStream = appContext.getContentResolver().openOutputStream(uri, "w");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));

            Uri uri = ContentUris.withAppendedId(contentUri, id);

            try {
                outputStream = appContext.getContentResolver().openOutputStream(uri, "wa");      //overwrite mode, see below
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (outputStream != null) {
            try {
                outputStream.write("New stuff to write".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
