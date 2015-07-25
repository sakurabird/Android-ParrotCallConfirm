package com.sakurafish.parrot.callconfirm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sakurafish.common.lib.pref.Pref;
import com.sakurafish.parrot.callconfirm.Config;
import com.sakurafish.parrot.callconfirm.MyApplication;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.utils.ContactUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 確認ダイアログ表示
 * Created by sakura on 2015/01/23.
 */
public class ConfirmActivity extends Activity {
    private Context mContext;
    private String mPhoneNumber;

    public static Intent createIntent(@NonNull final Context context, @NonNull final Class clazz, @NonNull final String phoneNumber) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString(Config.INTENT_EXTRAS_PHONENUMBER, phoneNumber);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_confirm);
        ButterKnife.bind(this);

        mContext = this;
        mPhoneNumber = getIntent().getStringExtra(Config.INTENT_EXTRAS_PHONENUMBER);
        if (mPhoneNumber == null) {
            throw new IllegalStateException();
        }

        initLayout();
    }

    private void initLayout() {
        if (Pref.getPrefBool(mContext, getString(R.string.PREF_VIBRATE), true)) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
        }
        if (Pref.getPrefBool(mContext, getString(R.string.PREF_SOUND_ON), true)) {
            MyApplication.getSoundManager().play(MyApplication.getSoundIds()[0]);
        }

        ((TextView) findViewById(R.id.textView_telno)).setText(mPhoneNumber);
        ContactUtils.ContactInfo info = ContactUtils.getContactInfoByNumber(mPhoneNumber);
        if (info != null) {
            ((TextView) findViewById(R.id.textView_name)).setText(info.getName());
            if (info.getPhotoBitmap() != null)
                ((ImageView) findViewById(R.id.imageView_callto)).setImageBitmap(info.getPhotoBitmap());
        }

        ImageView inco =(ImageView)findViewById(R.id.imageView_inco);
        Animation animation= AnimationUtils.loadAnimation(mContext, R.anim.inco_jump);
        inco.startAnimation(animation);
    }

    @OnClick(R.id.button_ok)
    void call() {
        Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, true);
        String number = "tel:" + mPhoneNumber.trim();
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number)));
        ConfirmActivity.this.finish();
    }

    @OnClick(R.id.button_no)
    void cancel() {
        Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, false);
        ConfirmActivity.this.finish();
    }

    @OnClick(R.id.imageView_setting)
    void setting() {
        Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, false);
        startActivity(MainActivity.createIntent(mContext, MainActivity.class));
        ConfirmActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, false);
        ConfirmActivity.this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mContext = null;
        mPhoneNumber = null;
    }

}
