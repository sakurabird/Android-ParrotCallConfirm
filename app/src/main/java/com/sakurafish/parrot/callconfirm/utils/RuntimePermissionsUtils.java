package com.sakurafish.parrot.callconfirm.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.sakurafish.parrot.callconfirm.R;

public final class RuntimePermissionsUtils {

    // この中でREAD_CONTACTSは必須ではない(単にかける相手を不明と表示する)
    public static final String[] PERMISSIONS = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS
    };

    public static final String[] PERMISSIONS_MUST = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.PROCESS_OUTGOING_CALLS
    };

    public static final int PERMISSIONS_REQUESTS = 10;

    /**
     * パラメータで渡されたパーミッションをユーザーが許可しているか判定する<br>
     * true : 許可している<br>
     * false : 許可していない<br>
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean hasPermission(@NonNull final Context context, @NonNull final String permission) {
        if ((ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }
        return false;
    }

    /**
     * パラメータで渡されたパーミッション全てをユーザーが許可しているか判定する<br>
     * true : 許可している<br>
     * false : 許可していない<br>
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermissions(@NonNull final Context context, @NonNull final String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * パラメータで渡されたパーミッションがshouldShowRequestPermissionRationaleかどうかを判定する<br>
     * true : shouldShowRequestPermissionRationaleの状態である<br>
     * false : shouldShowRequestPermissionRationaleの状態でない<br>
     *
     * @param activity
     * @param permission
     * @return
     */
    public static boolean shouldShowRational(@NonNull final Activity activity, @NonNull final String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            return true;
        }
        return false;
    }

    /**
     * 全てのパーミッションのうちshouldShowRequestPermissionRationaleのものがあるかどうかを判定する<br>
     * true : shouldShowRequestPermissionRationaleの状態が存在する<br>
     * false : shouldShowRequestPermissionRationaleの状態が存在しない<br>
     *
     * @param activity
     * @return
     */
    public static boolean shouldShowRational(@NonNull final Activity activity) {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Rational Dialogを表示する。<br>
     * ok : パーミッションをリクエストする<br>
     * cancel : 何もしない<br>
     *
     * @param activity
     * @param context
     */
    public static void showRationaleDialog(@NonNull final Activity activity,
                                           @NonNull final Context context) {
        new MaterialDialog.Builder(context)
                .theme(Theme.LIGHT)
                .title(context.getString(R.string.message_request_permissions1))
                .content(context.getString(R.string.message_request_permissions2))
                .positiveText(context.getString(android.R.string.ok))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSIONS_REQUESTS);
                    }
                })
                .negativeText(android.R.string.cancel)
                .show();
    }

    /**
     * ユーザーにパーミッションを要求したがNever Ask Againをチェックした状態で拒否された場合にアプリの設定画面に誘導するダイアログを表示する
     *
     * @param context
     */
    public static void onNeverAskAgainSelected(@NonNull final Context context) {
        new MaterialDialog.Builder(context)
                .theme(Theme.LIGHT)
                .title(context.getString(R.string.message_request_permissions1))
                .content(context.getString(R.string.message_request_permissions3))
                .positiveText(context.getString(android.R.string.ok))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // open app settings
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                })
                .negativeText(android.R.string.cancel)
                .show();
    }
}
