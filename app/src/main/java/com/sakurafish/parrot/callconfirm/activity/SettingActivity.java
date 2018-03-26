package com.sakurafish.parrot.callconfirm.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.sakurafish.parrot.callconfirm.config.Config;
import com.sakurafish.parrot.callconfirm.MyApplication;
import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.Pref.SoundSeekBarPreference;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.utils.CallConfirmUtils;

/**
 * 設定画面
 * Created by sakura on 2015/03/24.
 */
public class SettingActivity extends AppCompatActivity {

    public static Intent createIntent(@NonNull final Context context, @NonNull final Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction().replace(R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        private Context mContext;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            init();
            initLayout();
        }

        private void init() {
            mContext = getActivity();
            if (TextUtils.isEmpty(Pref.getPrefString(mContext, getString(R.string.PREF_VIBRATE_PATTERN)))) {
                Pref.setPref(mContext, getString(R.string.PREF_VIBRATE_PATTERN), "0");
            }
            if (TextUtils.isEmpty(Pref.getPrefString(mContext, getString(R.string.PREF_SOUND)))) {
                Pref.setPref(mContext, getString(R.string.PREF_SOUND), "0");
            }
            addPreferencesFromResource(R.xml.preferences);
        }

        private void initLayout() {
            final ListPreference vibrationlist = (ListPreference) findPreference(Config.PREF_VIBRATE_PATTERN);
            vibrationlist.setOnPreferenceChangeListener((preference, newValue) -> {

                int index = vibrationlist.findIndexOfValue(newValue.toString());
                if (index != -1) {
                    //バイブレート
                    CallConfirmUtils.vibrate(mContext, index);
                    String[] array = getResources().getStringArray(R.array.setting_vibrate);
                    vibrationlist.setSummary(array[index]);
                }
                return true;
            });

            final ListPreference list = (ListPreference) findPreference(Config.PREF_SOUND);
            list.setOnPreferenceChangeListener((preference, newValue) -> {

                int index = list.findIndexOfValue(newValue.toString());
                if (index != -1) {
                    //音を鳴らす
                    MyApplication.getSoundManager().play(MyApplication.getSoundIds()[index]);
                    String[] array = getResources().getStringArray(R.array.setting_sound);
                    list.setSummary(array[index]);
                }
                return true;
            });

            final SoundSeekBarPreference volume = (SoundSeekBarPreference) findPreference(getString(R.string.PREF_SOUND_VOLUME));
            volume.setOnVolumeChangedListerner(() -> {
                int index = Integer.parseInt(Pref.getPrefString(mContext, mContext.getString(R.string.PREF_SOUND)));
                int voiceIdx = MyApplication.getSoundIds()[index];

                MyApplication.getSoundManager().stop(voiceIdx);

                AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                int newVolume = Pref.getPrefInt(mContext, mContext.getString(R.string.PREF_SOUND_VOLUME));
                if (am != null) {
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
                }

                MyApplication.getSoundManager().play(voiceIdx);
            });
        }
    }
}
