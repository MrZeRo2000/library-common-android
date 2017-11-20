package com.romanpulov.library.common.loader.core;

import android.content.Context;

/**
 * Base class for context dependent load path provider
 * Created by romanpulov on 22.09.2017.
 */

public abstract class ContextLoadPathProvider implements LoadPathProvider {
    private final Context mContext;

    protected Context getContext() {
        return mContext;
    }

    public ContextLoadPathProvider(Context context) {
        mContext = context;
    }
}
