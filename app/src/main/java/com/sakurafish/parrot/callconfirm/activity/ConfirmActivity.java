package com.sakurafish.parrot.callconfirm.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.config.Config;
import com.sakurafish.parrot.callconfirm.databinding.ActivityConfirmBinding;
import com.sakurafish.parrot.callconfirm.utils.CallConfirmUtils;
import com.sakurafish.parrot.callconfirm.utils.ContactUtils;
import com.sakurafish.parrot.callconfirm.utils.Utils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.sakurafish.parrot.callconfirm.config.Config.PREF_STATE_INVALID_TELNO;
import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.PERMISSIONS;
import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.PERMISSIONS_REQUESTS;
import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.hasPermissions;
import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.onNeverAskAgainSelected;
import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.shouldShowRational;
import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.showRationaleDialog;

/**
 * 確認ダイアログ表示
 * Created by sakura on 2015/01/23.
 */
public class ConfirmActivity extends AppCompatActivity {
    private ActivityConfirmBinding binding;
    private Context mContext;
    private String mPhoneNumber;

    private BroadcastReceiver mScreenOffReceiver;
    private BroadcastReceiver mHomeKeyReceiver;

    private boolean shouldShowPermissionsDialog = true;
    private boolean shouldShowRationalDialog = true;

    private int soundID;

    public static Intent createIntent(@NonNull final Context context,
                                      @NonNull final Class clazz,
                                      @NonNull final String phoneNumber) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        Bundle bundle = new Bundle();
        bundle.putString(Config.INTENT_EXTRAS_PHONENUMBER, phoneNumber);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirm);

        init();
        initLayout();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // インコ音を再生する
        if (Pref.getPrefBool(mContext, getString(R.string.PREF_SOUND_ON), true)) {
            new Handler().postDelayed(() -> {
                if (!isFinishing() && !isDestroyed() && mContext != null) {
                    soundID = CallConfirmUtils.playSound(mContext);
                }
            }, 1000);
        }

        // 端末をバイブレートさせる
        if (Pref.getPrefBool(mContext, getString(R.string.PREF_VIBRATE), true)) {
            CallConfirmUtils.vibrate(mContext);
        }

    }

    private void init() {
        mContext = this;

        int launchCount = Pref.getPrefInt(getApplicationContext(), Config.PREF_LAUNCH_COUNT);
        Pref.setPref(getApplicationContext(), Config.PREF_LAUNCH_COUNT, ++launchCount);

        setupReceiver();
        checkPhoneNumber();
        notifyAppMessage();
        checkPermissions();
    }

    private void setupReceiver() {
        mScreenOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new Handler().post(() -> finishActivity());
            }
        };
        registerReceiver(mScreenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        mHomeKeyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new Handler().post(() -> finishActivity());
            }
        };
        registerReceiver(mHomeKeyReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    private void checkPhoneNumber() {
        mPhoneNumber = getIntent().getStringExtra(Config.INTENT_EXTRAS_PHONENUMBER);
        if (TextUtils.isEmpty(mPhoneNumber)) {
            // 発信確認が出来ない機種の可能性あり。機能を無効にする。
            Pref.setPref(mContext, getString(R.string.PREF_CONFIRM), false);
            Pref.setPref(mContext, PREF_STATE_INVALID_TELNO, true);
            startActivity(MainActivity.createIntent(mContext, MainActivity.class));
            Utils.showToast(this, getString(R.string.error_cannot_get_phonenumber));
            finishActivity();
        }
    }

    private void initLayout() {

        binding.textViewTelno.setText(mPhoneNumber);
        ContactUtils.ContactInfo info = ContactUtils.getContactInfoByNumber(mPhoneNumber);
        if (info != null) {
            binding.textViewName.setText(info.getName());
            if (info.getPhotoBitmap() != null)
                binding.imageViewCallto.setImageBitmap(info.getPhotoBitmap());
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.inco_jump);
        binding.imageViewInco.startAnimation(animation);


        binding.buttonOk.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, true);
            String number = "tel:" + mPhoneNumber.trim();
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(number)));
            finishActivity();
        });

        binding.buttonNo.setOnClickListener(v -> {
            Utils.showToast(this, getString(R.string.message_telephone_canceled));
            Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, false);
            finishActivity();
        });

        binding.imageViewSetting.setOnClickListener(v -> {
            Utils.showToast(this, getString(R.string.message_telephone_canceled));
            Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, false);
            startActivity(MainActivity.createIntent(mContext, MainActivity.class));
            finishActivity();
        });
    }

    private void checkPermissions() {
        // 基本的にMainActivityから許可してもらわないと発信をキャッチしないので、このActivityは許可済みの状態である。
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }

        if (!hasPermissions(mContext, PERMISSIONS)) {
            if (shouldShowPermissionsDialog) {
                new MaterialDialog.Builder(this)
                        .cancelable(false)
                        .theme(Theme.LIGHT)
                        .title(getString(R.string.message_request_permissions1))
                        .content(getString(R.string.message_request_permissions2))
                        .positiveText(getString(android.R.string.ok))
                        .onPositive((dialog, which) -> {
                            shouldShowPermissionsDialog = false;
                            shouldShowRationalDialog = false;
                            requestPermissionDialog();
                        })
                        .show();
            } else {
                requestPermissionDialog();
            }
        }
    }

    private void requestPermissionDialog() {
        if (shouldShowRationalDialog && shouldShowRational(ConfirmActivity.this)) {
            shouldShowRationalDialog = false;
            showRationaleDialog(ConfirmActivity.this, mContext);
        } else {
            ActivityCompat.requestPermissions(ConfirmActivity.this, PERMISSIONS, PERMISSIONS_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults.length <= i || grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRational(ConfirmActivity.this, permissions[i])) {
                    // Never ask again
                    onNeverAskAgainSelected(mContext);
                }
            }
        }
    }

    private void notifyAppMessage() {
        final int lastNo = Pref.getPrefInt(mContext, Config.PREF_APP_MESSAGE_NO);
        int messageNo = getResources().getInteger(R.integer.APP_MESSAGE_NO);
        String messageText = getString(R.string.APP_MESSAGE_TEXT);

        if (messageNo <= lastNo) {
            return;
        }

        // インストール時点のメッセージは表示しない
        if (Pref.getPrefInt(mContext, Config.PREF_LAUNCH_COUNT) <= 1) {
            return;
        }

        CallConfirmUtils.setNotification(MainActivity.class, messageText);
        Pref.setPref(mContext, Config.PREF_APP_MESSAGE_NO, messageNo);
    }

    private void finishActivity() {
        try {
            ConfirmActivity.this.finish();
            overridePendingTransition(0, 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, false);
        finishActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        CallConfirmUtils.stopSound(soundID);

        try {
            unregisterReceiver(mHomeKeyReceiver);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            unregisterReceiver(mScreenOffReceiver);
        } catch (Exception exception1) {
            exception1.printStackTrace();
        }
    }
}
