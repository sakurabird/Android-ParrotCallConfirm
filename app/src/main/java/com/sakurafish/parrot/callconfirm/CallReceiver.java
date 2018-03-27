package com.sakurafish.parrot.callconfirm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.activity.ConfirmActivity;
import com.sakurafish.parrot.callconfirm.config.Config;

import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.PERMISSIONS;
import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.hasPermissions;

/**
 * 発信を捉える
 * Created by sakura on 2014/12/28.
 */
public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (!canProceedConfirm(context, intent)) return;

        String phoneNumber = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);
        // 発信を取り消す
        setResultData(null);
        //発信確認ダイアログを表示する
        context.startActivity(ConfirmActivity.createIntent(context, ConfirmActivity.class, phoneNumber));
    }

    private boolean canProceedConfirm(final Context context, final Intent intent) {
        if (!intent.getAction().equalsIgnoreCase(Intent.ACTION_NEW_OUTGOING_CALL)) {
            return false;
        }

        // 発信確認が無効の場合処理を行わない
        if (!Pref.getPrefBool(context, context.getString(R.string.PREF_CONFIRM), true)) {
            return false;
        }

        // 必要なパーミッションが許可されていない場合処理を行わない
        if (Build.VERSION.SDK_INT >= 23 && !hasPermissions(context, PERMISSIONS)) {
            return false;
        }

        // インコ発信確認アプリがユーザーから確認ダイアログで電話をかけるよう指示された後の発信の場合処理を行わない
        if (Pref.getPrefBool(context, Config.PREF_AFTER_CONFIRM, false)) {
            Pref.setPref(context, Config.PREF_AFTER_CONFIRM, false);
            return false;
        }
        return true;
    }
}