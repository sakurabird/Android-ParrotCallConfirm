package com.sakurafish.parrot.callconfirm.Pref;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.AudioManager;
import android.os.Build;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

import com.sakurafish.common.lib.pref.Pref;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.utils.Utils;

import static android.os.Build.VERSION.SDK_INT;

public class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener {

    public interface OnVolumeChangedListerner {
        void onChanged();
    }

    private Context mContext;
    private OnVolumeChangedListerner mListerner;
    private int mMaxValue = 100;
    private int mMinValue = 0;
    private int mInterval = 1;
    private int mCurrentValue;
    private SeekBar mSeekBar;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setLayoutResource(R.layout.views_seekbar);
        setCurrentVolume();
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setLayoutResource(R.layout.views_seekbar);
        setCurrentVolume();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        setLayoutResource(R.layout.views_seekbar);
        setCurrentVolume();
    }

    public void setOnVolumeChangedListerner(OnVolumeChangedListerner listerner) {
        mListerner = listerner;
    }

    public void removeOnVolumeChangedListerner() {
        mListerner = null;
    }

    private void setCurrentVolume() {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (Pref.isExistKey(mContext, mContext.getString(R.string.PREF_SOUND_VOLUME))) {
            mCurrentValue = Pref.getPrefInt(mContext, mContext.getString(R.string.PREF_SOUND_VOLUME));
        } else {
            mCurrentValue = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        mMaxValue = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Utils.logDebug("mCurrentValue:" + mCurrentValue + " mMaxValue:" + mMaxValue+" sysvol:"+am.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    @Override
    public void onBindView(@NonNull View view) {
        super.onBindView(view);

        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setOnSeekBarChangeListener(this);


        mSeekBar.setProgress(mCurrentValue - mMinValue);

        setSeekBarTintOnPreLollipop();

        if (!view.isEnabled()) {
            mSeekBar.setEnabled(false);
        }
    }

    public void setCurrentValue(int value) {
        mCurrentValue = value;
        super.persistInt(value);
        notifyChanged();
    }

    public int getCurrentValue() {
        return mCurrentValue;
    }

    static int pxFromDp(int dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    void setSeekBarTintOnPreLollipop() { //TMP: I hope google will introduce native seekbar tinting for appcompat users
        if (SDK_INT < 21) {
            Resources.Theme theme = getContext().getTheme();

            int attr = R.attr.colorAccent;
            int fallbackColor = Color.parseColor("#009688");
            int accent = theme.obtainStyledAttributes(new int[]{attr}).getColor(0, fallbackColor);

            ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
            thumb.setIntrinsicHeight(pxFromDp(15, getContext()));
            thumb.setIntrinsicWidth(pxFromDp(15, getContext()));
            thumb.setColorFilter(new PorterDuffColorFilter(accent, PorterDuff.Mode.SRC_ATOP));
            mSeekBar.setThumb(thumb);

            Drawable progress = mSeekBar.getProgressDrawable();
            progress.setColorFilter(new PorterDuffColorFilter(accent, PorterDuff.Mode.MULTIPLY));
            mSeekBar.setProgressDrawable(progress);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int newValue = progress + mMinValue;

        if (newValue > mMaxValue) newValue = mMaxValue;

        else if (newValue < mMinValue) newValue = mMinValue;

        else if (mInterval != 1 && newValue % mInterval != 0)
            newValue = Math.round(((float) newValue) / mInterval) * mInterval;

        // change rejected, revert to the previous value
        if (!callChangeListener(newValue)) {
            seekBar.setProgress(mCurrentValue - mMinValue);
            return;
        }

        // change accepted, store it
        mCurrentValue = newValue;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        notifyChanged();
        persistInt(mCurrentValue);
        if (mListerner != null) {
            mListerner.onChanged();
        }
    }
}
