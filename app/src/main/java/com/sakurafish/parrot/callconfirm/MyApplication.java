package com.sakurafish.parrot.callconfirm;

import android.app.Application;
import android.content.Context;

import com.sakurafish.common.lib.pref.Pref;
import com.sakurafish.parrot.callconfirm.utils.SoundManager;
import tokyo.suru_pass.AdContext;

public class MyApplication extends Application {

    private static MyApplication application;
    private static SoundManager sSoundManager;
    private static int[] sSoundIds = new int[1];
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

        int launchCount = Pref.getPrefInt(getApplicationContext(), Config.PREF_LAUNCH_COUNT);
        Pref.setPref(getApplicationContext(), Config.PREF_LAUNCH_COUNT, ++launchCount);

        setupSounds();
        setupADs();
    }

    private void setupSounds() {
        sSoundManager = SoundManager.getInstance(getContext());
        sSoundIds[0] = sSoundManager.load(R.raw.inco1);
    }
    /**
     * 広告SDKのインスタンスを取得
     */
    private void setupADs() {
        //Suru Pass
        sAdContext = new AdContext(getContext(), getString(R.string.surupass_mediaId), BuildConfig.DEBUG);
    }
}
