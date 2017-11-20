package com.romanpulov.library.common.loader.core;

import java.lang.reflect.Method;

/**
 * Loader helper class
 * Created by romanpulov on 17.11.2017.
 */

public class LoaderHelper {
    public static final String LOADER_INTERNET_REQUIRED_METHOD_NAME = "isLoaderInternetRequired";

    public static boolean isLoaderInternetConnectionRequired(Class<? extends AbstractContextLoader> loaderClass) {
        boolean result = false;
        try {
            Method loaderInternetRequiredMethod = loaderClass.getMethod(LOADER_INTERNET_REQUIRED_METHOD_NAME);
            result = (Boolean) loaderInternetRequiredMethod.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
