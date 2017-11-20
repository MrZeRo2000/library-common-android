package com.romanpulov.library.common.loader.file;

import android.content.Context;

import com.romanpulov.library.common.loader.core.AbstractContextLoader;
import com.romanpulov.library.common.loader.core.LoadPathProvider;

/**
 * Abstract class for file loader
 * Created by romanpulov on 20.09.2017.
 */

public abstract class FileLoader extends AbstractContextLoader {
    private final LoadPathProvider mLoadPathProvider;

    public LoadPathProvider getLoadPathProvider() {
        return mLoadPathProvider;
    }

    public FileLoader(Context context, LoadPathProvider loadPathProvider) {
        super(context);
        mLoadPathProvider = loadPathProvider;
    }
}
