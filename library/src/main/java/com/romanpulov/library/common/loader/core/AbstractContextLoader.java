package com.romanpulov.library.common.loader.core;

import android.content.Context;

/**
 * Common loader context class
 * Created by romanpulov on 06.09.2017.
 */

public abstract class AbstractContextLoader extends AbstractLoader {
    protected final Context mContext;

    public AbstractContextLoader(Context context) {
        mContext = context;
    }

}
