package com.sakurafish.parrot.callconfirm.utils;

import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.NonNull;

import com.sakurafish.parrot.callconfirm.MyApplication;
import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.config.Config;

import static com.sakurafish.parrot.callconfirm.config.Config.VIBRATOR_NOT_REPEAT;
import static com.sakurafish.parrot.callconfirm.config.Config.VIBRATOR_PATTERN0;
import static com.sakurafish.parrot.callconfirm.config.Config.VIBRATOR_PATTERN1;
import static com.sakurafish.parrot.callconfirm.config.Config.VIBRATOR_PATTERN2;
import static com.sakurafish.parrot.callconfirm.config.Config.VIBRATOR_PATTERN3;
import static com.sakurafish.parrot.callconfirm.config.Config.VIBRATOR_PATTERN4;

/**
 * Created by sakura on 9/16/15.
 */
public final class CallConfirmUtils {

    public static void vibrate(@NonNull final Context context) {
        String prefPattern = Pref.getPrefString(context, context.getString(R.string.PREF_VIBRATE_PATTERN), "0");
        vibrate(context, Integer.parseInt(prefPattern));
    }

    public static void vibrate(@NonNull final Context context, final int patternNo) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        switch (patternNo) {
            case 0:
                vibrator.vibrate(VIBRATOR_PATTERN0, VIBRATOR_NOT_REPEAT);
                break;
            case 1:
                vibrator.vibrate(VIBRATOR_PATTERN1, VIBRATOR_NOT_REPEAT);
                break;
            case 2:
                vibrator.vibrate(VIBRATOR_PATTERN2, VIBRATOR_NOT_REPEAT);
                break;
            case 3:
                vibrator.vibrate(VIBRATOR_PATTERN3, VIBRATOR_NOT_REPEAT);
                break;
            case 4:
                vibrator.vibrate(VIBRATOR_PATTERN4, VIBRATOR_NOT_REPEAT);
                break;
            default:
                vibrator.vibrate(VIBRATOR_PATTERN0, VIBRATOR_NOT_REPEAT);
        }
    }

    public static int playSound(@NonNull final Context context) {
        String s = Pref.getPrefString(context, context.getString(R.string.PREF_SOUND), "0");
        int idx = 0;
        try {
            idx = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            Utils.logError(e.getLocalizedMessage());
        }
        return MyApplication.getInstance().getSoundManager().play(Config.SOUND_IDS[idx]);
    }

    public static void stopSound(int soundID) {
        MyApplication.getInstance().getSoundManager().stop(soundID);
    }

    @Deprecated
    private CallConfirmUtils() {
    }
}
