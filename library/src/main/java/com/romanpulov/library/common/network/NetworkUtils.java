package com.romanpulov.library.common.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Network related utility routines
 * Created by romanpulov on 20.11.2017.
 */

public final class NetworkUtils {
    private static final int HOST_CHECK_TIMEOUT = 30;
    private static final int CAPABILITIES_CHANGE_DELAY = 5000;

    public interface OnInternetAvailableListener {
        void onInternetAvailable (@NonNull NetworkCapabilities networkCapabilities);
    }

    public static class ConnectivityMonitor extends ConnectivityManager.NetworkCallback {
        long mLastAvailableTime;

        OnInternetAvailableListener mListener;

        public void registerOnInternetAvailableListener(OnInternetAvailableListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            mLastAvailableTime = System.currentTimeMillis();
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            long currentTime = System.currentTimeMillis();

            if (mListener != null &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_ROAMING) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                    mLastAvailableTime > 0 &&
                    (currentTime - mLastAvailableTime) < CAPABILITIES_CHANGE_DELAY
            ) {
                mListener.onInternetAvailable(networkCapabilities);
            }
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            mLastAvailableTime = 0;
        }
    }

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
