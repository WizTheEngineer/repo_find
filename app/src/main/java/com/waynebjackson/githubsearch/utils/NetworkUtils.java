package com.waynebjackson.githubsearch.utils;

import com.google.common.base.Preconditions;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

/**
 * Created by wayne.jackson on 3/7/17.
 */

public class NetworkUtils {

    public static boolean isConnected(@NonNull Context context) {
        Preconditions.checkNotNull(context);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
