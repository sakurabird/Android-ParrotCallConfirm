package com.sakurafish.parrot.callconfirm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

import com.sakurafish.common.lib.pref.Pref;
import com.sakurafish.parrot.callconfirm.Config;
import com.sakurafish.parrot.callconfirm.R;

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

        mContext = this;
        mPhoneNumber = getIntent().getStringExtra(Config.INTENT_EXTRAS_PHONENUMBER);
        if (mPhoneNumber==null){
            throw new IllegalStateException();
        }

        initLayout();
    }

    private void initLayout() {
        findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call実行
                call();
            }
        });

        findViewById(R.id.button_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, false);
                ConfirmActivity.this.finish();
            }
        });

        findViewById(R.id.imageView_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, false);
                startActivity(MainActivity.createIntent(mContext, MainActivity.class));
                ConfirmActivity.this.finish();
            }
        });
    }

    private void call() {
        Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, true);
        String number = "tel:" + mPhoneNumber.trim();
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number)));
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
