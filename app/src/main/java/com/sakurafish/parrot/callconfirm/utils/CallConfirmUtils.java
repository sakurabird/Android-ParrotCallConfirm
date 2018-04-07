package com.sakurafish.parrot.callconfirm.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.sakurafish.parrot.callconfirm.MyApplication;
import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.R;

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

    /**
     * 通知エリアに即時通知
     *
     * @param cls
     * @param message
     */
    public static void setNotification(@NonNull final Class<?> cls, @NonNull final String message) {

        final String appName = MyApplication.getContext().getResources().getString(R.string.app_name);

        final NotificationManager notificationManager =
                (NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        final Intent intent = new Intent(MyApplication.getContext(), cls);
        final PendingIntent contentIntent = PendingIntent.getActivity(MyApplication.getContext(), 0, intent, 0);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(MyApplication.getContext())
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(appName)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setTicker(appName)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(true)
                .setContentIntent(contentIntent);

        notificationManager.cancel(R.string.app_name);
        notificationManager.notify(R.string.app_name, builder.build());
    }

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

    public static void playSound(@NonNull final Context context) {
        String s = Pref.getPrefString(context, context.getString(R.string.PREF_SOUND), "0");
        int idx = 0;
        try {
            idx = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            Utils.logError(e.getLocalizedMessage());
        }
        MyApplication.getSoundManager().play(MyApplication.getSoundIds()[idx]);
    }

    @Deprecated
    private CallConfirmUtils() {
    }
}
