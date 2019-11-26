package com.romanpulov.library.common.loader.core;

import android.os.Bundle;

/**
 * Common loader interface
 * Created by romanpulov on 17.11.2017.
 */

public interface Loader {
    void load() throws Exception;
    void setBundle(Bundle bundle);
}

