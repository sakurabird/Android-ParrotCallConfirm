package com.sakurafish.common.lib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by sakura on 2014/12/28.
 */
public class PackageUtils {

    /**
     * パッケージ存在チェック
     *
     * @return
     */
    public static boolean isPackageExist(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    /**
     * version nameを取得する
     *
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        String version = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return version;
    }

    private PackageUtils(){}
}
