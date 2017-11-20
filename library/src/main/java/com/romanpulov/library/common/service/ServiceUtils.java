package com.romanpulov.library.common.service;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Service utils class
 * Created by romanpulov on 17.11.2017.
 */

public class ServiceUtils {
    public static boolean isServiceRunning(Context context, String serviceClassName) {
        final ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

            for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
                if ((runningServiceInfo.service.getClassName().equals(serviceClassName)) && (runningServiceInfo.started)) {
                    return true;
                }
            }
        }

        return false;
    }
}
