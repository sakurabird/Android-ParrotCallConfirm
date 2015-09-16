package com.sakurafish.parrot.callconfirm.utils;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sakurafish.parrot.callconfirm.BuildConfig;
import com.sakurafish.parrot.callconfirm.MyApplication;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.ui.GeneralDialogFragment;

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
        Toast.makeText(MyApplication.getContext(), text,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Toastを表示する
     *
     * @param text
     */
    public static void showToast(Activity activity, String text) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.views_toast, null);
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

    public static void showDialog(final FragmentManager manager, final Fragment target, final String message, final String tag) {
        dismissDialog(manager, tag);
        GeneralDialogFragment.Builder builder = new GeneralDialogFragment.Builder();
        builder.setMessage(message);
        builder.setPositiveText("OK");
        builder.setTargetFragment(target, 12345);
        builder.create().show(manager, tag);
    }

    public static void dismissDialog(final FragmentManager manager, final String tag) {
        if (manager != null) {
            Fragment fragment = manager.findFragmentByTag(tag);
            if (fragment instanceof DialogFragment) {
                DialogFragment dialog = (DialogFragment) fragment;
                dialog.onDismiss(null);
            }
        }
    }
}
