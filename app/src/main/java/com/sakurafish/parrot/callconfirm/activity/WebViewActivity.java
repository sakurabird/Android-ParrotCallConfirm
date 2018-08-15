package com.sakurafish.parrot.callconfirm.activity;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.android.gms.ads.AdListener;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.databinding.ActivityMainBinding;
import com.sakurafish.parrot.callconfirm.fragment.WebFragment;
import com.sakurafish.parrot.callconfirm.utils.AdsHelper;

import static com.sakurafish.parrot.callconfirm.fragment.BaseWebFragment.EXTRA_TITLE;
import static com.sakurafish.parrot.callconfirm.fragment.BaseWebFragment.EXTRA_URL;
import static com.sakurafish.parrot.callconfirm.utils.AdsHelper.ACTION_BANNER_CLICK;
import static com.sakurafish.parrot.callconfirm.utils.AdsHelper.INTENT_EXTRAS_KEY_CLASS;


public class WebViewActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private Fragment mContent;
    private String mUrl;
    private String mTitle;
    // AdMob
    private AdsHelper adsHelper;
    private BroadcastReceiver receiverADClick;

    public static Intent createIntent(@NonNull final Context context,
                                      @NonNull final Class clazz,
                                      @NonNull final String url,
                                      @Nullable final String title) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_URL, url);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(EXTRA_TITLE, title);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initialize(savedInstanceState);
    }

    private void initialize(final Bundle savedInstanceState) {
        if (getIntent() != null) {
            mUrl = getIntent().getStringExtra(EXTRA_URL);
            if (getIntent().hasExtra(EXTRA_TITLE)) {
                mTitle = getIntent().getStringExtra(EXTRA_TITLE);
            }
        }

        findViewById(R.id.content).setBackgroundColor(Color.WHITE);

        if (savedInstanceState == null) {
            mContent = WebFragment.getInstance(mUrl, mTitle);
            getFragmentManager().beginTransaction()
                    .add(R.id.content, mContent)
                    .commit();
        } else {
            mContent = getFragmentManager().getFragment(savedInstanceState, "mContent");
            getFragmentManager().beginTransaction()
                    .replace(R.id.content, mContent)
                    .addToBackStack("WebFragment").commit();
        }

        setupAds();
    }

    private void setupAds() {
        adsHelper = new AdsHelper(this);
        if (adsHelper.isIntervalOK()) {
            adsHelper.startLoad(binding.adView);
        } else {
            adsHelper.startInterval(false, WebViewActivity.class.getSimpleName(), binding.adView);
        }

        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdOpened() {
                adsHelper.startInterval(true, WebViewActivity.class.getSimpleName(), binding.adView);
            }
        });

        receiverADClick = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == null
                        || binding.adView == null
                        || !intent.getAction().equals(ACTION_BANNER_CLICK)
                        || intent.getStringExtra(INTENT_EXTRAS_KEY_CLASS).equals(WebViewActivity.class.getSimpleName())) {
                    return;
                }
                adsHelper.finishInterval();
                adsHelper.startInterval(false, WebViewActivity.class.getSimpleName(), binding.adView);
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
