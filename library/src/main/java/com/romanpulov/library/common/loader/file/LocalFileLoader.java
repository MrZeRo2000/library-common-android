package com.romanpulov.library.common.loader.file;

import android.content.Context;

import com.romanpulov.library.common.loader.core.LoadPathProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Base class for local file loader
 * Created by romanpulov on 22.09.2017.
 */

public abstract class LocalFileLoader extends FileLoader {

    public LocalFileLoader(Context context, LoadPathProvider loadPathProvider) {
        super(context, loadPathProvider);
    }

    @Override
    public void load() throws Exception {
        InputStream inputStream = new FileInputStream(getLoadPathProvider().getSourcePath());
        OutputStream outputStream = new FileOutputStream(new File(getLoadPathProvider().getDestPath()));
        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                outputStream.write(buf, 0, len);
        } finally {
            try {
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unused")
    public static boolean isLoaderInternetRequired() {return false;}
}
