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
    private static SoundManager sSoundManager;
    private static int[] sSoundIds = new int[5];

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

    public static SoundManager getSoundManager() {
        return sSoundManager;
    }

    public static int[] getSoundIds() {
        return sSoundIds;
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
        setupSounds();

        // adMob
        MobileAds.initialize(this, getString(R.string.banner_ad_unit_id));
    }

    private void setupSounds() {
        sSoundManager = SoundManager.getInstance(getContext());
        sSoundIds[0] = sSoundManager.load(R.raw.inco1);
        sSoundIds[1] = sSoundManager.load(R.raw.inco2);
        sSoundIds[2] = sSoundManager.load(R.raw.inco3);
        sSoundIds[3] = sSoundManager.load(R.raw.inco4);
        sSoundIds[4] = sSoundManager.load(R.raw.inco5);
    }
}
