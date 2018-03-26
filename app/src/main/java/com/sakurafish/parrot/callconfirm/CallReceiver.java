package com.sakurafish.parrot.callconfirm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.activity.ConfirmActivity;
import com.sakurafish.parrot.callconfirm.config.Config;

/**
 * 発信を捉える
 * Created by sakura on 2014/12/28.
 */
public class CallReceiver extends BroadcastReceiver {

    // パーミッションが許可されていないとこちらの処理まで来ない

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!Pref.getPrefBool(context, context.getString(R.string.PREF_CONFIRM), true)) {
            return;
        }
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_NEW_OUTGOING_CALL)) {
            if (Pref.getPrefBool(context, Config.PREF_AFTER_CONFIRM, false)) {
                Pref.setPref(context, Config.PREF_AFTER_CONFIRM, false);
                return;
            }
            String phoneNumber = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);
            // 発信を取り消す
            setResultData(null);
            //発信確認ダイアログを表示する
            context.startActivity(ConfirmActivity.createIntent(context, ConfirmActivity.class, phoneNumber));
        }
    }
}