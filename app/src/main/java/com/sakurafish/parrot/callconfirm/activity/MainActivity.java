package com.sakurafish.parrot.callconfirm.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.ads.AdRequest;
import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.config.Config;
import com.sakurafish.parrot.callconfirm.databinding.ActivityMainBinding;
import com.sakurafish.parrot.callconfirm.fragment.MainFragment;
import com.sakurafish.parrot.callconfirm.utils.AdsHelper;
import com.sakurafish.parrot.callconfirm.utils.Utils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
        checkPermissions();
    }

    private void initLayout() {

        // show AD banner
        AdRequest adRequest = new AdsHelper(mContext).getAdRequest();
        binding.adView.loadAd(adRequest);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], int[] grantResults) {
        if (mContent instanceof MainFragment) {
            MainFragment mainFragment = (MainFragment) mContent;
            mainFragment.checkStatus();
        }
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults.length <= i || grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRational(MainActivity.this, permissions[i])) {
                    // Never ask again
                    onNeverAskAgainSelected(mContext);
                }
            }
        }
    }

    private void showAppMessage() {
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
