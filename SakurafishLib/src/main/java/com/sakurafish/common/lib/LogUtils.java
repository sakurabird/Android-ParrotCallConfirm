package com.sakurafish.common.lib;

import android.util.Log;

/**
 * Created by sakura on 2014/12/28.
 */
public class LogUtils {

    /**
     * デバッグ用ログ出力：出所が分かるようにメソッドの出力位置を表示する
     *
     * @param message 出力するメッセージ文字列
     */
    public static void logDebug(String message) {
        if (BuildConfig.DEBUG) {

            String prefLabel = new Throwable().getStackTrace()[1].toString();
            Log.d("logDebug", prefLabel + ": " + message);
        }
    }

    /**
     * ログ出力：出所が分かるようにメソッドの出力位置を表示する
     *
     * @param message 出力するメッセージ文字列
     */
    public static void logError(String message) {
        String prefLabel = new Throwable().getStackTrace()[1].toString();
        Log.e("logError", prefLabel + ": " + message);
    }

    private LogUtils(){}
}
