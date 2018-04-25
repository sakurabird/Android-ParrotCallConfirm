package com.sakurafish.parrot.callconfirm.Pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.sakurafish.parrot.callconfirm.R;

public class PrefSounds extends Preference implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Context context;
    private TextView summary;

    public PrefSounds(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onBindView(final View view) {
        TextView text = (TextView) view.findViewById(R.id.text);
        this.summary = (TextView) view.findViewById(R.id.summary);

        text.setText(R.string.setting_sound1);

        String prefSoundNumber = Pref.getPrefString(context, context.getString(R.string.PREF_SOUND));
        if (TextUtils.isEmpty(prefSoundNumber)) {
            Pref.setPref(context, context.getString(R.string.PREF_SOUND), "0");
            prefSoundNumber = "0";
        }
        this.summary.setText(getSummaryString(prefSoundNumber));

        super.onBindView(view);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!key.equals(context.getString(R.string.PREF_SOUND))) return;
        String prefSoundNumber = Pref.getPrefString(context, context.getString(R.string.PREF_SOUND));
        this.summary.setText(getSummaryString(prefSoundNumber));
    }

    private String getSummaryString(@NonNull final String prefSoundNumber) {
        String[] array = context.getResources().getStringArray(R.array.setting_sound);
        int index = Integer.parseInt(prefSoundNumber);
        return array[index];
    }
}
