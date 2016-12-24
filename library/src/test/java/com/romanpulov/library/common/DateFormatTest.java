package com.romanpulov.library.common;

import com.romanpulov.library.common.logger.DateFormatter;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by rpulov on 24.12.2016.
 */

public class DateFormatTest {
    @Test
    public void simpleTest() {

        Date currentDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        System.out.println("formattedDate = " + formattedDate);

        GregorianCalendar cal = new GregorianCalendar(Locale.getDefault());
        cal.setTimeInMillis(155);
        cal.set(2000, 7, 4, 12, 33, 25);

        Date testDate = cal.getTime();
        String formattedTestDate = dateFormat.format(testDate);
        System.out.println("formattedTestDate = " + formattedTestDate);

        Assert.assertEquals(formattedTestDate, "2000-08-04 12:33:25.155");

        DateFormatter formatter = new DateFormatter();
        String formatterString = formatter.format(cal.getTimeInMillis());
        Assert.assertEquals(formatterString, "2000-08-04 12:33:25.155");
    }

}
