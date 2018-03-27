package com.sakurafish.parrot.callconfirm.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.gson.Gson;
import com.sakurafish.parrot.callconfirm.MyApplication;
import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.config.Config;
import com.sakurafish.parrot.callconfirm.databinding.ActivityConfirmBinding;
import com.sakurafish.parrot.callconfirm.dto.AppMessage;
import com.sakurafish.parrot.callconfirm.utils.CallConfirmUtils;
import com.sakurafish.parrot.callconfirm.utils.ContactUtils;
import com.sakurafish.parrot.callconfirm.utils.SoundManager;
import com.sakurafish.parrot.callconfirm.utils.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
    private boolean hasVolumeChanged;
    private int mOriginalVolume;

    private boolean shouldShowPermissionsDialog = true;
    private boolean shouldShowRationalDialog = true;

    public static Intent createIntent(@NonNull final Context context,
                                      @NonNull final Class clazz,
                                      @NonNull final String phoneNumber) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    private void init() {
        mContext = this;

        checkPhoneNumber();
        retrieveAppMessage();
        checkPermissions();
    }

    private void initLayout() {
        if (Pref.getPrefBool(mContext, getString(R.string.PREF_VIBRATE), true)) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(1000);
            }
        }
        if (Pref.getPrefBool(mContext, getString(R.string.PREF_SOUND_ON), true)) {
            String s = Pref.getPrefString(mContext, getString(R.string.PREF_SOUND));
            if (s == null) s = "0";
            int idx = 0;
            try {
                idx = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                Utils.logError(e.getLocalizedMessage());
            }
            if (Pref.isExistKey(mContext, getString(R.string.PREF_SOUND_VOLUME))) {
                hasVolumeChanged = true;
                mOriginalVolume = SoundManager.getOriginalVolume();
                AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                int newVolume = Pref.getPrefInt(mContext, mContext.getString(R.string.PREF_SOUND_VOLUME));
                if (am != null) {
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
                }
            } else {
                hasVolumeChanged = false;
            }
            MyApplication.getSoundManager().play(MyApplication.getSoundIds()[idx]);
        }

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
            ConfirmActivity.this.finish();
        });

        binding.buttonNo.setOnClickListener(v -> {
            Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, false);
            ConfirmActivity.this.finish();
        });

        binding.imageViewSetting.setOnClickListener(v -> {
            Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, false);
            startActivity(MainActivity.createIntent(mContext, MainActivity.class));
            ConfirmActivity.this.finish();
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

    private void checkPhoneNumber() {
        mPhoneNumber = getIntent().getStringExtra(Config.INTENT_EXTRAS_PHONENUMBER);
        if (!TextUtils.isEmpty(mPhoneNumber)) {
            return;
        }

        // 発信確認が出来ない機種の可能性あり。機能を無効にする。
        Pref.setPref(mContext, getString(R.string.PREF_CONFIRM), false);

        new MaterialDialog.Builder(this)
                .cancelable(false)
                .theme(Theme.LIGHT)
                .title(getString(R.string.error_cannot_get_phonenumber_title))
                .content(getString(R.string.error_cannot_get_phonenumber))
                .positiveText(getString(android.R.string.ok))
                .onPositive((dialog, which) -> ConfirmActivity.this.finish())
                .show();
    }

    private void retrieveAppMessage() {
        final String url = getString(R.string.URL_APP_MESSAGE);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String result = null;
                Request request = new Request.Builder().url(url).get().build();
                OkHttpClient client = new OkHttpClient();
                try {
                    Response response = client.newCall(request).execute();
                    result = response.body().string();
                } catch (IOException e) {
                    Utils.logError("retrieveAppMessage failed IOException");
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result == null) {
                    Utils.logError("retrieveAppMessage failed response==null");
                    return;
                }
                Utils.logDebug(result);
                try {
                    Gson gson = new Gson();
                    AppMessage message = gson.fromJson(result, AppMessage.class);
                    setNotification(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.logError("Json parse error");
                }
            }
        }.execute();
    }

    private void setNotification(AppMessage message) {
        final int lastNo = Pref.getPrefInt(mContext, Config.PREF_APP_MESSAGE_NO);
        Utils.logDebug("last message no:" + lastNo);

        for (AppMessage.Data data : message.getData()) {
            int messageNo = data.getMessage_no();
            if (data.getApp().equals("ParrotCallConfirm") && messageNo > lastNo &&
                    data.getVersion() == Utils.getVersionCode()) {
                String msg = Utils.isJapan() ? data.getMessage_jp() : data.getMessage_en();
                Utils.logDebug("no:" + data.getMessage_no() + " message:" + msg);
                CallConfirmUtils.setNotification(MainActivity.class, msg);
                messageNo++;
                Pref.setPref(mContext, Config.PREF_APP_MESSAGE_NO, messageNo);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Pref.setPref(mContext, Config.PREF_AFTER_CONFIRM, false);
        ConfirmActivity.this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (hasVolumeChanged) {
            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, mOriginalVolume, 0);
            }
        }
        mContext = null;
        mPhoneNumber = null;
    }

}
