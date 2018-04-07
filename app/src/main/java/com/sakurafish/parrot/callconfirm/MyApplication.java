package com.sakurafish.parrot.callconfirm;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;
import com.sakurafish.parrot.callconfirm.utils.SoundManager;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyApplication extends Application {

    private static MyApplication application;
    private SoundManager soundManager;

    public MyApplication() {
        super();
        application = this;
    }

    public static MyApplication getInstance() {
        return application;
    }

    public static Context getContext() {
        return application.getApplicationContext();
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/GenShinGothic-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        // setup Sounds
        soundManager = SoundManager.getInstance(getContext());

        // adMob
        MobileAds.initialize(this, getString(R.string.banner_ad_unit_id));
    }
}
