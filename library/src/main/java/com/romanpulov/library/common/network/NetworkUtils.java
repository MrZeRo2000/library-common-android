package com.romanpulov.library.common.network;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Network related utility routines
 * Created by romanpulov on 20.11.2017.
 */

public final class NetworkUtils {
    private static final int HOST_CHECK_TIMEOUT = 30;

    /**
     * Checks if network is available
     * @param context Context
     * @return flag if network is available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    /**
     * Checks if internet host is reachable
     * @param hostName host name to check
     * @return flag if host is reachable
     */
    public static boolean isInternetHostAvailable(String hostName) {
        try {
            return InetAddress.getByName(hostName).isReachable(HOST_CHECK_TIMEOUT);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
