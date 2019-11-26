package com.romanpulov.library.common.loader.core;

import android.os.Bundle;

/**
 * Loader class with bundle support
 */
public abstract class AbstractLoader implements Loader {
    protected Bundle mBundle;

    @Override
    public void setBundle(Bundle bundle) {
        mBundle = bundle;
    }

}
