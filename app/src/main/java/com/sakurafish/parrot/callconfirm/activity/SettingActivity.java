package com.sakurafish.parrot.callconfirm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import com.sakurafish.common.lib.pref.Pref;
import com.sakurafish.parrot.callconfirm.Config;
import com.sakurafish.parrot.callconfirm.MyApplication;
import com.sakurafish.parrot.callconfirm.R;

/**
 * 設定画面
 * Created by sakura on 2015/03/24.
 */
public class SettingActivity extends ActionBarActivity {

    public static Intent createIntent(@NonNull final Context context, @NonNull final Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            if (TextUtils.isEmpty(Pref.getPrefString(mContext, getString(R.string.PREF_SOUND)))) {
                Pref.setPref(mContext, getString(R.string.PREF_SOUND), "0");
            }
            addPreferencesFromResource(R.xml.preferences);
        }

        private void initLayout() {
            final ListPreference list = (ListPreference) findPreference(Config.PREF_SOUND);
            list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    int index = list.findIndexOfValue(newValue.toString());
                    if (index != -1) {
                        //音を鳴らす
                        MyApplication.getSoundManager().play(MyApplication.getSoundIds()[index]);
                        String[] array = getResources().getStringArray(R.array.setting_sound);
                        list.setSummary(array[index]);
                    }
                    return true;
                }
            });
        }
    }
}
