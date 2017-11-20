package com.romanpulov.library.common.loader.core;

import android.content.Context;

import java.lang.reflect.Constructor;

/**
 * Factory for DocumentLoader creation
 * Created by romanpulov on 16.06.2016.
 */
public class LoaderFactory {

    public static Loader fromClassName(Context context, String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        Constructor<?> contextConstructor;
        try {
            contextConstructor = clazz.getConstructor(Context.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        try {
            return (Loader) contextConstructor.newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
