package com.sakurafish.parrot.callconfirm.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.sakurafish.parrot.callconfirm.MyApplication;
import com.sakurafish.parrot.callconfirm.R;

/**
 * Created by sakura on 9/16/15.
 */
public final class CallConfirmUtils {

    /**
     * 通知エリアに即時通知
     * @param cls
     * @param message
     */
    public static void setNotification(@NonNull final Class<?> cls, @NonNull final String message) {
        final NotificationManager notificationManager = (NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        final Intent intent = new Intent(MyApplication.getContext(), cls);
        final PendingIntent contentIntent = PendingIntent.getActivity(MyApplication.getContext(), 0, intent, 0);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(MyApplication.getContext())
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(MyApplication.getContext().getResources().getString(R.string.app_name))
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(true)
                .setContentIntent(contentIntent);

        notificationManager.cancel(R.string.app_name);
        notificationManager.notify(R.string.app_name, builder.build());
    }

    @Deprecated
    private CallConfirmUtils() {
    }
}
