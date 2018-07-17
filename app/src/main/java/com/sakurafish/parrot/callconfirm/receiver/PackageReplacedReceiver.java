package com.sakurafish.parrot.callconfirm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.config.Config;
import com.sakurafish.parrot.callconfirm.utils.AlarmUtils;

public class PackageReplacedReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        if ((intent == null) || !intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            return;
        }

        Pref.setPref(context, Config.PREF_AFTER_CONFIRM, false);

        if (Pref.getPrefBool(context, context.getString(R.string.PREF_NOTIFICATION), true)) {
            AlarmUtils.unregisterAlarm(context);
            AlarmUtils.registerAlarm(context);
        }
    }
}
