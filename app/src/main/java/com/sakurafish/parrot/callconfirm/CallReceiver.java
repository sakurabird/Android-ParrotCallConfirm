package com.sakurafish.parrot.callconfirm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sakurafish.common.lib.LogUtils;

/**
 * Created by sakura on 2014/12/28.
 */
public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        LogUtils.logDebug("onReceive intent action:"+intent.getAction());
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String phoneNumber = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);
            LogUtils.logDebug("onReceive phoneNumber:"+phoneNumber);
            //発信確認ダイアログを表示する
        }
    }
}