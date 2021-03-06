package com.sakurafish.parrot.callconfirm.activity;

import android.Manifest;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.ads.AdListener;
import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.config.Config;
import com.sakurafish.parrot.callconfirm.databinding.ActivityMainBinding;
import com.sakurafish.parrot.callconfirm.fragment.MainFragment;
import com.sakurafish.parrot.callconfirm.utils.AdsHelper;
import com.sakurafish.parrot.callconfirm.utils.AlarmUtils;
import com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils;
import com.sakurafish.parrot.callconfirm.utils.Utils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.sakurafish.parrot.callconfirm.utils.AdsHelper.ACTION_BANNER_CLICK;
import static com.sakurafish.parrot.callconfirm.utils.AdsHelper.INTENT_EXTRAS_KEY_CLASS;
import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.PERMISSIONS;
import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.PERMISSIONS_REQUESTS;
import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.hasPermissions;
import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.onNeverAskAgainSelected;
import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.shouldShowRational;
import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.showRationaleDialog;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Fragment mContent;
    private Context mContext;
    private boolean shouldShowPermissionsDialog = true;
    private boolean shouldShowRationalDialog = true;
    // AdMob
    private AdsHelper adsHelper;
    private BroadcastReceiver receiverADClick;

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

        int launchCount = Pref.getPrefInt(getApplicationContext(), Config.PREF_LAUNCH_COUNT);
        Pref.setPref(getApplicationContext(), Config.PREF_LAUNCH_COUNT, ++launchCount);

        showAppMessage();
        checkPermission();
    }

    private void initLayout() {
        setupAds();
    }

    private void checkPermission() {
        if ((Build.VERSION.SDK_INT < 23) || hasPermissions(mContext, RuntimePermissionsUtils.PERMISSIONS_MUST)) {
            return;
        }

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

    private void requestPermissionDialog() {
        if (shouldShowRationalDialog && shouldShowRational(MainActivity.this)) {
            shouldShowRationalDialog = false;
            showRationaleDialog(MainActivity.this, mContext);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSIONS_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], int[] grantResults) {
        if (mContent instanceof MainFragment) {
            MainFragment mainFragment = (MainFragment) mContent;
            mainFragment.checkStatus();
        }

        if (permissions.length == 0 || grantResults.length == 0) {
            return;
        }

        for (int i = 0; i < permissions.length; i++) {
            // CALL_PHONEまたはPROCESS_OUTGOING_CALLSの権限を許可されなかった場合端末の設定画面へ誘導する
            if (permissions[i].equals(Manifest.permission.CALL_PHONE)
                    || permissions[i].equals(Manifest.permission.PROCESS_OUTGOING_CALLS)) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    onNeverAskAgainSelected(mContext);
                    break;
                }
            }
        }
    }

    private void showAppMessage() {
        scheduleNotification();
        final int lastNo = Pref.getPrefInt(mContext, Config.PREF_APP_MESSAGE_NO);
        int messageNo = getResources().getInteger(R.integer.APP_MESSAGE_NO);
        String messageText = getString(R.string.APP_MESSAGE_TEXT);

        if (messageNo <= lastNo) {
            return;
        }

        // インストール時点のメッセージは表示しない
        if (Pref.getPrefInt(mContext, Config.PREF_LAUNCH_COUNT) <= 1) {
            Pref.setPref(mContext, Config.PREF_APP_MESSAGE_NO, messageNo);
            return;
        }
        Utils.logDebug("no:" + messageNo + " message:" + messageText);
        new MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .title(getString(R.string.information))
                .content(messageText)
                .positiveText(getString(android.R.string.ok))
                .show();

        Pref.setPref(mContext, Config.PREF_APP_MESSAGE_NO, messageNo);
    }

    private void scheduleNotification() {
        AlarmUtils.unregisterAlarm(this);
        AlarmUtils.registerAlarm(this);
    }

    private void setupAds() {
        adsHelper = new AdsHelper(this);
        if (adsHelper.isIntervalOK()) {
            adsHelper.startLoad(binding.adView);
        } else {
            adsHelper.startInterval(false, MainActivity.class.getSimpleName(), binding.adView);
        }

        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdOpened() {
                adsHelper.startInterval(true, MainActivity.class.getSimpleName(), binding.adView);
            }
        });

        receiverADClick = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == null
                        || binding.adView == null
                        || !intent.getAction().equals(ACTION_BANNER_CLICK)
                        || intent.getStringExtra(INTENT_EXTRAS_KEY_CLASS).equals(MainActivity.class.getSimpleName())) {
                    return;
                }
                adsHelper.finishInterval();
                adsHelper.startInterval(false, MainActivity.class.getSimpleName(), binding.adView);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_BANNER_CLICK);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiverADClick, filter);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        getFragmentManager().putFragment(outState, "mContent", mContent);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (adsHelper != null) {
            adsHelper.finishInterval();
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiverADClick);
        mContent = null;
    }
}
