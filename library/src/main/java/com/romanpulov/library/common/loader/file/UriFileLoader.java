package com.romanpulov.library.common.loader.file;

import android.content.Context;
import android.net.Uri;

import com.romanpulov.library.common.loader.core.LoadPathProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class UriFileLoader extends FileLoader {
    public UriFileLoader(Context context, LoadPathProvider loadPathProvider) {
        super(context, loadPathProvider);
    }

    @Override
    public void load() throws Exception {
        try (InputStream inputStream = mContext.getContentResolver().openInputStream(Uri.parse(getLoadPathProvider().getSourcePath()));
             OutputStream outputStream = new FileOutputStream(new File(getLoadPathProvider().getDestPath()));
        )
        {
            if (inputStream != null) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0)
                    outputStream.write(buf, 0, len);
                outputStream.flush();
            } else {
                throw new IOException("Failed to create an input stream from Uri " + getLoadPathProvider().getSourcePath());
            }
        }
    }

    @SuppressWarnings("unused")
    public static boolean isLoaderInternetRequired() {return false;}
}
