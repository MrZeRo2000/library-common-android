package com.romanpulov.library.common.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by rpulov on 24.12.2016.
 */

public class FileLogger extends AbstractLogger {

    private PrintWriter mPrintWriter;

    public FileLogger(String folderName, String fileName) {
        super(folderName, fileName);
    }

    @Override
    public synchronized void open() throws IOException {
        File logFile = new File(mFolderName + mFileName);
        mPrintWriter = null;

        FileOutputStream fileOutputStream = new FileOutputStream(logFile, true);
        mPrintWriter = new PrintWriter(fileOutputStream);
    }

    @Override
    public synchronized void close()  {
        if (mPrintWriter != null) {
            mPrintWriter.close();
        }
    }

    @Override
    protected synchronized void internalLog(String message) {
        if ((message != null) && (!message.isEmpty())) {

            // try to open
            if (mPrintWriter == null)
                try {
                    open();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            // write if possible
            if (mPrintWriter != null) {
                mPrintWriter.write(message);
                mPrintWriter.flush();
            }
        }
    }
}
