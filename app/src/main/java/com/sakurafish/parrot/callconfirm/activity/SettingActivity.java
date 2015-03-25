package com.sakurafish.parrot.callconfirm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;

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
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
