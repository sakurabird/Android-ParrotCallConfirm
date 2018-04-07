package com.sakurafish.parrot.callconfirm.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.NonNull;
import android.util.SparseIntArray;

import com.sakurafish.parrot.callconfirm.MyApplication;
import com.sakurafish.parrot.callconfirm.config.Config;

public class SoundManager {

    private static SoundManager soundManager = null;
    private SoundPool soundPool;
    private SparseIntArray soundPoolMap = new SparseIntArray();

    private static float rate = 1.0f;
    private static float masterVolume = 1.0f;
    private static float leftVolume = 1.0f;
    private static float rightVolume = 1.0f;
    private static float balance = 0.5f;

    private SoundManager(@NonNull final Context context) {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        // Initialize Sounds
        for (int soundId : Config.SOUND_IDS) {
            soundPoolMap.put(soundId, soundPool.load(context, soundId, 1));
        }
    }

    @NonNull
    public static SoundManager getInstance(@NonNull final Context context) {
        if (soundManager == null) {
            soundManager = new SoundManager(context);
        }
        return soundManager;
    }

    public int play(final int soundID) {
        boolean hasSound = soundPoolMap.indexOfKey(soundID) >= 0;
        if (!hasSound) {
            return 0;
        }

        final int id = soundPool.play(soundPoolMap.get(soundID), leftVolume, rightVolume, 1, 0, rate);
        return id;
    }

    public void stop(final int soundID) {
        soundPool.stop(soundID);
    }

    public void setVolume(final float vol) {
        masterVolume = vol;

        if (balance < 1.0f) {
            leftVolume = masterVolume;
            rightVolume = masterVolume * balance;
        } else {
            rightVolume = masterVolume;
            leftVolume = masterVolume * (2.0f - balance);
        }
    }

    public void setSpeed(final float speed) {
        rate = speed;

        if (rate < 0.01f)
            rate = 0.01f;

        if (rate > 2.0f)
            rate = 2.0f;
    }

    public void setBalance(final float balVal) {
        balance = balVal;
        setVolume(masterVolume);
    }

    public static int getStreamVolume() {
        AudioManager am = (AudioManager) MyApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        return am.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void unloadAll() {
        soundPool.release();
    }
}
