package com.sakurafish.parrot.callconfirm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.config.Config;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (null != intent && "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Pref.setPref(context, Config.PREF_AFTER_CONFIRM, false);
        }
    }
}