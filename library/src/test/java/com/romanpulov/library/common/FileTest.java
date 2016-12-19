package com.romanpulov.library.common;

import org.junit.Test;

import java.io.File;

/**
 * Created by rpulov on 19.12.2016.
 */

public class FileTest {
    @Test
    public void simpleFilePathTest() throws Exception {
        File f = new File("../data/");
        for (File ff : f.listFiles()) {
            String s = ff.getAbsolutePath();
            System.out.println(s);
            File f1 = new File(s);
            System.out.println("Name = " + f1.getName() + ", Path = " + f1.getParent());
        }
    }
}
