package com.sakurafish.common.lib;

import android.os.Environment;
import android.util.Log;

/**
 * Created by sakura on 2014/12/28.
 */
public class EnvironmentUtils {

    public static boolean checkStorage() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        Log.d("", "mExternalStorageAvailable:" + mExternalStorageAvailable);
        return mExternalStorageWriteable;
    }

    private EnvironmentUtils(){}
}
