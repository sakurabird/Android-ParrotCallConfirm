package com.sakurafish.parrot.callconfirm.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.activity.SettingActivity;
import com.sakurafish.parrot.callconfirm.activity.WebViewActivity;
import com.sakurafish.parrot.callconfirm.databinding.FragmentMainBinding;
import com.sakurafish.parrot.callconfirm.utils.AdsHelper;
import com.sakurafish.parrot.callconfirm.web.WebConsts;


/**
 * Created by sakura on 2015/01/23.
 */
public class MainFragment extends Fragment {

    FragmentMainBinding binding;

    private Context mContext;

    public static MainFragment getInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initLayout();
    }

    private void initLayout() {
        binding.menuSetting.setOnRippleCompleteListener
                (rippleView -> startActivity(SettingActivity.createIntent(mContext, SettingActivity.class)));

        binding.menuShare.setOnRippleCompleteListener(rippleView -> {
            final Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
            intent.setType("text/plain");
            startActivity(intent);
        });

        binding.menuGoogleplay.setOnRippleCompleteListener
                (rippleView -> startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.share_text)))));

        binding.menuCredit.setOnRippleCompleteListener(
                rippleView -> startActivity(WebViewActivity.createIntent(mContext,
                WebViewActivity.class,
                WebConsts.LOCAL_CREDIT,
                getString(R.string.text_credit))));

        binding.menuMailToDev.setOnRippleCompleteListener(rippleView -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sakurafish1@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_mail_to_dev2));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.setting_mail_to_dev3));
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        binding.menuPrivacyPolicy.setOnRippleCompleteListener
                (rippleView -> startActivity(WebViewActivity.createIntent(mContext,
                WebViewActivity.class,
                WebConsts.LOCAL_PRIVACY_POLICY,
                getString(R.string.text_privacy_policy))));

        // show AD banner
        AdRequest adRequest = new AdsHelper(mContext).getAdRequest();
        binding.adView.loadAd(adRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding.adView != null) {
            binding.adView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding.adView != null) {
            binding.adView.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mContext = null;
    }
}