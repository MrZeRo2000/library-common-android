package com.romanpulov.library.common.loader.core;

import android.content.Context;

/**
 * Common loader context class
 * Created by romanpulov on 06.09.2017.
 */

public abstract class AbstractContextLoader implements Loader {

    protected final Context mContext;

    public AbstractContextLoader(Context context) {
        mContext = context;
    }

    public abstract void load() throws Exception;

}
