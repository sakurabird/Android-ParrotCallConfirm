package com.sakurafish.parrot.callconfirm.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.android.gms.ads.AdRequest;
import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.config.Config;
import com.sakurafish.parrot.callconfirm.databinding.ActivityMainBinding;
import com.sakurafish.parrot.callconfirm.fragment.SelectSoundFragment;
import com.sakurafish.parrot.callconfirm.utils.AdsHelper;
import com.sakurafish.parrot.callconfirm.utils.CallConfirmUtils;

/**
 * 設定画面
 * Created by sakura on 2015/03/24.
 */
public class SettingActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Fragment mContent;

    public static Intent createIntent(@NonNull final Context context, @NonNull final Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (savedInstanceState == null) {
            switchContent(new MyPreferenceFragment());
        } else {
            switchContent(getFragmentManager().getFragment(savedInstanceState, "mContent"));
        }

        initLayout();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        if (getFragmentManager().findFragmentByTag("mContent") != null) {
            getFragmentManager().putFragment(outState, "mContent", mContent);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() <= 1) {
            SettingActivity.this.finish();
            return;
        }
        super.onBackPressed();
    }

    private void initLayout() {
        // show AD banner
        AdRequest adRequest = new AdsHelper(this).getAdRequest();
        binding.adView.loadAd(adRequest);
    }

    public void switchContent(Fragment fragment) {
        mContent = fragment;
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in, R.animator.slide_out)
                .replace(R.id.content, mContent)
                .addToBackStack(null).commit();
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

            // 音声の選択は別画面で行う
            Preference sound = findPreference(getString(R.string.PREF_SOUND));
            sound.setOnPreferenceClickListener(preference -> {
                if (!(getActivity() instanceof SettingActivity)) {
                    return false;
                }
                SettingActivity activity = (SettingActivity) getActivity();
                activity.switchContent(SelectSoundFragment.newInstance());
                return false;
            });
        }
    }
}
