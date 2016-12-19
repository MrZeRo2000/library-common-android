package com.romanpulov.library.common.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * File utilities
 * Created by romanpulov on 19.12.2016.
 */

public class FileUtils {
    private static final int FILE_BUF_LEN = 1024;
    private static final String FILE_EXTENSION = ".";
    private static final String FILE_TEMP_EXTENSION = ".temp";
    private static final String FILE_COPY_FORMAT = "%s.bak%02d";

    private static int mFileKeepCopiesCount = 5;

    /**
     * Sets retained file copies count, default is 5
     * @param value new value, should be 2 or more
     */
    public static void setFileKeepCopiesCount(int value) {
        if (value < 2)
            throw new RuntimeException("Invalid value for copies count, should be at least 2");
        mFileKeepCopiesCount = value;
    }

    /**
     * Returns temp file name
     * @param fileName input file
     * @return temp file name
     */
    public static String getTempFileName(String fileName) {
        return fileName + FILE_TEMP_EXTENSION;
    }

    /**
     * Removes extension if it exists
     * @param fileName input file name
     * @param extension input extension, can be null
     * @return file with removed extension
     */
    public static String removeExtension(String fileName, String extension) {
        int pos;

        if (extension == null)
            pos = fileName.lastIndexOf(FILE_EXTENSION);
        else
            pos = fileName.lastIndexOf(extension);

        if (pos == -1)
            return fileName;
        else
            return fileName.substring(0, pos);
    }

    public static String getFileNameFromTemp(String tempFileName) {
        return removeExtension(tempFileName, FILE_TEMP_EXTENSION);
    }

    /**
     * File copy procedure
     * @param sourceFileName source file
     * @param destFileName destination file
     * @return true if successful
     */
    public static boolean copy(String sourceFileName, String destFileName) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = new FileInputStream(sourceFileName);
            outputStream = new FileOutputStream(destFileName);

            //copy routine
            byte[] buf = new byte[FILE_BUF_LEN];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }

            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * File delete procedure
     * @param fileName file name to delete
     * @return true if successful
     */
    public static boolean delete(String fileName) {
        File f = new File(fileName);
        return (f.exists() && f.delete());
    }

    /**
     * Save rolling copies of a file
     * @param fileName full file name path
     * @return true if successful
     */
    public static boolean saveCopies(String fileName) {
        for (int cp = mFileKeepCopiesCount - 1; cp >= 0; cp--) {
            File fc = new File(cp == 0 ? fileName : String.format(Locale.getDefault(), FILE_COPY_FORMAT, fileName, cp));
            String copyFileName = String.format(Locale.getDefault(), FILE_COPY_FORMAT, fileName, cp + 1);
            if (fc.exists()) {
                if (!copy(fc.getPath(), copyFileName))
                    return false;
            }
        }
        return true;
    }

    /**
     * Save rolling copies of a file
     * @param fileName full file name path
     * @return true if successful
     */
    public static boolean renameCopies(String fileName) {
        for (int cp = mFileKeepCopiesCount - 1; cp >= 0; cp--) {
            File fc = new File(cp == 0 ? fileName : String.format(Locale.getDefault(), FILE_COPY_FORMAT, fileName, cp));
            String copyFileName = String.format(Locale.getDefault(), FILE_COPY_FORMAT, fileName, cp + 1);
            if (fc.exists()) {
                if (!fc.renameTo(new File(copyFileName)))
                    return false;
            }
        }
        return true;
    }

    /**
     * Renames temp file to normal file
     * @param tempFileName temp file name
     * @return true if successful
     */
    public static boolean renameTempFile(String tempFileName) {
        String targetFileName = removeExtension(tempFileName, FILE_TEMP_EXTENSION);
        return ((!targetFileName.equals(tempFileName)) && copy(tempFileName, targetFileName) && delete(tempFileName));
    }
}
