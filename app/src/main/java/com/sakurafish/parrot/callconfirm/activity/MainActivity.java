package com.sakurafish.parrot.callconfirm.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.gson.Gson;
import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.config.Config;
import com.sakurafish.parrot.callconfirm.databinding.ActivityMainBinding;
import com.sakurafish.parrot.callconfirm.dto.AppMessage;
import com.sakurafish.parrot.callconfirm.fragment.MainFragment;
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
import static com.sakurafish.parrot.callconfirm.utils.Utils.isJapan;
import static com.sakurafish.parrot.callconfirm.utils.Utils.logError;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Fragment mContent;
    private Context mContext;
    private boolean shouldShowPermissionsDialog = true;
    private boolean shouldShowRationalDialog = true;

    public static Intent createIntent(Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        init();

        initLayout();

        if (savedInstanceState == null) {
            mContent = MainFragment.getInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.content, mContent)
                    .commit();
        } else {
            mContent = getFragmentManager().getFragment(savedInstanceState, "mContent");
            getFragmentManager().beginTransaction()
                    .replace(R.id.content, mContent)
                    .addToBackStack("MainFragment").commit();
        }
    }

    private void init() {
        mContext = this;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        int launchCount = Pref.getPrefInt(getApplicationContext(), Config.PREF_LAUNCH_COUNT);
        Pref.setPref(getApplicationContext(), Config.PREF_LAUNCH_COUNT, ++launchCount);

        retrieveAppMessage();
        checkPermissions();
    }

    private void initLayout() {
    }

    private void checkPermissions() {
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
        if (shouldShowRationalDialog && shouldShowRational(MainActivity.this)) {
            shouldShowRationalDialog = false;
            showRationaleDialog(MainActivity.this, mContext);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSIONS_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults.length <= i || grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRational(MainActivity.this, permissions[i])) {
                    // Never ask again
                    onNeverAskAgainSelected(mContext);
                }
            }
        }
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
                    logError("retrieveAppMessage failed IOException");
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result == null) {
                    logError("retrieveAppMessage failed response==null");
                    return;
                }
//                logDebug(result);
                try {
                    Gson gson = new Gson();
                    AppMessage message = gson.fromJson(result, AppMessage.class);
                    showAppMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    logError("Json parse error");
                }
            }
        }.execute();
    }

    private void showAppMessage(AppMessage message) {
        final int lastNo = Pref.getPrefInt(mContext, Config.PREF_APP_MESSAGE_NO);

        for (AppMessage.Data data : message.getData()) {
            int messageNo = data.getMessage_no();
            if (data.getApp().equals("ParrotCallConfirm") && messageNo > lastNo &&
                    data.getVersion() == Utils.getVersionCode()) {
                String msg = isJapan() ? data.getMessage_jp() : data.getMessage_en();
//                logDebug("no:" + data.getMessage_no() + " message:" + msg);

                // インストール時点のメッセージは表示しない
                if (Pref.getPrefInt(mContext, Config.PREF_LAUNCH_COUNT) <= 1) {
                    Pref.setPref(mContext, Config.PREF_APP_MESSAGE_NO, messageNo);
                    return;
                }

                new MaterialDialog.Builder(this)
                        .theme(Theme.LIGHT)
                        .title("APP_MESSAGE")
                        .content(msg)
                        .positiveText(getString(android.R.string.ok))
                        .show();

                ++messageNo;
                Pref.setPref(mContext, Config.PREF_APP_MESSAGE_NO, messageNo);
                break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        getFragmentManager().putFragment(outState, "mContent", mContent);
        super.onSaveInstanceState(outState);
    }

    private void finalizeLayout() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finalizeLayout();
        mContent = null;
    }
}
