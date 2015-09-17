package com.sakurafish.parrot.callconfirm;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.sakurafish.common.lib.pref.Pref;
import com.sakurafish.parrot.callconfirm.utils.SoundManager;

import io.fabric.sdk.android.Fabric;
import tokyo.suru_pass.AdContext;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyApplication extends Application {

    private static MyApplication application;
    private static SoundManager sSoundManager;
    private static int[] sSoundIds = new int[5];
    private static AdContext sAdContext;//suru pass

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

    public static AdContext getAdContext() {
        return sAdContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        int launchCount = Pref.getPrefInt(getApplicationContext(), Config.PREF_LAUNCH_COUNT);
        Pref.setPref(getApplicationContext(), Config.PREF_LAUNCH_COUNT, ++launchCount);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/GenShinGothic-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setupSounds();
        setupADs();
    }

    private void setupSounds() {
        sSoundManager = SoundManager.getInstance(getContext());
        sSoundIds[0] = sSoundManager.load(R.raw.inco1);
        sSoundIds[1] = sSoundManager.load(R.raw.inco2);
        sSoundIds[2] = sSoundManager.load(R.raw.inco3);
        sSoundIds[3] = sSoundManager.load(R.raw.inco4);
        sSoundIds[4] = sSoundManager.load(R.raw.inco5);
    }

    /**
     * 広告SDKのインスタンスを取得
     */
    private void setupADs() {
        //Suru Pass
        sAdContext = new AdContext(getContext(), getString(R.string.surupass_mediaId), BuildConfig.DEBUG);
    }
}
