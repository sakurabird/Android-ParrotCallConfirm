package com.sakurafish.parrot.callconfirm.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sakurafish.parrot.callconfirm.Pref.Pref;
import com.sakurafish.parrot.callconfirm.R;
import com.sakurafish.parrot.callconfirm.activity.SettingActivity;
import com.sakurafish.parrot.callconfirm.activity.WebViewActivity;
import com.sakurafish.parrot.callconfirm.config.Config;
import com.sakurafish.parrot.callconfirm.config.WebConsts;
import com.sakurafish.parrot.callconfirm.databinding.FragmentMainBinding;
import com.sakurafish.parrot.callconfirm.utils.Utils;

import static com.sakurafish.parrot.callconfirm.utils.RuntimePermissionsUtils.hasPermission;


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
        init();
        initLayout();
    }

    private void init() {
        mContext = getActivity();
        checkStatus();
    }

    private void initLayout() {
        String version = Utils.getVersionName();
        binding.appVersion.setText(String.format(getString(R.string.text_app_version), version));
        binding.alertMessageView.setOnClickListener(v -> checkStatus());

        binding.menuSetting.setOnRippleCompleteListener
                (rippleView -> startActivity(SettingActivity.createIntent(mContext, SettingActivity.class)));

        binding.menuHelp.setOnRippleCompleteListener(
                rippleView -> startActivity(WebViewActivity.createIntent(mContext,
                        WebViewActivity.class,
                        WebConsts.LOCAL_HELP,
                        getString(R.string.text_help))));

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
            sendMail();
        });

        binding.menuPrivacyPolicy.setOnRippleCompleteListener
                (rippleView -> startActivity(WebViewActivity.createIntent(mContext,
                        WebViewActivity.class,
                        WebConsts.LOCAL_PRIVACY_POLICY,
                        getString(R.string.text_privacy_policy))));
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

    private void checkStatus() {
        if (Pref.getPrefInt(mContext, Config.PREF_LAUNCH_COUNT) <= 1) {
            binding.alertMessageView.setVisibility(View.GONE);
            return;
        }

        boolean hasAlert = false;
        StringBuilder message = new StringBuilder();
        message.append(getString(R.string.error_now_disabled));

        // 発信確認が設定画面で無効になっている
        if (!Pref.getPrefBool(mContext, getString(R.string.PREF_CONFIRM), true)) {
            hasAlert = true;
            message.append("\n");
            message.append(getString(R.string.error_not_activated));
        }

        // 必要なパーミッションが許可されていない
        if (Build.VERSION.SDK_INT >= 23 && !hasPermission(mContext, Manifest.permission.CALL_PHONE)) {
            hasAlert = true;
            message.append("\n");
            message.append(getString(R.string.error_no_permission_call));
        }
        if (Build.VERSION.SDK_INT >= 23 && !hasPermission(mContext, Manifest.permission.READ_CONTACTS)) {
            hasAlert = true;
            message.append("\n");
            message.append(getString(R.string.error_no_permission_contact));
        }

        // 電話番号が取得できない。機種の問題の可能性があるので機能を無効にしている。
        if (!Pref.getPrefBool(mContext, getString(R.string.PREF_CONFIRM), true)
                && Pref.getPrefBool(mContext, Config.PREF_STATE_INVALID_TELNO, false)) {
            hasAlert = true;
            message.append("\n");
            message.append(getString(R.string.error_cannot_get_phonenumber));
        }

        if (!hasAlert) {
            binding.alertMessageView.setVisibility(View.GONE);
            return;
        }
        binding.alertMessageView.setVisibility(View.VISIBLE);
        message.append(getString(R.string.error_alert_refresh));
        binding.alertMessageText.setText(message.toString());
    }

    private void sendMail() {
        String mailBody = String.format(getString(R.string.setting_mail_to_dev3),
                android.os.Build.VERSION.RELEASE, Utils.getDeviceName());

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sakurafish1@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_mail_to_dev2));
        intent.putExtra(Intent.EXTRA_TEXT, mailBody);
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}