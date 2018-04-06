package com.sakurafish.parrot.callconfirm.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sakurafish.parrot.callconfirm.BuildConfig;
import com.sakurafish.parrot.callconfirm.MyApplication;
import com.sakurafish.parrot.callconfirm.R;

public final class Utils {

    @Deprecated
    private Utils() {
    }

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

    /**
     * Toastを表示する
     *
     * @param text
     */
    public static void showToast(String text) {
        Toast.makeText(MyApplication.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Toastを表示する
     *
     * @param text
     */
    public static void showToast(Activity activity, String text) {
        LayoutInflater inflater = activity.getLayoutInflater();

        final ViewGroup nullParent = null;
        View view = inflater.inflate(R.layout.views_toast, nullParent);

        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        int rand = (int) (Math.random() * 10);
        switch (rand) {
            case 0:
                imageView.setImageResource(R.drawable.inco1);
                break;
            case 1:
                imageView.setImageResource(R.drawable.inco2);
                break;
            case 2:
                imageView.setImageResource(R.drawable.inco3);
                break;
            case 3:
                imageView.setImageResource(R.drawable.inco4);
                break;
            case 4:
                imageView.setImageResource(R.drawable.inco5);
                break;
            case 5:
                imageView.setImageResource(R.drawable.inco6);
                break;
            case 6:
                imageView.setImageResource(R.drawable.inco7);
                break;
            case 7:
                imageView.setImageResource(R.drawable.inco8);
                break;
            case 8:
                imageView.setImageResource(R.drawable.inco9);
                break;
            case 9:
                imageView.setImageResource(R.drawable.inco10);
                break;
            default:
                imageView.setImageResource(R.drawable.inco9);
                break;
        }

        TextView textView = (TextView) view.findViewById(R.id.message);
        textView.setText(text);
        Toast toast = new Toast(activity);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);
        toast.show();
    }

    /**
     * 環境が日本語ならtrue
     */
    public static boolean isJapan() {
        String locale = MyApplication.getContext().getResources().getConfiguration().locale.getLanguage();
        Utils.logDebug("locale:" + locale);
        return locale.equals("ja");
    }

    /**
     * version codeを取得する
     */
    public static int getVersionCode() {
        Context context = MyApplication.getContext();
        PackageManager manager = context.getPackageManager();
        PackageInfo info;
        int versionCode;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
        return versionCode;
    }

    /**
     * version nameを取得する
     */
    public static String getVersionName() {
        Context context = MyApplication.getContext();
        PackageManager manager = context.getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * デバイスの名前を取得する
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
