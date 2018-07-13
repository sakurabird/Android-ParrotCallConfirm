package com.sakurafish.parrot.callconfirm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.config.Config;

import static com.sakurafish.parrot.callconfirm.config.Config.PREF_STATE_INVALID_TELNO;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if ((intent == null) || !intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            return;
        }

        Pref.setPref(context, Config.PREF_AFTER_CONFIRM, false);
        Pref.setPref(context, PREF_STATE_INVALID_TELNO, false);
    }
}