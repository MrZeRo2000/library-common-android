package com.romanpulov.library.common;

import com.romanpulov.library.common.io.FileUtils;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

import static org.junit.Assert.assertTrue;

/**
 * Created by romanpulov on 19.12.2016.
 */

public class FileUtilsTest {
    private static final String FILE_PATH = "../data/";
    private static final String FILE_NAME = "filetest.txt";

    @Test
    public void test1() {
        assertTrue(1==1);
    }

    @Test
    public void test2() throws Exception{
        String fileName = FILE_PATH + FILE_NAME;
        String tempFileName = FileUtils.getTempFileName(fileName);

        // delete existing
        File f = new File(tempFileName);
        if (f.exists())
            f.delete();

        //create new
        FileOutputStream outputStream = new FileOutputStream(f);
        outputStream.write(32 + (int)(Math.random() * 50));
        outputStream.flush();
        outputStream.close();

        //save copies
        FileUtils.saveCopies(fileName);
        //set new
        FileUtils.renameTempFile(tempFileName);
    }
}
