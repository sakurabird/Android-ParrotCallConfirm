package com.sakurafish.parrot.callconfirm.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andexert.library.RippleView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.activity.CreditActivity;
import com.sakurafish.parrot.callconfirm.activity.SettingActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sakura on 2015/01/23.
 */
public class MainFragment extends Fragment {

    private Context mContext;
    @Bind(R.id.menu_setting)
    RippleView mButtonSetting;
    @Bind(R.id.menu_share)
    RippleView mButtonShare;
    @Bind(R.id.menu_googleplay)
    RippleView mButtonGooglePlay;
    @Bind(R.id.menu_credit)
    RippleView mButtonCredit;
    @Bind(R.id.menu_mail_to_dev)
    RippleView mButtonMainToDev;
    @Bind(R.id.adView)
    AdView mAdView;

    public static MainFragment getInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initLayout();
    }

    private void initLayout() {
        mButtonSetting.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                startActivity(SettingActivity.createIntent(mContext, SettingActivity.class));
            }
        });

        mButtonShare.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                final Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
                intent.setType("text/plain");
                startActivity(intent);
            }
        });

        mButtonGooglePlay.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.share_text))));
            }
        });

        mButtonCredit.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                startActivity(CreditActivity.createIntent(mContext, CreditActivity.class));
            }
        });

        mButtonMainToDev.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sakurafish1@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_mail_to_dev2));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.setting_mail_to_dev3));
                startActivity(intent);
            }
        });


        // show AD banner
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    private void finalizeLayout() {
        mButtonSetting = null;
        mButtonGooglePlay = null;
        mButtonCredit = null;
        mButtonShare = null;
        mButtonMainToDev = null;
        if (mAdView != null) {
            mAdView.destroy();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        finalizeLayout();
        mContext = null;
    }
}