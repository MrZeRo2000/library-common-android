package com.romanpulov.library.common;

import com.romanpulov.library.common.logger.AbstractLogger;
import com.romanpulov.library.common.logger.FileLogger;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by rpulov on 24.12.2016.
 */

public class LoggerTest {
    @Test
    public void levelTest() {
        String testString = AbstractLogger.getLevelString(4, "d");
        Assert.assertEquals(testString, "dddd");
        testString = AbstractLogger.getLevelString(0, "d");
        System.out.println("Level from 0: " + testString);
    }

    @Test
    public void logTest() throws  Exception {
        AbstractLogger logger = new FileLogger("../data/", "log.txt");

        logger.open();

        logger.log("tag", "message", 0);
        logger.log("tag", "message 1", 1);
        logger.log("tag2", "message 2", 2);

        logger.log("ddd", "new message");
    }
}
