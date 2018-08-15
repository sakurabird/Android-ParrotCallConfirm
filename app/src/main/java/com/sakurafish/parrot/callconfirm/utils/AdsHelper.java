package com.sakurafish.parrot.callconfirm.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.config.Config;

public class AdsHelper {

    private static final String TAG = AdsHelper.class.getSimpleName();
    // Intent action filter string
    public static final String ACTION_BANNER_CLICK = "ACTION_BANNER_CLICK";
    // Intent extras key string
    public static final String INTENT_EXTRAS_KEY_CLASS = "INTENT_EXTRAS_KEY_CLASS";
    // click interval time millisecond
    public static final long CLICK_DELAY_MILLIS = 1000 * 60 * 60; // 60 min

    private final Context context;
    private final Handler handler = new Handler();
    private Runnable runnable;

    public AdsHelper(Context context) {
        this.context = context;
    }

    public boolean isIntervalOK() {
        // Need 60 min Interval
        long now = System.currentTimeMillis();
        long lastClickTime = getLastClickTimeMillis();
        if (lastClickTime == 0) return true;
        long passedTime = now - lastClickTime;
        return passedTime >= CLICK_DELAY_MILLIS;
    }

    public void startLoad(@NonNull final AdView adView) {
        adView.loadAd(getAdRequest());
    }

    public void startInterval(final boolean clicked, @NonNull final String className, @NonNull final AdView adView) {
        adView.setVisibility(View.GONE);
        long now = System.currentTimeMillis();

        runnable = () -> {
            adView.setVisibility(View.VISIBLE);
            adView.loadAd(getAdRequest());
        };

        if (clicked) {
            setLastClickTimeMillis(now);
            Intent localIntent = new Intent(ACTION_BANNER_CLICK);
            localIntent.putExtra(INTENT_EXTRAS_KEY_CLASS, className);
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        }
        long intervalTimeMillis = getIntervalTimeMillis(now, getLastClickTimeMillis());

        handler.postDelayed(runnable, intervalTimeMillis);
    }

    public void finishInterval() {
        handler.removeCallbacks(runnable);
    }

    private AdRequest getAdRequest() {
        String[] ids = context.getResources().getStringArray(R.array.admob_test_device);
        AdRequest.Builder builder = new AdRequest.Builder();
        for (String id : ids) {
            builder.addTestDevice(id);
        }
        return builder.build();
    }

    private long getLastClickTimeMillis() {
        return Pref.getPrefLong(context, Config.PREF_LAST_AD_CLICK_TIME, 0);
    }

    private void setLastClickTimeMillis(final long date) {
        Pref.setPrefLong(context, Config.PREF_LAST_AD_CLICK_TIME, date);
    }

    private long getIntervalTimeMillis(final long now, final long lastClickTimeMillis) {
        long intervalTimeMillis = CLICK_DELAY_MILLIS;
        if (lastClickTimeMillis != 0) {
            long passedTimeMillis = now - lastClickTimeMillis;
            intervalTimeMillis = CLICK_DELAY_MILLIS - passedTimeMillis;
            if (intervalTimeMillis > CLICK_DELAY_MILLIS) {
                intervalTimeMillis = CLICK_DELAY_MILLIS;
            }
        }
        return intervalTimeMillis;
    }
}
