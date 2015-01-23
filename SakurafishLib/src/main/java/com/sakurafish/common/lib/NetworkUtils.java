package com.sakurafish.common.lib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by sakura on 2014/12/28.
 */
public class NetworkUtils {

    /**
     * ネットワーク接続チェック
     *
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            return cm.getActiveNetworkInfo().isConnected();
        }
        return false;
    }
}
